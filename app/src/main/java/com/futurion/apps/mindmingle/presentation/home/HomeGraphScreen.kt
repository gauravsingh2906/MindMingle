package com.futurion.apps.mindmingle.presentation.home

import ContentWithMessageBar
import android.app.Activity
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.futurion.apps.mindmingle.R

import com.futurion.apps.mindmingle.data.local.entity.OverallProfileEntity
import com.futurion.apps.mindmingle.presentation.games.GameGridItem
import com.futurion.apps.mindmingle.presentation.games.GamesScreen
import com.futurion.apps.mindmingle.presentation.games.SampleGames.gameItems
import com.futurion.apps.mindmingle.presentation.home.component.BottomBarNavigation
import com.futurion.apps.mindmingle.presentation.home.domain.BottomBarDestination
import com.futurion.apps.mindmingle.presentation.mind_mingle.MindMingleScreen
import com.futurion.apps.mindmingle.presentation.navigation.Screen
import com.futurion.apps.mindmingle.presentation.profile.ProfileScreen
import com.futurion.apps.mindmingle.presentation.profile.StatChip
import com.futurion.apps.mindmingle.presentation.profile.StatsViewModel
import com.futurion.apps.mindmingle.presentation.utils.AppBackground
import com.futurion.apps.mindmingle.presentation.utils.BebasNeueFont
import com.futurion.apps.mindmingle.presentation.utils.FontSize
import com.futurion.apps.mindmingle.presentation.utils.IconPrimary
import com.futurion.apps.mindmingle.presentation.utils.Surface
import com.futurion.apps.mindmingle.presentation.utils.TextPrimary
import rememberMessageBarState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeGraphScreen(
    modifier: Modifier = Modifier,
    navigateToGameDetail: (String) -> Unit,
    coins: String
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
                TopAppBar(
                    title = {
                        AnimatedContent(
                            targetState = selectedDestination
                        ) { destination ->
                            Text(
                                text = destination.title,
                                fontFamily = BebasNeueFont(),
                                fontSize = FontSize.LARGE,
                                color = TextPrimary
                            )
                        }
                    },
                    navigationIcon = {

                        AnimatedVisibility(
                            visible = selectedDestination != BottomBarDestination.Games
                        ) {

                            IconButton(
                                onClick = {
                                    navController.navigate(Screen.Games)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Menu",
                                    tint = IconPrimary
                                )
                            }
                        }

//                        IconButton(
//                            onClick = {
//                                navController.navigate(Screen.Games)
//                            }
//                        ) {
//                            AnimatedVisibility(
//                                visible = selectedDestination == BottomBarDestination.Games
//                            ) {
//                                Icon(
//                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                                    contentDescription = "Menu",
//                                    tint = IconPrimary
//                                )
//                            }
//                        }
                    },
                    actions = {

                        StatChip(icon = "💰", label = "Coins", value = coins)

                        IconButton(
                            onClick = {
                                navController.navigate(Screen.Profile)
                            }
                        ) {
                            AnimatedVisibility(
                                visible = selectedDestination == BottomBarDestination.Games
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Menu",
                                    tint = IconPrimary
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Surface,
                        scrolledContainerColor = Surface,
                        navigationIconContentColor = IconPrimary,
                        titleContentColor = TextPrimary,
                        actionIconContentColor = IconPrimary
                    )
                )
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
                            GamesScreen(
                                onGameClick = navigateToGameDetail,
                                games = gameItems
                            )
                        }
                        composable<Screen.Profile> {

                            val context = LocalContext.current
                            val activity = context as Activity

                            val statsViewModel: StatsViewModel = hiltViewModel()
                            val profile by statsViewModel.profile.collectAsState() // Use Flow/LiveData/State
                            val perGameStats by statsViewModel.perGameStats.collectAsState()

                            val userId = statsViewModel.userId.value
                            val us = "d41e5130-eacf-401a-bd03-e0cb4c0c9a96"
                            Log.d("User", userId.toString())

//                            val rewardedAdManager = remember {
//                                RewardedAdManager(context, "ca-app-pub-3940256099942544/5224354917")
//                            }

                            ProfileScreen(
                                profile = profile
                                    ?: OverallProfileEntity(userId = "1\tc97f320d-4681-4e07-aeca-f305ea33d7e9\tsudoku\t2\t0\t2\t0\t20\t1\t3\t1\t6\t20",),
                                perGameStats = perGameStats,
                            )
                        }

                    }



                }

            }
        }

    }

}







