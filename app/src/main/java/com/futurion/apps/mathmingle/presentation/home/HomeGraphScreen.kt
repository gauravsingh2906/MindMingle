package com.futurion.apps.mathmingle.presentation.home

import ContentWithMessageBar
import android.app.Activity
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.futurion.apps.mathmingle.R
import com.futurion.apps.mathmingle.data.local.entity.OverallProfileEntity
import com.futurion.apps.mathmingle.presentation.games.GameStats
import com.futurion.apps.mathmingle.presentation.games.GamesScreenWithStats
import com.futurion.apps.mathmingle.presentation.games.SampleGames.gameItems
import com.futurion.apps.mathmingle.presentation.home.component.AnimatedCoinsChip
import com.futurion.apps.mathmingle.presentation.home.domain.BottomBarDestination
import com.futurion.apps.mathmingle.presentation.mind_mingle.MindMingleScreen
import com.futurion.apps.mathmingle.presentation.navigation.Screen
import com.futurion.apps.mathmingle.presentation.profile.ProfileScreen
import com.futurion.apps.mathmingle.presentation.profile.StatsViewModel
import com.futurion.apps.mathmingle.presentation.utils.AppBackground
import com.futurion.apps.mathmingle.presentation.utils.BebasNeueFont
import com.futurion.apps.mathmingle.presentation.utils.FontSize
import com.futurion.apps.mathmingle.presentation.utils.Surface
import rememberMessageBarState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HomeGraphScreen(
    modifier: Modifier = Modifier,
    viewModel: StatsViewModel,
    profile: OverallProfileEntity,
    navigateToGameDetail: (String) -> Unit,
    navigateToThemeUnlock:(String)-> Unit,
    coins: Int
) {

    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState()

    val selectedDestination by remember {
        derivedStateOf {
            val route = currentRoute.value?.destination?.route.toString()

            when {
                route.contains(BottomBarDestination.Home.screen.toString()) -> BottomBarDestination.Home
                route.contains(BottomBarDestination.Games.screen.toString()) -> BottomBarDestination.Games
                route.contains(BottomBarDestination.Profile.screen.toString()) -> BottomBarDestination.Profile
                else -> BottomBarDestination.Home
            }
        }
    }

    val messageBarState = rememberMessageBarState()




    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .systemBarsPadding()
            .navigationBarsPadding()
    ) {
        Scaffold(
            containerColor = AppBackground,
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    TopAppBar(
                        modifier = Modifier
                            .fillMaxWidth(),
                        title = {
                            AnimatedContent(
                                targetState = selectedDestination,
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(300)) with fadeOut(
                                        animationSpec = tween(
                                            300
                                        )
                                    )
                                }
                            ) { destination ->
                                Text(
                                    text = destination.title,
                                    fontFamily = BebasNeueFont(),
                                    fontSize = FontSize.LARGE,
                                    color = Color.Black
                                )
                            }
                        },
                        navigationIcon = {
                            AnimatedVisibility(
                                visible = selectedDestination != BottomBarDestination.Games
                            ) {
                                IconButton(
                                    onClick = {
                                        viewModel.loadProfile(profile.userId)
                                        navController.navigate(Screen.Games)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.Black
                                    )
                                }
                            }
                        },
                        actions = {
                            //    StatChip(icon = "💰", label = "Coins", value = coins)

                            AnimatedVisibility(visible = selectedDestination == BottomBarDestination.Games) {
                                AnimatedCoinsChip(
                                    viewModel.profile.collectAsStateWithLifecycle().value?.coins
                                        ?: 0
                                )
                            }

                            AnimatedVisibility(
                                visible = selectedDestination == BottomBarDestination.Profile
                            ) {
                                IconButton(
                                    onClick ={
                                        navigateToThemeUnlock(profile.userId)
                                    },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.theme), // Replace with your theme icon
                                        contentDescription = "Theme Selector",
                                        tint = Color(0xFF6A8BFF)
                                    )
                                }

                            }




                            AnimatedVisibility(visible = selectedDestination == BottomBarDestination.Games) {

                                IconButton(
                                    onClick = {
                                        navController.navigate(Screen.Profile)
                                    }
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(model = profile.avatarUri),
                                        contentDescription = "Avatar",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.White, // transparent because gradient is behind
                            navigationIconContentColor = Color.White,
                            titleContentColor = Color.White,
                            actionIconContentColor = Color.White
                        ),
                    )
                }
            }
        ) { paddingValues ->
            ContentWithMessageBar(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding()
                    ),
                messageBarState = messageBarState,
                errorMaxLines = 2,
                contentBackgroundColor = Surface
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    NavHost(
                        modifier = Modifier.weight(1f),
                        navController = navController,
                        startDestination = Screen.Games
                    ) {
                        composable<Screen.Home> {
                            MindMingleScreen()
                        }
                        composable<Screen.Games> {


                            //    val viewModel : StatsViewModel = hiltViewModel()

//                            val sudokuViewModel: SudokuViewModel = hiltViewModel()
//
//                            val stats = viewModel.profile.collectAsState().value
//
//                            val userId = viewModel.userId.collectAsState().value
//
//                            // val ids = viewModel.loadProfile(userId = userId ?: "212")
//
//                            val perGameStats = viewModel.gameStats.collectAsState().value
//
                            val perStats = viewModel.perGameStats.collectAsState().value

                            //    val game = sudokuViewModel.getGameById(id)


                            GamesScreenWithStats(
                                games = gameItems,
                                onGameClick = navigateToGameDetail,
                                statsForGame = { id ->

                                    val entity = perStats.find { it.gameName == id }
                                    Log.d("Stats", entity.toString())

                                    if (entity != null) {
                                        GameStats(
                                            gamesPlayed = entity.gamesPlayed,
                                            wins = entity.wins,
                                            xpTotal = entity.xp,
                                            currentStreak = entity.currentStreak
                                        )

                                    } else {
                                        GameStats()
                                    }


                                }
                            )

//                            GamesScreen(
//                                statsForGame = { id ->
//
//                                    val entity = perStats.find { it.gameName == id }
//                                    Log.d("Stats", entity.toString())
//
//                                    if (entity != null) {
//                                        GameStats(
//                                            gamesPlayed = entity.gamesPlayed,
//                                            wins = entity.wins,
//                                            xpTotal = entity.xp,
//                                            currentStreak = entity.currentStreak
//                                        )
//
//                                    } else {
//                                        GameStats(
//                                            gamesPlayed = 4,
//                                            wins = 2,
//                                            xpTotal = 100,
//                                            currentStreak = 3
//                                        )
//                                    }
//                                },
//                                onGameClick = navigateToGameDetail,
//                                games = gameItems
//                            )
                        }
                        composable<Screen.Profile> {

                            val context = LocalContext.current
                            val activity = context as Activity

                            val statsViewModel: StatsViewModel = hiltViewModel()
                            val profile by statsViewModel.profile.collectAsStateWithLifecycle() // Use Flow/LiveData/State
                            val perGameStats by statsViewModel.perGameStats.collectAsStateWithLifecycle()

                            val userId = statsViewModel.userId.collectAsStateWithLifecycle().value
                            val us = "d41e5130-eacf-401a-bd03-e0cb4c0c9a96"
                            Log.d("User", "tab$userId")


                            ProfileScreen(
                                profile = profile
                                    ?: OverallProfileEntity(userId = "1\tc97f320d-4681-4e07-aeca-f305ea33d7e9\tsudoku\t2\t0\t2\t0\t20\t1\t3\t1\t6\t20"),
                                perGameStats = perGameStats,
                                navigateToProfileScreen = {
                                    navController.navigate(Screen.GameDetailScreen)
                                }
                            )
                        }

                    }


                }

            }
        }

    }

}

fun Brush.toBrushColor(): Color {
    // Compose doesn’t support brush directly in TopAppBarColors; workaround:
    // Use a transparent or base color and paint gradient behind the TopAppBar Box if needed.
    return Color(0xFF667EEA) // fallback: use gradient’s start color
}







