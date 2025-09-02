package com.futurion.apps.mindmingle.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.futurion.apps.mindmingle.R
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.futurion.apps.mindmingle.domain.Difficulty
import com.futurion.apps.mindmingle.presentation.game_detail.GameDetailScreen
import com.futurion.apps.mindmingle.presentation.home.HomeGraphScreen
import com.futurion.apps.mindmingle.presentation.sudoku.SudokuScreen
import com.futurion.apps.mindmingle.presentation.sudoku.SudokuViewModel

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
            HomeGraphScreen(
                navigateToGameDetail = {
                    navController.navigate(Screen.GameDetailScreen(it))
                }
            )
        }

        composable<Screen.GameDetailScreen> {
            val id = it.toRoute<Screen.GameDetailScreen>().gameId

            val sudokuViewModel: SudokuViewModel = hiltViewModel()

            val game = sudokuViewModel.getGameById(id)

            GameDetailScreen(
                gameTitle = game?.name ?: "",
                gameSubtitle = game?.description ?: "",
                xpReward = 4,
                coinsReward = 3,
                knowledgeBadges = listOf("a", "b", "c"),
                howToPlaySteps = listOf("a", "b", "c"),
                howToPlayImages = listOf(
                    R.drawable.fourthone,
                    R.drawable.cat,
                    R.drawable.shopping_cart_image
                ),
                onStart = { difficulty ->
                    if (game?.id == "sudoku") {
                        navController.navigate(Screen.SudokuScreen(difficulty))
                    }

                },
            )


        }

        composable<Screen.SudokuScreen> {

            val difficulty = Difficulty.valueOf(it.toRoute<Screen.SudokuScreen>().difficulty)

            SudokuScreen()
        }

    }

}