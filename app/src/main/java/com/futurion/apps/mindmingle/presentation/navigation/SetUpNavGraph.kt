package com.futurion.apps.mindmingle.presentation.navigation

import android.app.Activity
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.futurion.apps.mindmingle.R
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.futurion.apps.mindmingle.GoogleRewardedAdManager
import com.futurion.apps.mindmingle.data.local.entity.OverallProfileEntity
import com.futurion.apps.mindmingle.domain.model.AllGames
import com.futurion.apps.mindmingle.domain.model.Difficulty
import com.futurion.apps.mindmingle.domain.model.UniversalResult
import com.futurion.apps.mindmingle.domain.state.SudokuState
import com.futurion.apps.mindmingle.presentation.algebra.AlgebraGameScreen
import com.futurion.apps.mindmingle.presentation.algebra.AlgebraViewModel


import com.futurion.apps.mindmingle.presentation.game_detail.GameDetailScreen
import com.futurion.apps.mindmingle.presentation.game_result.GameResultScreen
import com.futurion.apps.mindmingle.presentation.game_result.GameResultViewModel
import com.futurion.apps.mindmingle.presentation.home.HomeGraphScreen
import com.futurion.apps.mindmingle.presentation.level_selection.LevelBasedScreen
import com.futurion.apps.mindmingle.presentation.level_selection.LevelSelectionViewModel
import com.futurion.apps.mindmingle.presentation.math_memory.MathMemoryAction
import com.futurion.apps.mindmingle.presentation.math_memory.MathMemoryScreen
import com.futurion.apps.mindmingle.presentation.math_memory.MathMemoryViewModel
import com.futurion.apps.mindmingle.presentation.profile.StatsViewModel
import com.futurion.apps.mindmingle.presentation.sudoku.SudokuGameEvent
import com.futurion.apps.mindmingle.presentation.sudoku.SudokuScreen
import com.futurion.apps.mindmingle.presentation.sudoku.SudokuViewModel
import com.futurion.apps.mindmingle.presentation.sudoku.sudoku_history.SavedSudokuResultsScreen
import com.futurion.apps.mindmingle.presentation.themes_screen.ThemeUnlockScreen
import com.futurion.apps.mindmingle.presentation.themes_screen.ThemeViewModel
import com.futurion.apps.mindmingle.presentation.utils.Constants
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun SetUpNavGraph(
    modifier: Modifier = Modifier
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.HomeGraph
    ) {

//        composable<Screen.Auth> {
//            AuthScreen(
//                navigateToHomeScreen = {
//                    navController.navigate(Screen.HomeGraph) {
//                        popUpTo<Screen.Auth> {
//                            inclusive=true
//                        }
//                    }
//                }
//            )
//        }

        composable<Screen.HomeGraph> {

            val statsViewModel: StatsViewModel = hiltViewModel()

            HomeGraphScreen(
                navigateToGameDetail = {
                    navController.navigate(Screen.GameDetailScreen(it))
                },
                coins = statsViewModel.profile.value?.coins.toString()
            )
        }

        composable<Screen.GameDetailScreen> {
            val id = it.toRoute<Screen.GameDetailScreen>().gameId

            val sudokuViewModel: SudokuViewModel = hiltViewModel()

            val statsViewModel: StatsViewModel = hiltViewModel()

            val state = sudokuViewModel.state.value

            val cp = statsViewModel.profile.value?.currentLevelXP

            val coins = statsViewModel.profile.value?.coins


            val game = sudokuViewModel.getGameById(id)

            val xp = statsViewModel.perGameStats.value.forEach { it ->
                if (it.gameName == "Sudoku") {
                    val po = it.xp
                }
            }



            GameDetailScreen(
                gameTitle = game?.name ?: "",
                gameSubtitle = game?.description ?: "",
                xpReward = cp ?: 0,
                coinsReward = coins ?: 0,
                knowledgeBadges = listOf("amazing", "best", "corin"),
                howToPlaySteps = game?.steps ?: listOf(),
                howToPlayImages = listOf(
                    R.drawable.fourthone,
                    R.drawable.cat,
                    R.drawable.shopping_cart_image
                ),
                howToEarnCons = game?.coins ?: listOf(),
                onStart = { difficulty ->
                    if (game?.id == "sudoku") {
                        navController.navigate(Screen.SudokuScreen(difficulty))
                    } else if (game?.id == "math_memory") {
                        val highestLevel = statsViewModel.profile.value?.mathMemoryHighestLevel ?: 1
                        Log.d("Nav Level", highestLevel.toString())
                        navController.navigate(Screen.MathMemoryScreen(highestLevel))
                    } else {
                        navController.navigate(Screen.LevelSelection("algebra"))
                    }

                },
                navigateBack = {
                    navController.popBackStack()
                },
                navigateToSudokuResult = {
                    navController.navigate(Screen.SudokuHistoryScreen)
                }

            )


        }

        composable<Screen.LevelSelection> {

            val viewModel: LevelSelectionViewModel = hiltViewModel()

            val id = it.toRoute<Screen.LevelSelection>().id

            val gameViewModel: AlgebraViewModel = hiltViewModel()

            val maxUnlocked by viewModel.maxUnlockedLevel.collectAsState()

            // val gameId = viewModel.gameId.value


            Log.d("Nav Level", maxUnlocked.toString())

            //  val scaffoldState = rememberScaffoldState()
            val coroutineScope = rememberCoroutineScope()

            LevelBasedScreen(
                onLevelClick = { level ->
                    if (id == "algebra") {
                        Log.e("Nav Level Inside", "Algebra nav level inside$level")
                        if (level <= maxUnlocked) {
                            gameViewModel.setLevel(level)
                            navController.navigate(Screen.AlgebraGameScreen(level))
                        }
                    } else if (id == "math_memory") {
                        Log.e("Nav Level Inside", "Math nav level inside$level")
                        if (level <= maxUnlocked) {
                            gameViewModel.setLevel(level)
                            //  navController.navigate(Routes.MathMemoryMixScreen(level))
                        }
                    }

                },
                maxUnlockedLevel = maxUnlocked,
            )
        }

        composable<Screen.SudokuScreen> {

            val stringDifficulty = it.toRoute<Screen.SudokuScreen>().difficulty

            val difficulty = Difficulty.valueOf(it.toRoute<Screen.SudokuScreen>().difficulty)

            val viewModel: SudokuViewModel = hiltViewModel()
            val statsViewModel: StatsViewModel = hiltViewModel()

            val state = viewModel.state

            val context = LocalContext.current

            LaunchedEffect(Unit) {
                viewModel.event.collect { event ->
                    when (event) {
                        is SudokuGameEvent.PuzzleSolved -> {
                            handleSudokuWin(
                                viewModel = viewModel,
                                newViewModel = statsViewModel,
                                navController = navController,
                                difficulty = stringDifficulty,
                                coins = calculateCoins(viewModel)
                            )
                        }

                        is SudokuGameEvent.GameOver -> {
                            handleSudokuLoss(
                                viewModel = viewModel,
                                newViewModel = statsViewModel,
                                navController = navController,
                                difficulty = stringDifficulty,
                                coins = calculateCoins(viewModel)
                            )
                        }
                    }
                }
            }
            val activity = context as? Activity
//            val adMobAdUnitId =
//                "ca-app-pub-3940256099942544/5224354917"

            var showHint by remember { mutableStateOf(false) }
            val googleAdManager = remember { GoogleRewardedAdManager(context, Constants.AD_Unit) }
            // Facebook ad manager
            //  val facebookAdManager = remember { FacebookRewardedAdManager(context) }

            // Your AdMob rewarded ad unit
            val facebookPlacementId = "xxxxxxxx"               // Your FB placement ID

            SudokuScreen(
                state = state.value,
                onAction = viewModel::onAction,
                onHint = {
                    if (activity != null) {
                        googleAdManager.showRewardedAd(
                            activity,
                            onUserEarnedReward = {
                                showHint = true
                                viewModel.rewardUserForAd()
                                Toast.makeText(context, "Use your hint now", Toast.LENGTH_LONG)
                                    .show()
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

                    //  viewModel.onAction(SudokuAction.UseHint)

                },
                difficulty = difficulty,
                onBack = {
                    navController.navigateUp()
                    handleSudokuLoss(
                        viewModel = viewModel,
                        newViewModel = statsViewModel,
                        navController = navController,
                        difficulty = stringDifficulty,
                        coins = calculateCoins(viewModel)
                    )
                }
            )
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
                    navController.navigateUp()
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
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "resultData",
                        universalResult
                    )
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "currentLevel",
                        level
                    )

                    navController.navigate(Screen.CommonResultScreen)
                }

            )
        }

        composable<Screen.ThemeSelectionScreen> {
            val userId = it.toRoute<Screen.ThemeSelectionScreen>() ?: "3r3r"

            val mathMemoryViewModel: MathMemoryViewModel = hiltViewModel()

            val themeViewModel: ThemeViewModel = hiltViewModel()

            ThemeUnlockScreen(
                modifier = Modifier.fillMaxSize(),
                onThemeSelected = { selectedTheme ->
                    Log.d("ThemeSelectionScreen", "Selected theme: $selectedTheme")
                    mathMemoryViewModel.onAction(MathMemoryAction.SelectTheme(selectedTheme))


                    navController.popBackStack()


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
                            popUpTo(Screen.HomeGraph) { inclusive = false }
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
                                navController.navigate("sudoku_screen?gameId=${"sudoku"}&difficulty=${difficulty}")
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
                                navController.navigate("sudoku_screen?gameId=${"sudoku"}&difficulty=${nextDifficulty}")
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
    coins: Int
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

    // ✅ Navigate to CommonResultScreen with Sudoku data
    navigateToSudokuResult(
        navController = navController,
        state = state,
        difficulty = difficulty,
        coins = coins,
        currentStreak = viewModel.currentStreak.value,
        bestStreak = viewModel.bestStreak.value,
        profileData = newViewModel.profile.value,
        isWin = true
    )
}

private fun handleSudokuLoss(
    viewModel: SudokuViewModel,
    newViewModel: StatsViewModel,
    navController: NavController,
    difficulty: String,
    coins: Int
) {
    val state = viewModel.state.value

    // Update all your existing stats


    val time = (state.elapsedTime) / 60


    newViewModel.updateGameAndProfile(
        userId = newViewModel.userId.value ?: "name",
        gameName = "sudoku",
        level = 1,
        won = false,
        xp = state.xpEarned,
        hints = state.hintsUsed,
        timeSec = state.elapsedTime.toLong(),
        coins = 0,
        currentStreak = viewModel.currentStreak.value,
        bestStreak = viewModel.bestStreak.value,
        resultTitle = "😔 GAME OVER",
        resultMessage = "You made 3 mistakes. Better luck next time!",
        isMatchWon = false,
        eachGameXp = state.xpEarned,
        eachGameCoin = 1,
    )

    // ✅ Navigate to CommonResultScreen with Sudoku data
    navigateToSudokuResult(
        navController = navController,
        state = state,
        difficulty = difficulty,
        currentStreak = viewModel.currentStreak.value,
        bestStreak = viewModel.bestStreak.value,
        profileData = newViewModel.profile.value,
        isWin = false,
        coins = coins,
    )
}

private fun navigateToSudokuResult(
    navController: NavController,
    state: SudokuState,
    difficulty: String,
    currentStreak: Int,
    bestStreak: Int,
    profileData: OverallProfileEntity?,
    isWin: Boolean,
    coins: Int
) {
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
        eachGameCoin = if (currentStreak >= 3) 20 else 0,
        coinsEarned = coins ?: 1,
        currentStreak = if (isWin) currentStreak + 1 else 0,
        bestStreak = if (isWin) bestStreak + 1 else bestStreak,
    )

    // Pass all data through SavedStateHandle
    navController.currentBackStackEntry?.savedStateHandle?.apply {
        set("resultData", universalResult)
        set("gameType", "SUDOKU")
        set("currentDifficulty", difficulty)
    }

    navController.navigate(Screen.CommonResultScreen)
}

fun calculateCoins(viewModel: SudokuViewModel): Int {
    var totalCoins = 0
    if (viewModel.currentStreak.value >= 3) {
        totalCoins += 30       // Streak bonus
    }
    if (viewModel.state.value.isGameWon) {
        totalCoins += when (viewModel.difficulty) {
            Difficulty.EASY -> 5
            Difficulty.MEDIUM -> 15
            Difficulty.HARD -> 30
        }
    }
    if (viewModel.state.value.elapsedTime < 5) {
        totalCoins += 15       // Fast finish bonus
    }
    return totalCoins
}
