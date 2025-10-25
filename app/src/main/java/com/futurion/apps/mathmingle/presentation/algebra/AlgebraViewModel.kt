package com.futurion.apps.mathmingle.presentation.algebra


import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futurion.apps.mathmingle.domain.repository.LevelRepository
import com.futurion.apps.mathmingle.domain.repository.StatsRepository
import com.futurion.apps.mathmingle.domain.GameManager
import com.futurion.apps.mathmingle.domain.model.GameResult
import com.futurion.apps.mathmingle.domain.LevelConfig
import com.futurion.apps.mathmingle.domain.model.Question
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlgebraViewModel @Inject constructor(
    private val levelRepository: LevelRepository,
    private val statsRepository: StatsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    //  val userId = savedStateHandle.toRoute<>()

    private val manager = GameManager()

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()

    private val _level = MutableStateFlow(1)
    val level: StateFlow<Int> = _level

    private val _question = MutableStateFlow<Question?>(manager.nextQuestion(1))
    val question: StateFlow<Question?> = _question

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score

    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak

    private val _bestStreak = MutableStateFlow(0)
    val bestStreak: StateFlow<Int> = _bestStreak

    private val _hintsUsed = MutableStateFlow(0)
    val hintsUsed: StateFlow<Int> = _hintsUsed

    private val _bestScore = MutableStateFlow(0)
    val bestScore: StateFlow<Int> = _bestScore

    private val _coinsEarned = MutableStateFlow(0)
    val coinsEarned: StateFlow<Int> = _coinsEarned

    private val _gameOver = MutableStateFlow(false)
    val gameOver: StateFlow<Boolean> = _gameOver

    private val _time = MutableStateFlow(0)
    val timePlayed: StateFlow<Int> = _time



    private val _levelCompleted = MutableStateFlow(false)
    val levelCompleted: StateFlow<Boolean> = _levelCompleted

    private val _timeRemaining = MutableStateFlow(0)
    val timeRemaining: StateFlow<Int> = _timeRemaining

    val totalSeconds = LevelConfig(_level.value).timeLimitSeconds()

//    private val _timeLeft = mutableStateOf(config.timeLimitSeconds())
//    val timeLeft: State<Int> = _timeLeft

    private var questionStartTime: Long = 0L
    private var timerJob: Job? = null

    private val _maxUnlockedLevel = MutableStateFlow(1)
    val maxUnlockedLevel: StateFlow<Int> = _maxUnlockedLevel

    private val _gameResult = MutableStateFlow<GameResult?>(null)
    val gameResult: StateFlow<GameResult?> = _gameResult

    private var isTimerPaused = false

    private val rewardLevels: Map<Int, Int> = mapOf(
        5 to 20,   // 20 coins at level 5
        10 to 40,  // 40 coins at level 10
        18 to 60,   // 60 coins at level 18
        25 to 80,
        30 to 100
    )


    private var currentLevel = 1

    fun setLevel(level: Int) {
        currentLevel = level
        Log.d("Level", "Level set to $level")
        Log.d("Level Current", "Level set to $currentLevel")
        _level.value = level
        Log.d("Level Value", "Level value:${_level.value}")
    }


    //happen on click
    fun markLevelCompleted() {
        _levelCompleted.value = true
        unlockNextLevelIfNeeded()
    }

    // mark on complete
    private fun unlockNextLevelIfNeeded() {
        viewModelScope.launch {
            val nextLevel = currentLevel + 1
            levelRepository.unlockNextLevelIfNeeded(currentLevel, gameId = "algebra")
            _maxUnlockedLevel.value = nextLevel  // update local cache
            Log.d("Level", "Unlocked next level: $nextLevel")
        }
    }

    init {
        viewModelScope.launch {
            levelRepository.ensureInitialized(gameId = "algebra")
            levelRepository.getMaxUnlockedLevelOnce(gameId = "algebra").collect { level ->
                _maxUnlockedLevel.value = level
                Log.d("Level", "Collected max unlocked level = $level")
            }
        }
    }


    fun startGame() {

        _score.value = 0
        _level.value = currentLevel
        _currentStreak.value = 0
        _hintsUsed.value = 0
        _gameOver.value = false
        startNext()
    }


    //  old one but important
    fun startNext() {
        val q = manager.nextQuestion(_level.value)
        _question.value = q
        questionStartTime = System.currentTimeMillis()

        startTimer(LevelConfig(_level.value).timeLimitSeconds())
    }

    fun startTimer(seconds: Int) {
        timerJob?.cancel()
        _timeRemaining.value = seconds
        viewModelScope.launch {
            _userId.value = statsRepository.initUserIfNeeded()
        }
        timerJob = viewModelScope.launch {
            while (_timeRemaining.value > 0) {
                delay(1000)

                if (_gameOver.value) break
                if (isTimerPaused) continue // skip countdown while paused

                _timeRemaining.value--
            }
            if (_timeRemaining.value <= 0 && !_gameOver.value) {
                Log.d("Time", "Time is up")
                endGame(timeout = true)
            }

        }
    }

    private fun onTimeOver() {
        // Handle time over - game over or next question
    }

    fun pauseTimer() {
        isTimerPaused = true
        Log.d("Timer", "Paused at ${_timeRemaining.value} seconds")
    }

    fun resumeTimer() {
        if (_gameOver.value) return

        isTimerPaused = false
        Log.d("Timer", "Resumed with ${_timeRemaining.value} seconds left")
    }



    fun submitAnswer(userAnswer: Any?) {
        if (_gameOver.value) return

        val q = _question.value ?: return
        val timeSec = (System.currentTimeMillis() - questionStartTime) / 1000L
        Log.d("Time", "Time taken: $timeSec")

        val correct = when (q) {
            is Question.MissingNumber -> (userAnswer as? Int) == q.answer
            is Question.MissingOperator -> (userAnswer as? Char) == q.answer
            is Question.TrueFalse -> (userAnswer as? Boolean) == q.isCorrect
            is Question.Reverse -> (userAnswer as? Char) == q.answer
            is Question.Mix -> {
                val inner = q.inner
                when (inner) {
                    is Question.MissingNumber -> (userAnswer as? Int) == inner.answer
                    is Question.MissingOperator -> (userAnswer as? Char) == inner.answer
                    is Question.TrueFalse -> (userAnswer as? Boolean) == inner.isCorrect
                    is Question.Reverse -> (userAnswer as? Char) == inner.answer
                    else -> false
                }
            }
        }

        val xp = calculateXp(correct, currentLevel)
        //   _time.value += timeSec.toInt() // add time spent

        if (correct) {
            _score.value += xp
//            _currentStreak.value += 1
//            if (_currentStreak.value > _bestStreak.value) {
//                _bestStreak.value = _currentStreak.value
//            }
        } else {
            // _currentStreak.value = 0
        }

        // Save to repository
//        viewModelScope.launch {
//            statsRepository.updateGameResult(
//                userId = statsRepository.initUserIfNeeded(),
//                gameName = "algebra",
//                levelReached = currentLevel,
//                won = false,
//                xpGained = 0,
//                hintsUsed = 0,
//                timeSpentSeconds = timeSec
//            )
//        }

        val time = timeSec / 60

        _time.value = _time.value + timeSec.toInt() // total time in seconds

//        viewModelScope.launch {
//            dailyMissionRepository.updateMissionProgress(
//                gameName = "algebra",
//                minutesPlayed = _time.value
//            )
//        }
        viewModelScope.launch {
            _userId.value = statsRepository.initUserIfNeeded()
            val perGameStats = statsRepository.getPerGameStats(_userId.value ?: "1","algebra")
            Log.d("Latest-Id", _userId.value ?: "fake")
        }



        // If game over (wrong answer or time out)
        if (!correct) {
            _gameResult.value = GameResult(
                level = currentLevel,
                won = false,
                xpEarned = xp,
                score = _score.value,
                streak = 0,
                bestStreak = _bestStreak.value,
                hintsUsed = _hintsUsed.value,
                timeSpent = _time.value.toLong(),
            )

//            viewModelScope.launch {
//                dailyMissionRepository.updateMissionProgress(
//                    gameName = "algebra",
//                    missionType = "play_games",
//                    incrementBy = 1,
//                    userId = _userId.value ?: "fake"
//                )
//            }

            viewModelScope.launch {
                statsRepository.updateGameResult(
                    userId = statsRepository.initUserIfNeeded() ?: "987",
                    gameName = "algebra",
                    levelReached = currentLevel,
                    won = false,
                    xpGained = xp,
                    hintsUsed = _hintsUsed.value,
                    timeSpentSeconds = _time.value.toLong(),
                    coinsEarned = 0,
                    currentStreak = 0,
                    bestStreak = _bestStreak.value,
                    eachGameXp = xp,
                    eachGameCoin = 0,
                    resultTitle = "Better Luck Next Time",
                    resultMessage = "Keep trying!",
                    isMatchWon = false
                )
            }
            endGame1()
        } else if ((_score.value / 100) > _level.value) {

            markLevelCompleted()

            _currentStreak.value = _currentStreak.value + 1
            _bestStreak.value = maxOf(_bestStreak.value, _currentStreak.value)

            Log.d("Streak", "Current streak: ${_currentStreak.value}")
            Log.d("Streak", "Best streak: ${_bestStreak.value}")


//            _coinsEarned.value = if (_currentStreak.value >= 2) {
//                30
//            } else {
//                rewardLevels[currentLevel] ?: 0
//            }
            viewModelScope.launch {
                val perGameStats = statsRepository.getPerGameStats(_userId.value ?: "1","algebra")
                val currentStreak = perGameStats?.currentStreak?.plus(1)
                val bestStreak = perGameStats?.bestStreak?.plus(1)
                _coinsEarned.value = calculateAlgebraCoins(
                    levelReached = currentLevel,
                    isLevelCleared = true,
                    currentStreak =  currentStreak ?:0,
                    bestStreak = bestStreak ?:0
                )
                Log.d("CoinsEA",_coinsEarned.value.toString())
            }





            _gameResult.value = GameResult(
                level = currentLevel,
                won = true,
                xpEarned = xp,
                score = _score.value,
                streak = _currentStreak.value,
                bestStreak = _bestStreak.value,
                hintsUsed = _hintsUsed.value,
                timeSpent = _time.value.toLong()
            )

//            viewModelScope.launch {
//                dailyMissionRepository.updateMissionProgress(
//                    gameName = "algebra",
//                    missionType = "play_games",
//                    incrementBy = 1,
//                    userId = _userId.value ?: "fake"
//                )
//            }

            viewModelScope.launch {
                delay(1500)
                statsRepository.updateGameResult(
                    userId = statsRepository.initUserIfNeeded() ?: "987",
                    gameName = "algebra",
                    levelReached = currentLevel,
                    won = true,
                    xpGained = xp,
                    hintsUsed = _hintsUsed.value,
                    timeSpentSeconds = _time.value.toLong(),
                    coinsEarned = _coinsEarned.value,
                    currentStreak = _currentStreak.value,
                    bestStreak = _bestStreak.value,
                    eachGameXp = xp,
                    eachGameCoin = _coinsEarned.value,
                    resultTitle = "Congratulations",
                    resultMessage = "Beat your own best streak to earn coins",
                    isMatchWon = true,
                )
            }
            //  viewModel.loadResult(userId.value ?: "pass", "algebra")
            endGame1()
        } else {
            startNext()
        }

    }

    fun calculateAlgebraCoins(
        levelReached: Int,
        isLevelCleared: Boolean,
        currentStreak: Int,
        bestStreak: Int
    ): Int {
        if (!isLevelCleared) return 0

        Log.d("CoinsEA","CurrentStreak: ${currentStreak}")
        Log.d("CoinsEA","BestStreak: ${bestStreak}")

        var coins = 0

        // 🎯 Base reward by level range
        coins += when (levelReached) {
            in 1..5 -> (3..6).random()
            in 6..10 -> (6..10).random()
            in 11..20 -> (10..15).random()
            in 21..30 -> (15..25).random()
            else -> (25..35).random()
        }

        // 🔥 Streak rewards (not too generous)
        coins += when {
            currentStreak in 3..5 -> 8
            currentStreak in 6..10 -> 15
            currentStreak > 10 -> 25
            else -> 0
        }

        // 🧠 New best streak reward (rare)
        if (currentStreak > bestStreak) {
            coins += 20
        }

        // 🎁 Milestone bonus levels (big dopamine spikes)
        coins += when (levelReached) {
            5 -> 15
            10 -> 25
            15 -> 35
            20 -> 45
            25 -> 55
            30 -> 70
            40 -> 90
            50 -> 120
            else -> 0
        }

        // 🍀 Small random chance (3–5%) of "Brain Bonus"
        if ((1..100).random() <= 5) {
            coins += (10..25).random()
        }

        return coins
    }



    fun calculateXp(won: Boolean, playerLevel: Int): Int {
        return if (won) {
            when (playerLevel) {
                in 1..5 -> 20
                in 6..10 -> 35
                in 11..20 -> 50
                else -> 70
            }
        } else {
            when (playerLevel) {
                in 1..5 -> 5
                in 6..10 -> 10
                in 11..20 -> 15
                else -> 20
            }
        }
    }


    fun useHint() {
        _hintsUsed.value += 1
    }

     fun endGame(timeout: Boolean = false) {
        timerJob?.cancel()
        timerJob = null


        if (timeout) {
            // Always override gameResult on timeout, even if non-null
            _gameResult.value = GameResult(
                level = currentLevel,
                won = false,
                xpEarned = 0,
                score = _score.value,
                streak = _currentStreak.value,
                bestStreak = _bestStreak.value,
                hintsUsed = _hintsUsed.value,
                timeSpent = _time.value.toLong()
            )
            viewModelScope.launch {
                // val user = statsRepository.initUserIfNeeded()
                statsRepository.updateGameResult(
                    userId = _userId.value ?: "987",
                    gameName = "algebra",
                    levelReached = currentLevel,
                    won = false,
                    xpGained = 0,
                    hintsUsed = _hintsUsed.value,
                    timeSpentSeconds = _time.value.toLong(),
                    coinsEarned = 0,
                    currentStreak = 0,
                    bestStreak = _bestStreak.value,
                    eachGameXp = 0,
                    eachGameCoin = 0,
                    resultTitle = "Time's Up!",
                    resultMessage = "Try again!",
                    isMatchWon = false
                )
                Log.d("EndGame", "Stats updated for timeout lose")
            }


            _gameOver.value = true
            return
        } else {
            // Always override gameResult on timeout, even if non-null
            _gameResult.value = GameResult(
                level = currentLevel,
                won = false,
                xpEarned = 0,
                score = _score.value,
                streak = _currentStreak.value,
                bestStreak = _bestStreak.value,
                hintsUsed = _hintsUsed.value,
                timeSpent = _time.value.toLong()
            )
            viewModelScope.launch {
                // val user = statsRepository.initUserIfNeeded()
                statsRepository.updateGameResult(
                    userId = _userId.value ?: "987",
                    gameName = "algebra",
                    levelReached = currentLevel,
                    won = false,
                    xpGained = 0,
                    hintsUsed = _hintsUsed.value,
                    timeSpentSeconds = _time.value.toLong(),
                    coinsEarned = 0,
                    currentStreak = 0,
                    bestStreak = _bestStreak.value,
                    eachGameXp = 0,
                    eachGameCoin = 0,
                    resultTitle = "Better Luck Next Time",
                    resultMessage = "Try again!",
                    isMatchWon = false
                )
                Log.d("EndGame", "Stats updated for timeout lose")
            }


            _gameOver.value = true
        }

        // 🔑 if result is already set (win/lose), don’t overwrite
        if (_gameResult.value != null) {
            _gameOver.value = true
            return
        }


        _gameOver.value = true
    }

    fun endGame1() {
        if (_gameResult.value != null) {
            _gameOver.value = true
            return
        }

        _gameOver.value = true

    }

    fun resumeNextQuestion() {
        viewModelScope.launch {
            _gameOver.value = false
            _levelCompleted.value = false
            startNext() // continue to next question
        }
    }

    fun triggerGameOver() {
        viewModelScope.launch {
            _gameOver.value = true
            _gameResult.value = GameResult(
                level = currentLevel,
                won = false,
                xpEarned = 0,
                score = 0,
                streak = 0 ,
                bestStreak = 7,
                hintsUsed = 7,
                timeSpent = 8,
            )
        }
    }


}


