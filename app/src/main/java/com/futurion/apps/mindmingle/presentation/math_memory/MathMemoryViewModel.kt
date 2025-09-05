package com.futurion.apps.mindmingle.presentation.math_memory

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futurion.apps.mindmingle.domain.LevelManager
import com.futurion.apps.mindmingle.domain.model.AnswerOption
import com.futurion.apps.mindmingle.domain.model.MemoryLevel
import com.futurion.apps.mindmingle.domain.repository.StatsRepository
import com.futurion.apps.mindmingle.domain.state.MathMemoryGameUIState
import com.futurion.apps.mindmingle.presentation.games.SampleGames.Default
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Random
import javax.inject.Inject
import kotlin.collections.get
import kotlin.compareTo
import kotlin.math.abs
import kotlin.math.max

@HiltViewModel
class MathMemoryViewModel @Inject constructor(
    private val levelManager: LevelManager,
    private val statsRepo: StatsRepository
): ViewModel() {

    private val _uiState = mutableStateOf(
        MathMemoryGameUIState(game = MathMemoryGameState(levelManager.currentLevel()))
    )
    val uiState: State<MathMemoryGameUIState> = _uiState

    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak


    private val _bestStreak = MutableStateFlow(0)
    val bestStreak: StateFlow<Int> = _bestStreak

    private val _hintsUsed = MutableStateFlow(0)
    val hintsUsed: StateFlow<Int> = _hintsUsed

    var selectedTheme by mutableStateOf(Default.first())
        private set
    var unlockedThemes by mutableStateOf(setOf(Default.first().name))
        private set

    private val _answerOptions = mutableStateOf<List<AnswerOption>>(emptyList())
    val answerOptions: State<List<AnswerOption>> = _answerOptions

    var showUnlockAnimation by mutableStateOf(false)
        private set
    var newlyUnlockedThemeName by mutableStateOf<String?>(null)
        private set

    init {
        setNewLevel(levelManager.currentLevel())

    }

    fun loadOrInitMathMemoryLevel() {
        viewModelScope.launch {
            val userId = statsRepo.initUserIfNeeded()
            val profile = statsRepo.getProfile(userId)

            // Get the theme name from profile selectedThemeName or fallback to default

            val savedLevel = profile?.mathMemoryCurrentLevel?.takeIf { it > 0 } ?: 1
            Log.d("MathMemoryVM", "Loading user progress for $userId, start level: $savedLevel")
            levelManager.setLevel(savedLevel)
            setNewLevel(levelManager.currentLevel())

            unlockedThemes = profile?.unlockedThemes?.toSet() ?: setOf(Default[0].name)
            val selectedThemeName = profile?.selectedThemeName ?: Default[0].name
            val themeFromProfile =
                Default.firstOrNull { it.name == selectedThemeName } ?: Default[0]
            selectedTheme = themeFromProfile

            _uiState.value = _uiState.value.copy(
                theme = _uiState.value.theme.copy(
                    unlockedThemes = unlockedThemes,
                    selectedTheme = selectedTheme
                )
            )

//            applyThemeForLevel(
//                levelManager.currentLevel().number,
//                showAnimationIfNew = false
//            )
        }
    }

    private fun setNewLevel(level: MemoryLevel) {
        _uiState.value = _uiState.value.copy(game = MathMemoryGameState(level))
        _answerOptions.value = generateAnswerOptions(level.correctAnswer)
    }

    private fun maybeUpdateLevels(userId: String, finishedLevel: Int) {
        viewModelScope.launch {
            val nextLevel = finishedLevel + 1
            statsRepo.updateMathMemoryCurrentLevel(userId, nextLevel)
            statsRepo.updateMathMemoryHighestLevel(userId, finishedLevel)
        }
    }

    private fun generateAnswerOptions(correct: Int): List<AnswerOption> {
        val options = mutableSetOf(correct)
        val rand = Random()
        val maxOffset = maxOf(3, abs(correct) / 3)
        var attempts = 0

        while (options.size < 4 && attempts < 25) {
            val offset = rand.nextInt(maxOffset) + 1
            val candidate = if (rand.nextBoolean()) correct + offset else correct - offset
            if (candidate != correct) options.add(candidate)
            attempts++
        }

        var fallbackValue = if (correct > 4) correct - 4 else 1
        while (options.size < 4) {
            if (fallbackValue != correct && fallbackValue !in options) options.add(fallbackValue)
            fallbackValue++
        }
        return options.shuffled().map { AnswerOption(it, it == correct) }
    }

    fun onAction(action: MathMemoryAction) {
        when (action) {
            is MathMemoryAction.RevealCards -> {
                _uiState.value = _uiState.value.copy(
                    game = _uiState.value.game.copy(isShowCards = false)
                )
            }

            is MathMemoryAction.InputChanged -> {
                _uiState.value = _uiState.value.copy(
                    game = _uiState.value.game.copy(userInput = action.value)
                )
            }

            is MathMemoryAction.SubmitAnswer -> {
                val isCorrect = _uiState.value.game.userInput.toIntOrNull() ==
                        _uiState.value.game.level.correctAnswer

                _currentStreak.value = if (isCorrect) _currentStreak.value + 1 else 0
                _bestStreak.value = maxOf(_bestStreak.value, _currentStreak.value)

                // Show the RESULT phase here (do NOT change level here)
                _uiState.value = _uiState.value.copy(
                    game = _uiState.value.game.copy(showResult = true, isCorrect = isCorrect)
                )
            }

            is MathMemoryAction.NextLevel -> {
                // Advance level only when user decides to proceed from the RESULT screen
                val completedLevel = levelManager.currentLevel().number
                log("User passed level $completedLevel, updating progress")

                if (_uiState.value.game.isCorrect) {

                    viewModelScope.launch {
                        val userId = statsRepo.initUserIfNeeded()
                        // Save next level as current to resume from here
                        maybeUpdateLevels(userId = userId, finishedLevel = completedLevel)
                    }
                    // Save the highest level reached


                    levelManager.nextLevel()
                    val newLevel = levelManager.currentLevel()
                    log("Moved to next level: ${newLevel.number}")
                    setNewLevel(newLevel)

                    // If we just entered a new 5-level block (i.e., a boundary at 6, 11, 16, ...)
                    // then update theme & (optionally) show unlock animation.
//                    val enteringNewBlock = ((newLevel.number - 1) % 5 == 0) && newLevel.number != 1
//                    applyThemeForLevel(newLevel.number, showAnimationIfNew = enteringNewBlock)
                }
            }

            is MathMemoryAction.ResetGame -> {
              //  levelManager.reset()
                val first = levelManager.currentLevel()
                setNewLevel(first)

                // Reset theme to the first block theme
                //  val baseTheme = themeForLevel(1)
                //     val baseUnlocked = setOf(baseTheme.name)
//                _uiState.value = _uiState.value.copy(
//                    theme = ThemeState(unlockedThemes = baseUnlocked, selectedTheme = baseTheme)
//                )
                //    selectedTheme = baseTheme
                //   unlockedThemes = baseUnlocked
                showUnlockAnimation = false
                newlyUnlockedThemeName = null
            }

            is MathMemoryAction.SelectTheme -> {
                // Manual theme selection, allowed only if already unlocked
                if (action.theme.name in unlockedThemes) {
                    selectedTheme = action.theme
                    _uiState.value = _uiState.value.copy(
                        theme = _uiState.value.theme.copy(selectedTheme = action.theme)
                    )
                }

            }

            is MathMemoryAction.UnlockNextTheme -> {
                // Manual unlock of the next theme (if you keep this button/flow)
                val currentUnlocked = _uiState.value.theme.unlockedThemes
                val next = Default.firstOrNull { it.name !in currentUnlocked }
                if (next != null) {
                    val updated = currentUnlocked + next.name
                    _uiState.value = _uiState.value.copy(
                        theme = _uiState.value.theme.copy(
                            unlockedThemes = updated,
                            selectedTheme = next
                        )
                    )
                    unlockedThemes = updated
                    selectedTheme = next
                    newlyUnlockedThemeName = next.name
                    showUnlockAnimation = true
                }
            }

            MathMemoryAction.HideCards -> {
                _uiState.value = _uiState.value.copy(
                    game = _uiState.value.game.copy(isShowCards = true)
                )
            }
        }
    }


    fun onLevelResultAndSaveStats(
        userId: String,
        isCorrect: Boolean,
        coinsEarned: Int,
        hintsUsed: Int,
        currentStreak: Int,
        bestStreak: Int,
        timeSpentSeconds: Long
    ) = viewModelScope.launch {
        val levelNum = _uiState.value.game.level.number
        log("Recording result for level $levelNum, success=$isCorrect")
        val xpEarned = getXpForLevel(levelNum)

//        val profile = statsRepo.getProfile(userId)
//        val savedHighest = profile?.mathMemoryHighestLevel ?: 0
//        val newHighest = maxOf(savedHighest, if (isCorrect) levelNum else 0)
//        if (isCorrect) {
//            _currentStreak.value = _currentStreak.value +1
//        } else {
//            _currentStreak.value =0
//        }

//        _currentStreak.value +=1
//        if (_currentStreak.value > _bestStreak.value) {
//            _bestStreak.value = _currentStreak.value
//        }

        // Keep current level if wrong, update only if correct
        val newCurrentLevel =
            if (isCorrect) levelNum else statsRepo.getProfile(userId)?.mathMemoryCurrentLevel ?: 1
        val newHighestLevel =
            if (isCorrect) max(levelNum, statsRepo.getProfile(userId)?.mathMemoryHighestLevel ?: 0)
            else statsRepo.getProfile(userId)?.mathMemoryHighestLevel ?: 0


     //   _currentStreak.value = if (isCorrect) _currentStreak.value + 1 else 0
        Log.d("MathStats", "Current Streak: ${_currentStreak.value}")
   //     _bestStreak.value = maxOf(_bestStreak.value, _currentStreak.value)

//        if (isCorrect) {
//            maybeUpdateHighestLevel(userId,levelNum)
//        }



        // Save only; DO NOT change level here. Let NextLevel action do it.
        statsRepo.updateGameResult(
            userId = userId,
            gameName = "math_memory",
            levelReached = levelNum,
            won = isCorrect,
            xpGained = xpEarned,
            hintsUsed = hintsUsed,
            timeSpentSeconds = timeSpentSeconds,
            coinsEarned = coinsEarned,
            currentStreak = 1,
            bestStreak = bestStreak,
            eachGameXp = xpEarned,
            eachGameCoin = 12,
            resultTitle = "Congratulations!",
            resultMessage = "fds",
            isMatchWon = isCorrect,
            mathMemoryLevel = newCurrentLevel,
            mathMemoryHighestLevel = newHighestLevel
        )
        log("Game result updated for user $userId at level $levelNum")
    }

    fun startMemorizationTimer(delayMs: Long) {
        viewModelScope.launch {
            delay(delayMs)
            onAction(MathMemoryAction.RevealCards)
        }
    }

    fun useHint() {
        _hintsUsed.value += 1
    }

    fun getXpForLevel(level: Int): Int = 10 + ((level - 1) / 5) * 2

    private fun log(message: String) {
        Log.d("MathMemoryVM", message)
    }

}