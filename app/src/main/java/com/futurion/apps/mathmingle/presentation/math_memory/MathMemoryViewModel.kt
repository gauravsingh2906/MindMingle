package com.futurion.apps.mathmingle.presentation.math_memory

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futurion.apps.mathmingle.domain.LevelManager
import com.futurion.apps.mathmingle.domain.model.AnswerOption
import com.futurion.apps.mathmingle.domain.model.MemoryCard
import com.futurion.apps.mathmingle.domain.model.MemoryLevel
import com.futurion.apps.mathmingle.domain.model.Operations
import com.futurion.apps.mathmingle.domain.repository.StatsRepository
import com.futurion.apps.mathmingle.domain.state.MathMemoryGameUIState
import com.futurion.apps.mathmingle.presentation.games.SampleGames.Default
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Random
import javax.inject.Inject
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

    private val _totalXp = MutableStateFlow(0)
    val totalXp: StateFlow<Int> = _totalXp

    private val _totalCoins = MutableStateFlow(0)
    val totalCoins: StateFlow<Int> = _totalCoins


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

    fun generateMemoryLevel(levelNumber: Int): MemoryLevel {
        val numCards = minOf(2 + levelNumber / 2, 8)

        // Max answer grows with level
        val maxAnswer = when {
            levelNumber <= 5 -> 50
            levelNumber <= 10 -> 100
            levelNumber <= 15 -> 150
            else -> 200
        }

        val weightedOps = when {
            levelNumber < 5 -> listOf(Operations.ADD)
            levelNumber < 10 -> listOf(Operations.ADD, Operations.SUB)
            levelNumber < 15 -> listOf(Operations.ADD, Operations.SUB, Operations.MUL, Operations.MUL)
            else -> Operations.entries.toList()
        }

        val cardValuesRangeAddSub = if (levelNumber < 5) 1..5 else 1..9
        val cardValuesRangeMul = when {
            levelNumber < 10 -> listOf(2)
            levelNumber < 15 -> (2..3).toList()
            else -> (2..4).toList()
        }

        val cards = mutableListOf<MemoryCard>()
        var currentValue = (0..10).random().coerceAtMost(maxAnswer)
        val start = currentValue

        repeat(numCards) {
            val op = weightedOps.random()
            val value = when (op) {
                Operations.ADD -> cardValuesRangeAddSub.filter { it + currentValue <= maxAnswer }.randomOrNull() ?: 1
                Operations.SUB -> cardValuesRangeAddSub.filter { it <= currentValue }.randomOrNull() ?: 1
                Operations.MUL -> cardValuesRangeMul.filter { it * currentValue <= maxAnswer }.randomOrNull() ?: 1
                Operations.DIV -> (2..4).filter { it != 0 && currentValue / it <= maxAnswer }.randomOrNull() ?: 1
            }

            cards.add(MemoryCard(op, value))

            currentValue = when (op) {
                Operations.ADD -> currentValue + value
                Operations.SUB -> currentValue - value
                Operations.MUL -> currentValue * value
                Operations.DIV -> if (value != 0) currentValue / value else currentValue
            }

            currentValue = currentValue.coerceIn(0, maxAnswer)
        }

        return MemoryLevel(levelNumber, cards, start, maxAnswer)
    }

    fun loadOrInitMathMemoryLevel() {
        viewModelScope.launch {
            val userId = statsRepo.initUserIfNeeded()
            Log.d("MathMemoryVM", "Loading user progress for $userId")
            val profile = statsRepo.getProfile(userId)

            _totalXp.value = profile?.currentLevelXP ?: 0
            _totalCoins.value = profile?.coins ?: 0
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
        val maxOffset = 20 // maximum difference for distractors
        var attempts = 0

        while (options.size < 4 && attempts < 50) {
            val offset = rand.nextInt(maxOffset) + 1
            val candidate = if (rand.nextBoolean()) correct + offset else correct - offset

            // Only include candidate if within -200..200 and not already in options
            if (candidate != correct && candidate in -200..200) {
                options.add(candidate)
            }
            attempts++
        }

        // Fallback loop to fill any missing options
        var fallback = correct - maxOffset
        while (options.size < 4) {
            if (fallback != correct && fallback in -200..200) {
                options.add(fallback)
            }
            fallback++
        }

        return options.shuffled().map { AnswerOption(it, it == correct) }
    }




//    private fun generateAnswerOptions(correct: Int): List<AnswerOption> {
//        val cappedCorrect = correct.coerceAtMost(200)
//        val options = mutableSetOf(cappedCorrect)
//        val rand = Random()
//        val maxOffset = maxOf(3, abs(cappedCorrect) / 3)
//        var attempts = 0
//
//        while (options.size < 4 && attempts < 25) {
//            val offset = rand.nextInt(maxOffset) + 1
//            val candidate = if (rand.nextBoolean()) cappedCorrect + offset else cappedCorrect - offset
//            val cappedCandidate = candidate.coerceIn(0, 200) // Cap options between 0 and 200
//            if (cappedCandidate != cappedCorrect) options.add(cappedCandidate)
//            attempts++
//        }
//
//        var fallbackValue = if (cappedCorrect > 4) cappedCorrect - 4 else 1
//        while (options.size < 4) {
//            val cappedFallback = fallbackValue.coerceIn(0, 200)
//            if (cappedFallback != cappedCorrect && cappedFallback !in options) options.add(cappedFallback)
//            fallbackValue++
//        }
//
//        return options.shuffled().map { AnswerOption(it, it == cappedCorrect) }
//    }


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
                    log("Moved to next level: ${newLevel}")
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
            xpGained =if (isCorrect) xpEarned else 5,
            hintsUsed = hintsUsed,
            timeSpentSeconds = timeSpentSeconds,
            coinsEarned = coinsEarned,
            currentStreak = 1,
            bestStreak = bestStreak,
            eachGameXp = if (isCorrect) xpEarned else 5,
            eachGameCoin = 12,
            resultTitle = "Congratulations!",
            resultMessage = "fds",
            isMatchWon = isCorrect,
            mathMemoryLevel = newCurrentLevel,
            mathMemoryHighestLevel = newHighestLevel
        )
        val profile = statsRepo.getProfile(userId)
        _totalXp.value = profile?.currentLevelXP ?: _totalXp.value
        _totalCoins.value = profile?.coins ?: _totalCoins.value
        log("Game result updated for user $userId at level $levelNum")
    }

    fun startMemorizationTimer(delayMs: Long) {
        viewModelScope.launch {
            delay(delayMs)
            onAction(MathMemoryAction.RevealCards)
        }
    }

    private val _remainingTime = MutableStateFlow(0)
    val remainingTime: StateFlow<Int> = _remainingTime

    fun startMemorizationTimer1(delayMs: Long) {
        viewModelScope.launch {
            val totalSeconds = (delayMs / 1000).toInt()
            _remainingTime.value = totalSeconds

            repeat(totalSeconds) {
                delay(1000)
                _remainingTime.value = totalSeconds - (it + 1)
            }

            onAction(MathMemoryAction.RevealCards)
        }
    }

    fun getMemorizationTime(levelNumber: Int, numCards: Int): Long {
        val baseTime = 2000L          // 2 seconds minimum
        val perCardTime = 500L        // additional time per card
        val difficultyFactor = 1 + levelNumber / 10f // higher levels slightly slower
        return ((baseTime + numCards * perCardTime) * difficultyFactor).toLong()
    }



    fun useHint() {
        _hintsUsed.value += 1
    }

    fun getXpForLevel(level: Int): Int = 20 + ((level - 1) / 5) * 2

    private fun log(message: String) {
        Log.d("MathMemoryVM", message)
    }

}