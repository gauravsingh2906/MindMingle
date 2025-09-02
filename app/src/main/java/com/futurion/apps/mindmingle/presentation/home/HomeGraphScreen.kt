package com.futurion.apps.mindmingle.presentation.home

import ContentWithMessageBar
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.futurion.apps.mindmingle.R
import com.futurion.apps.mindmingle.presentation.games.GameGridItem
import com.futurion.apps.mindmingle.presentation.games.GamesScreen
import com.futurion.apps.mindmingle.presentation.home.component.BottomBarNavigation
import com.futurion.apps.mindmingle.presentation.home.domain.BottomBarDestination
import com.futurion.apps.mindmingle.presentation.mind_mingle.MindMingleScreen
import com.futurion.apps.mindmingle.presentation.navigation.Screen
import com.futurion.apps.mindmingle.presentation.profile.ProfileScreen
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
                    actions = {
                        IconButton(
                            onClick = {}
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = IconPrimary
                            )
                        }
                        IconButton(
                            onClick = {}
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = IconPrimary
                            )
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
                        startDestination = Screen.Home
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
                            ProfileScreen(

                            )
                        }

                    }

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 12.dp, top = 2.dp)
                    ) {
                        BottomBarNavigation(
                            selected = selectedDestination,
                            onSelect = { destination ->
                                navController.navigate(destination.screen) {
                                    launchSingleTop = true
                                    popUpTo<Screen.Profile> {
                                        saveState = true
                                        inclusive = false
                                    }
                                    restoreState = true
                                }
                            }
                        )
                    }

                }

            }
        }

    }

}

val gameItems = listOf(
    GameGridItem(
        name = "Sudoku",
        description = "Sharpen your mind with logic puzzles",
        imageResId = R.drawable.figma_sudoku,
        cardColor = Color(0xFF56CCF2), // Light Blue
        xp = 1200,
        coins = 500,
        id = "sudoku"
    ),
    GameGridItem(
        name = "Math Memory",
        description = "Boost your memory and math skills",
        imageResId = R.drawable.fourthone,
        cardColor = Color(0xFF6A0572), // Purple
        xp = 1500,
        coins = 700,
        id = "math_memory",
    ),
    GameGridItem(
        name = "Algebra Quest",
        description = "Master algebra through fun challenges",
        imageResId = R.drawable.fourthone,
        cardColor = Color(0xFFEF476F), // Pinkish Red
        xp = 900,
        coins = 400,
        id = "algebra",
    ),
    GameGridItem(
        name = "New Game 1",
        description = "Coming soon",
        imageResId = R.drawable.fourthone,
        cardColor = Color.Black,
        xp = 0,
        coins = 0,
        isComingSoon = true,
        id = "4"
    ),
    GameGridItem(
        name = "New Game 2",
        description = "Coming soon",
        imageResId = R.drawable.fourthone,
        cardColor = Color.Black,
        xp = 0,
        coins = 0,
        isComingSoon = true,
        id = "5"
    )
)





