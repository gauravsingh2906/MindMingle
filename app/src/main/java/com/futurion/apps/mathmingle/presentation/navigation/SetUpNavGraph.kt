package com.futurion.apps.mathmingle.presentation.navigation

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.futurion.apps.mathmingle.R
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.futurion.apps.mathmingle.GoogleRewardedAdManager
import com.futurion.apps.mathmingle.data.local.entity.OverallProfileEntity
import com.futurion.apps.mathmingle.data.local.entity.PerGameStatsEntity
import com.futurion.apps.mathmingle.domain.model.AllGames
import com.futurion.apps.mathmingle.domain.model.Difficulty
import com.futurion.apps.mathmingle.domain.model.UniversalResult
import com.futurion.apps.mathmingle.domain.state.SudokuState
import com.futurion.apps.mathmingle.presentation.algebra.AlgebraGameScreen
import com.futurion.apps.mathmingle.presentation.algebra.AlgebraViewModel
import com.futurion.apps.mathmingle.presentation.algebra.GameWinAnimation
import com.futurion.apps.mathmingle.presentation.algebra.rememberSoundPool


import com.futurion.apps.mathmingle.presentation.game_detail.GameDetailScreen
import com.futurion.apps.mathmingle.presentation.game_result.GameResultScreen
import com.futurion.apps.mathmingle.presentation.game_result.GameResultViewModel
import com.futurion.apps.mathmingle.presentation.home.HomeGraphScreen
import com.futurion.apps.mathmingle.presentation.level_selection.LevelBasedScreen
import com.futurion.apps.mathmingle.presentation.level_selection.LevelSelectionViewModel
import com.futurion.apps.mathmingle.presentation.math_memory.MathMemoryAction
import com.futurion.apps.mathmingle.presentation.math_memory.MathMemoryScreen
import com.futurion.apps.mathmingle.presentation.math_memory.MathMemoryViewModel
import com.futurion.apps.mathmingle.presentation.math_memory.isInternetAvailable
import com.futurion.apps.mathmingle.presentation.profile.StatsViewModel
import com.futurion.apps.mathmingle.presentation.sudoku.SudokuGameEvent
import com.futurion.apps.mathmingle.presentation.sudoku.SudokuScreen
import com.futurion.apps.mathmingle.presentation.sudoku.SudokuViewModel
import com.futurion.apps.mathmingle.presentation.sudoku.sudoku_history.SavedSudokuResultsScreen
import com.futurion.apps.mathmingle.presentation.themes_screen.ThemeUnlockScreen
import com.futurion.apps.mathmingle.presentation.themes_screen.ThemeViewModel
import com.futurion.apps.mathmingle.presentation.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SetUpNavGraph(
    modifier: Modifier = Modifier
) {

    val navController = rememberNavController()


    NavHost(
        navController = navController,
        startDestination = Screen.HomeGraph
    ) {



        composable<Screen.HomeGraph> {

            val statsViewModel: StatsViewModel = hiltViewModel()

            val profile by statsViewModel.profile.collectAsStateWithLifecycle()

            LaunchedEffect(profile?.coins) {
                Log.d("Coins", profile?.coins.toString())
            }

            HomeGraphScreen(
                navigateToGameDetail = {
                    navController.navigate(Screen.GameDetailScreen(it))
                },
                coins = profile?.coins ?: 0,
                profile = profile ?: OverallProfileEntity(userId = "e"),
                viewModel = statsViewModel,
                navigateToThemeUnlock = {
                    navController.navigate(Screen.ThemeSelectionScreen(it))
                }
            )
        }

        composable<Screen.GameDetailScreen> {
            val id = it.toRoute<Screen.GameDetailScreen>().gameId

            val sudokuViewModel: SudokuViewModel = hiltViewModel()

            val statsViewModel: StatsViewModel = hiltViewModel()

            val state = sudokuViewModel.state.value

            val cp = statsViewModel.profile.value?.currentLevelXP

            val coins = statsViewModel.profile.value?.coins

            val userId = statsViewModel.userId.collectAsStateWithLifecycle().value


            val game = sudokuViewModel.getGameById(id)

            GameDetailScreen(
                gameTitle = game?.name ?: "",
                gameSubtitle = game?.description ?: "",
                xpReward = cp ?: 0,
                coinsReward = coins ?: 0,
                knowledgeBadges = listOf("amazing", "best", "corin"),
                howToPlaySteps = game?.steps ?: listOf(),
                howToPlayImages = listOf(R.drawable.fourthone),
                howToEarnCons = game?.coins ?: listOf(),
                onStart = { difficulty ->
                    when (game?.id) {
                        "sudoku" -> {
                            navController.navigate(Screen.SudokuScreen(difficulty))
                        }

                        "math_memory" -> {
                            val highestLevel = statsViewModel.profile.value?.mathMemoryHighestLevel ?: 1
                            Log.d("Nav Level", highestLevel.toString())
                            navController.navigate(Screen.MathMemoryScreen(highestLevel))
                        }

                        else -> {
                            navController.navigate(Screen.LevelSelection("algebra"))
                        }
                    }
                },
                navigateBack = {
                    navController.popBackStack()
                },
                navigateToSudokuResult = {
                    navController.navigate(Screen.SudokuHistoryScreen)
                },
                userId = userId ?:"",
                navigateToThemeUnlock = {
                    navController.navigate(Screen.ThemeSelectionScreen(it))
                }
            )
        }

        composable<Screen.LevelSelection> {

            val viewModel: LevelSelectionViewModel = hiltViewModel()

            val id = it.toRoute<Screen.LevelSelection>().id

            val gameViewModel: AlgebraViewModel = hiltViewModel()

            val maxUnlocked by viewModel.maxUnlockedLevel.collectAsStateWithLifecycle()

            // val gameId = viewModel.gameId.value


            Log.d("Nav Level", maxUnlocked.toString())

            //  val scaffoldState = rememberScaffoldState()
            val coroutineScope = rememberCoroutineScope()


            LevelBasedScreen(
                onLevelClick = { level ->
                    if (level <= maxUnlocked) {
                        gameViewModel.setLevel(level)
                        navController.navigate(Screen.AlgebraGameScreen(level))
                    }
//                    if (id == "algebra") {
//                        Log.e("Nav Level Inside", "Algebra nav level inside$level")
//                        if (level <= maxUnlocked) {
//                            gameViewModel.setLevel(level)
//                            navController.navigate(Screen.AlgebraGameScreen(level))
//                        }
//                    } else if (id == "math_memory") {
//                        Log.e("Nav Level Inside", "Math nav level inside$level")
//                        if (level <= maxUnlocked) {
//                            gameViewModel.setLevel(level)
//                            //  navController.navigate(Routes.MathMemoryMixScreen(level))
//                        }
//                    }

                },
                maxUnlockedLevel = maxUnlocked,
                rewardLevels = viewModel.generateRewardLevels()
            )



        }

        composable<Screen.SudokuScreen> {
            val stringDifficulty = it.toRoute<Screen.SudokuScreen>().difficulty
            val difficulty = Difficulty.valueOf(stringDifficulty)
            val viewModel: SudokuViewModel = hiltViewModel()
            val statsViewModel: StatsViewModel = hiltViewModel()
            val state = viewModel.state
            val context = LocalContext.current

            val perGameStats by statsViewModel.perGameStats.collectAsStateWithLifecycle()

            val perGameStatsEntity = perGameStats.find { it.gameName == "sudoku" } ?: PerGameStatsEntity(userId = "a", gameName = "sudoku", resultTitle = "d", resultMessage = "d")

            var showAnimation by remember { mutableStateOf(false) }
            val soundPool = rememberSoundPool()
            val winSoundId = remember { soundPool.load(context, R.raw.game_completed, 1) }
            val loseSoundId = remember { soundPool.load(context, R.raw.game_over, 1) }



            LaunchedEffect(Unit) {
                viewModel.event.collect { event ->
                    when (event) {
                        is SudokuGameEvent.PuzzleSolved -> {
                            val entity = perGameStats.find { it.gameName == "sudoku" } ?: PerGameStatsEntity(userId = "a", gameName = "sudoku", resultTitle = "d", resultMessage = "d")
                            Log.d("EntityStats", "Entiy +$entity")
                            showAnimation = true
                            soundPool.play(winSoundId, 1f, 1f, 1, 0, 1f)
                            delay(100)
                            delay(2500)
                            showAnimation = false
                            handleSudokuWin(
                                viewModel = viewModel,
                                newViewModel = statsViewModel,
                                navController = navController,
                                difficulty = stringDifficulty,
                                entity = entity,
                                coins = calculateCoins(viewModel,entity)
                            )
                        }

                        is SudokuGameEvent.GameOver -> {
                            val entity = perGameStats.find { it.gameName == "sudoku" } ?: PerGameStatsEntity(userId = "a", gameName = "sudoku", resultTitle = "d", resultMessage = "d")
                            Log.d("EntityStats", "Entiy +$entity")
                            delay(100)
                            soundPool.play(loseSoundId, 1f, 1f, 1, 0, 1f)
                            delay(1500)
                            handleSudokuLoss(
                                viewModel = viewModel,
                                newViewModel = statsViewModel,
                                navController = navController,
                                difficulty = stringDifficulty,
                                entity = entity,
                              //  totalCoins=statsViewModel.profile.value?.coins,
                                coins = calculateCoins(viewModel,entity)
                            )
                        }
                    }
                }
            }

            val activity = context as? Activity
            var showHint by remember { mutableStateOf(false) }
            val googleAdManager = remember { GoogleRewardedAdManager(context, Constants.AD_Unit) }

            // Wrap everything in a Box so we can overlay
            Box(modifier = Modifier.fillMaxSize()) {

                val entity = perGameStats.find { it.gameName == "sudoku" }
                Log.d("EntityStats", "Entiy +$entity")

                // Your Sudoku UI
                SudokuScreen(
                    state = state.value,
                    onAction = viewModel::onAction,
                    onHint = {
                        if (!isInternetAvailable(context)) {
                            Toast.makeText(
                                context,
                                "No internet connection. Please connect to the internet.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@SudokuScreen
                        }
                        if (activity != null) {
                            googleAdManager.showRewardedAd(
                                activity,
                                onUserEarnedReward = {
                                    showHint = true
                                    viewModel.rewardUserForAd()
                                    Toast.makeText(context, "Use your hint now", Toast.LENGTH_LONG).show()
                                },
                                onClosed = {
                                    if (!showHint) {
                                        Toast.makeText(
                                            context,
                                            "Ad not ready, please try again.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            )
                        }
                    },
                    difficulty = difficulty,
                    onBack = {
                        navController.navigateUp()
                        handleSudokuLoss(
                            viewModel = viewModel,
                            newViewModel = statsViewModel,
                            navController = navController,
                            difficulty = stringDifficulty,
                            coins = calculateCoins(viewModel,perGameStatsEntity),
                            entity = entity ?: PerGameStatsEntity(userId = "a", gameName = "sudoku", resultTitle = "d", resultMessage = "d"),
                        )
                    }
                )

                if (showAnimation) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.8f)),
                        contentAlignment = Alignment.Center
                    ) {
                        GameWinAnimation()
                    }
                }

                // Animation overlay on top
//                AnimatedVisibility(
//                    visible = showAnimation,
//                    enter = fadeIn() + scaleIn(),
//                    exit = fadeOut() + scaleOut()
//                ) {


           //     }
            }
        }


        composable<Screen.SudokuHistoryScreen> {
            SavedSudokuResultsScreen(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }

        composable<Screen.MathMemoryScreen> {

            val viewModel: MathMemoryViewModel = hiltViewModel()
            val statsViewModel: StatsViewModel = hiltViewModel()

            val userId = statsViewModel.userId.collectAsState().value ?: "sdf"
            Log.d("MathMemoryMixScreen", "Unlocking theme for user: $userId")

            LaunchedEffect(userId) {
                if (userId.isNotEmpty()) {
                    Log.d("OverTag", "Loading user: $userId")
                    viewModel.loadOrInitMathMemoryLevel()
                }
            }

            MathMemoryScreen(
                navigateToThemeUnlock = {
                    navController.navigate(Screen.ThemeSelectionScreen(it))
                },
                navigateToGames = {
                    navController.navigate(Screen.HomeGraph) {
                        popUpTo(Screen.HomeGraph) { inclusive = true }
                    }
                }
            )
//            MathMemoryScreen(
//                viewModel = viewModel,
//                onExit = {
//                    navController.navigateUp()
//                }
//            )
//            MathMemoryScreenV2(
//                startValue = 7,
//                moves = listOf<MemoryCard>(),
//                answerOptions = listOf(AnswerOption(1, true), AnswerOption(2, false), AnswerOption(3, false), AnswerOption(4, false)),
//                streak = 3,
//                bestStreak = 7,
//                xp = 16,
//                coins = 36,
//                phase = "SOLVE", // Use your logic to set phase
//                selectedTheme = Default[3],
//                userInput = "",
//                isCorrect = false,
//                onSelectOption = { /* handle option selection */ },
//                onNextLevel = { navController.navigate("math_memory") },
//                onRetry = { /* handle retry */ },
//                showTip = true,
//                calculationBreakdown = "Started at 7, +2=9, ×3=27"
//            )
        }

        composable<Screen.AlgebraGameScreen> { it ->

            val gameViewModel: AlgebraViewModel = hiltViewModel()
            val level = it.toRoute<Screen.AlgebraGameScreen>().level


            //   val dailyMissionViewModel: DailyMissionViewModel = hiltViewModel()


            Log.d("Algebra Level", level.toString())

            // val level = it.arguments.getString("level")

            LaunchedEffect(level) {
                gameViewModel.setLevel(level)
            }


            AlgebraGameScreen(
                onBackPressed = {
                    // Save before navigating back
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                },
                naviagteToResultScreen = { universalResult ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("resultData", universalResult)
                    navController.currentBackStackEntry?.savedStateHandle?.set("currentLevel", level)

                    navController.navigate(Screen.CommonResultScreen)
                },
                naviagteToDialogScreen = {},

            )
        }

        composable<Screen.ThemeSelectionScreen> {
            val userId = it.toRoute<Screen.ThemeSelectionScreen>()

            val mathMemoryViewModel: MathMemoryViewModel = hiltViewModel()

            val themeViewModel: ThemeViewModel = hiltViewModel()

            ThemeUnlockScreen(
                onThemeSelected = { selectedTheme ->
                    Log.d("ThemeSelectionScreen", "Selected theme: $selectedTheme")
                    mathMemoryViewModel.onAction(MathMemoryAction.SelectTheme(selectedTheme))
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }

        composable<Screen.CommonResultScreen> {
            val resultViewModel: GameResultViewModel = hiltViewModel()

            val viewModel: MathMemoryViewModel = hiltViewModel()

            //  val data = it.toRoute<Routes.CommonResultScreen>().result

            val data = it.savedStateHandle.get<UniversalResult>("resultData")


            val resultDatas =
                navController.previousBackStackEntry?.savedStateHandle?.get<UniversalResult>("resultData")

            val gameTypeString =
                navController.previousBackStackEntry?.savedStateHandle?.get<String>("gameType")
                    ?: "ALGEBRA"

            val gameType = when (gameTypeString) {
                "SUDOKU" -> AllGames.SUDOKU
                "MEMORY" -> AllGames.MEMORY
                else -> AllGames.ALGEBRA
            }

            val currentDifficulty =
                navController.previousBackStackEntry?.savedStateHandle?.get<String>("currentDifficulty")
                    ?: "Easy"

            Log.d("CurrentDifficulty",currentDifficulty)

            Log.d(" Nav Result Data", resultDatas.toString())

            val profileData =
                navController.previousBackStackEntry?.savedStateHandle?.get<OverallProfileEntity>("profileData")
            val resultData = resultViewModel.resultState.collectAsState().value

            val currentLevel =
                navController.previousBackStackEntry?.savedStateHandle?.get<Int>("currentLevel")
                    ?: 1
            Log.d("Nav Result Data", currentLevel.toString())

            val highestLevel =
                navController.previousBackStackEntry?.savedStateHandle?.get<Int>("highestLevel")
            Log.d("Nav Result Data", "highest level: $highestLevel")
            if (resultDatas != null) {

                GameResultScreen(
                    data = resultDatas,
                    gameType = gameType,
                    currentDifficulty = currentDifficulty,
                    currentLevel = currentLevel,
                    highestLevelCompleted = profileData?.finalLevel ?: 1,
                    onHome = {
                        navController.navigate(Screen.HomeGraph) {
                            popUpTo(Screen.HomeGraph) {
                                inclusive = false
                            }
                        }
                    },
                    onReplay = { level, difficulty ->
                        navController.popBackStack()
                        when (gameType) {
                            AllGames.ALGEBRA -> navController.navigate(
                                Screen.AlgebraGameScreen(
                                    level = currentLevel
                                )
                            )

                            AllGames.SUDOKU -> {
                                navController.navigate(Screen.SudokuScreen(difficulty))
                             //   navController.navigate("sudoku_screen?gameId=${"sudoku"}&difficulty=${difficulty}")
                            }

                            AllGames.MEMORY -> {
                                viewModel.onAction(MathMemoryAction.ResetGame)
                                navController.navigate(Screen.MathMemoryScreen(level = currentLevel))
                            }
                        }

                    },
                    onNext = { nextLevel, difficulty ->
                        navController.popBackStack()
                        // ✅ Smart navigation based on game type
                        when (gameType) {
                            AllGames.ALGEBRA -> {
                                // For Algebra: Go to next level
//                                val nextLevel = level + 1
                                navController.navigate(Screen.AlgebraGameScreen(level = nextLevel))
                            }

                            AllGames.SUDOKU -> {
                                // For Sudoku: Go to harder difficulty
                                val nextDifficulty = when (difficulty.lowercase()) {
                                    "easy" -> "MEDIUM"
                                    "medium" -> "HARD"
                                    "hard" -> "EXPERT"
                                    else -> "MEDIUM"
                                }
                                navController.navigate(Screen.SudokuScreen(nextDifficulty))
                            }

                            AllGames.MEMORY -> {
                                //  viewModel.onAction(MathMemoryAction.NextLevel)
                                navController.navigate(Screen.MathMemoryScreen(level = nextLevel))
                            }
                        }
                    },
                    navigateToMathMemory = { nextLevel ->
                        Log.d("Next Level", nextLevel.toString())
                        navController.navigate(Screen.MathMemoryScreen(nextLevel))
                    },
                    navigateToGameDetail = {
                        navController.navigate(Screen.GameDetailScreen(it)) {
                            popUpTo(Screen.HomeGraph) { inclusive = false }
                        }
                    }
                )
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No result data available")
                        Button(onClick = { navController.popBackStack() }) {
                            Text("Go Back")
                        }
                    }
                }
            }

        }


    }

}

private fun handleSudokuWin(
    viewModel: SudokuViewModel,
    newViewModel: StatsViewModel,
    navController: NavController,
    difficulty: String,
    coins: Int,
    entity: PerGameStatsEntity
) {
    val state = viewModel.state.value





    // Update all your existing stats
//    userStatsViewModel.recordGameResult(
//        gameName = "sudoku",
//        isWin = true,
//        isDraw = false,
//        xpEarned = state.xpEarned,
//    )

    val time = (state.elapsedTime) / 60
//    newViewModel.updateProgress(
//        gameName = "sudoku",
//        minutes = time,
//        missionType = "play_games"
//    )

    newViewModel.viewModelScope.launch {

        withContext(Dispatchers.IO) {
            newViewModel.updateGameAndProfile(
                userId = newViewModel.userId.value ?: "name",
                gameName = "sudoku",
                level = 1,
                won = true,
                xp = state.xpEarned,
                hints = state.hintsUsed,
                timeSec = state.elapsedTime.toLong(),
                coins = coins,
                currentStreak = viewModel.currentStreak.value,
                bestStreak = viewModel.bestStreak.value,
                resultTitle = "🎉 EXCELLENT!",
                resultMessage = "You solved the puzzle perfectly!",
                isMatchWon = true,
                eachGameXp = state.xpEarned,
                eachGameCoin = 1,
            )
            newViewModel.loadProfile(userId = newViewModel.userId.value ?: "name")
        }

        withContext(Dispatchers.Main) {
            // ✅ Navigate to CommonResultScreen with Sudoku data
            navigateToSudokuResult(
                navController = navController,
                state = state,
                difficulty = difficulty,
                coins = coins,
                currentStreak = entity.currentStreak,
                bestStreak = entity.bestStreak,
                profileData = newViewModel.profile.value,
                isWin = true,
                newViewModel = newViewModel,
            )
        }

    }




}

private fun handleSudokuLoss(
    viewModel: SudokuViewModel,
    newViewModel: StatsViewModel,
    navController: NavController,
    difficulty: String,
    coins: Int, // this is a per game coin
    entity: PerGameStatsEntity
) {
    val state = viewModel.state.value

    // Update all your existing stats


    val time = (state.elapsedTime) / 60

    val bestStreak = entity.bestStreak
    Log.d("BestStreak","Win: $entity")
    Log.d("BestStreak","Loss: $bestStreak")

    newViewModel.viewModelScope.launch {
        withContext(Dispatchers.IO) {
            newViewModel.updateGameAndProfile(
                userId = newViewModel.userId.value ?: "name",
                gameName = "sudoku",
                level = 1,
                won = false,
                xp = state.xpEarned,
                hints = state.hintsUsed,
                timeSec = state.elapsedTime.toLong(),
                coins = coins,
                currentStreak = viewModel.currentStreak.value,
                bestStreak = bestStreak,
                resultTitle = "😔 GAME OVER",
                resultMessage = "You made 3 mistakes. Better luck next time!",
                isMatchWon = false,
                eachGameXp = state.xpEarned,
                eachGameCoin = 0,
            )
        }
        withContext(Dispatchers.Main) {
            // ✅ Navigate to CommonResultScreen with Sudoku data
            navigateToSudokuResult(
                newViewModel,
                navController = navController,
                state = state,
                difficulty = difficulty,
                currentStreak = entity.currentStreak,
                bestStreak = entity.bestStreak,
                profileData = newViewModel.profile.value,
                isWin = false,
                coins = coins, // navigating with each game coins
            )
        }
    }




}

private fun navigateToSudokuResult(
    newViewModel: StatsViewModel,
    navController: NavController,
    state: SudokuState,
    difficulty: String,
    currentStreak: Int,
    bestStreak: Int,
    profileData: OverallProfileEntity?,
    isWin: Boolean,
    coins: Int
) {
    Log.d("BestStreak",bestStreak.toString())

    val resutl = newViewModel.perGameStats.value.find { it.gameName == "sudoku" }
    // Create UniversalResult for Sudoku
    val universalResult = UniversalResult(
        title = if (isWin) "EXCELLENT" else "GAME OVER",
        resultTitle = if (isWin) "🎉 EXCELLENT!" else "😔 GAME OVER", // Sudoku doesn't use score
        resultMessage = "Better luck next time!",
        won = isWin,
        isMatchWon = true,
        bestTimeSec = state.elapsedTime,
        difficulty = difficulty, // Add if you have avatar unlocks
        hintsUsed = state.hintsUsed,
        unlockMessage = if (currentStreak >= 3) "🔥 You're on fire! Keep the streak going!" else null,
        canReplay = true, // Can try harder difficulty if won
        canNextLevel = isWin,
        xpEarned = profileData?.currentLevelXP?.plus(state.xpEarned) ?: 0,
        eachGameXp = state.xpEarned,
        eachGameCoin = coins,
        coinsEarned = profileData?.coins?.plus(coins) ?: 0,
        currentStreak = (resutl?.currentStreak?.plus(1)) ?: 0,
        bestStreak = resutl?.bestStreak?.plus(1) ?: 0,
    )

    Log.d("SudokuUniversal",universalResult.toString())

    // Pass all data through SavedStateHandle
    navController.currentBackStackEntry?.savedStateHandle?.apply {
        set("resultData", universalResult)
        set("gameType", "SUDOKU")
        set("currentDifficulty", difficulty)
    }

    navController.navigate(Screen.CommonResultScreen)
}

fun calculateCoins(viewModel: SudokuViewModel,profileEntity: PerGameStatsEntity): Int {
    var totalCoins = 0
    val profile = profileEntity.currentStreak+1
    Log.d("Solution",profile.toString())
    if (profile >= 3) {
        totalCoins += 30       // Streak bonus
    }
    if (viewModel.state.value.isGameWon) {
        totalCoins += when (viewModel.difficulty) {
            Difficulty.EASY -> 5
            Difficulty.MEDIUM -> 15
            Difficulty.HARD -> 30
        }
    }
    val totalTime = viewModel.state.value.elapsedTime
    Log.d("Solution","TotalTime:$totalTime")
    if (totalTime < 300) {
        totalCoins += 15       // Fast finish bonus
    }
    return totalCoins
}
