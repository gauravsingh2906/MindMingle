package com.futurion.apps.mathmingle.presentation.math_memory

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.futurion.apps.mathmingle.GoogleRewardedAdManager
import com.futurion.apps.mathmingle.R
import com.futurion.apps.mathmingle.domain.model.AnswerOption
import com.futurion.apps.mathmingle.domain.model.GameTheme
import com.futurion.apps.mathmingle.domain.model.MemoryCard
import com.futurion.apps.mathmingle.domain.model.Operations
import com.futurion.apps.mathmingle.presentation.game_result.StatCard
import com.futurion.apps.mathmingle.presentation.games.SampleGames.Default
import com.futurion.apps.mathmingle.presentation.profile.StatsViewModel
import com.futurion.apps.mathmingle.presentation.utils.Constants
import kotlinx.coroutines.delay

private enum class TutorialPhase { WELCOME, MEMORIZE, SOLVE, RESULT }


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MathMemoryScreen(
    viewModel: MathMemoryViewModel = hiltViewModel(),
    statsViewModel: StatsViewModel = hiltViewModel(),
    navigateToThemeUnlock: (String) -> Unit,
    navigateToGames: () -> Unit
) {
    val profile by statsViewModel.profile.collectAsState()

    val totalXp by viewModel.totalXp.collectAsState()
    val totalCoins by viewModel.totalCoins.collectAsState()

    val uiState by viewModel.uiState
    val answerOptions by viewModel.answerOptions
    val theme = uiState.theme.selectedTheme
    val userId = statsViewModel.userId.value ?: "4045582c-f745-4822-a1d2-d882132176d8"

    var tutPhase by remember(uiState.game.level.number) {
        mutableStateOf(if (uiState.game.level.number == 1) TutorialPhase.WELCOME else TutorialPhase.RESULT)
    }

    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler {
        showExitDialog = true
    }



//    LaunchedEffect(uiState.game.level, uiState.game.isShowCards) {
//        if (uiState.game.isShowCards) {
//            val baseDelay = 2400L
//            val perCardDelay = 600L
//            viewModel.startMemorizationTimer1(baseDelay + uiState.game.level.cards.size * perCardDelay)
//        }
//    }
    LaunchedEffect(uiState.game.level, uiState.game.isShowCards) {
        if (uiState.game.isShowCards) {
            val totalTime = viewModel.getMemorizationTime(
                levelNumber = uiState.game.level.number,
                numCards = uiState.game.level.cards.size
            )
            viewModel.startMemorizationTimer1(totalTime)
        }
    }


    var secondChanceUsed by remember(uiState.game.level.number) { mutableStateOf(false) }
    var showMemorizePhaseAgain by remember { mutableStateOf(false) }
    val showUnlockAnimation by remember { derivedStateOf { viewModel.showUnlockAnimation } }
    val themeName by remember { derivedStateOf { viewModel.newlyUnlockedThemeName } }

    LaunchedEffect(Unit) {
        viewModel.loadOrInitMathMemoryLevel()
    }

    val gameLevel = uiState.game.level.number
    val showResult = uiState.game.showResult
    val isCorrect = uiState.game.isCorrect

    var lastSavedLevel by remember { mutableStateOf<Int?>(null) }

    val coinsEarned = when {
        viewModel.currentStreak.value > viewModel.bestStreak.value -> 50
        viewModel.currentStreak.value == 3 || viewModel.currentStreak.value == 5 -> 20
        else -> 0
    }

    val context = LocalContext.current
    val activity = context as? Activity
    val googleAdManager = GoogleRewardedAdManager(context, Constants.AD_Unit)

    if (showResult && lastSavedLevel != gameLevel) {
        LaunchedEffect(gameLevel, showResult) {
            viewModel.onLevelResultAndSaveStats(
                userId = statsViewModel.userId.value ?: "123",
                isCorrect = isCorrect,
                hintsUsed = viewModel.hintsUsed.value,
                timeSpentSeconds = 1,
                coinsEarned = coinsEarned,
                currentStreak = viewModel.currentStreak.value,
                bestStreak = viewModel.bestStreak.value,
            )
            lastSavedLevel = gameLevel
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Exit Game") },
            text = { Text("Are you sure you want to exit? Your progress will be saved to resume later.") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    viewModel.onLevelResultAndSaveStats(
                        userId = statsViewModel.userId.value ?: "123",
                        isCorrect = isCorrect,
                        hintsUsed = viewModel.hintsUsed.value,
                        timeSpentSeconds = 1,
                        coinsEarned = coinsEarned,
                        currentStreak = viewModel.currentStreak.value,
                        bestStreak = viewModel.bestStreak.value,
                    )
                    navigateToGames()
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("No")
                }
            }
        )
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .navigationBarsPadding()
    ) {
        // background + overlay
        Image(
            painter = painterResource(theme.backgroundImage),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.44f))
        )

        // Determine whether header should be visible.
        // Header visible only during MEMORIZE and SOLVE (tutorial or normal).
        val tutorialMemorizeOrSolve =
            uiState.game.level.number == 1 && (tutPhase == TutorialPhase.MEMORIZE || tutPhase == TutorialPhase.SOLVE)
        val normalMemorizeOrSolve =
            uiState.game.level.number > 1 && (uiState.game.isShowCards || !uiState.game.showResult)
        val showHeader = tutorialMemorizeOrSolve || normalMemorizeOrSolve

        // Outer column: header (wrap content) + phase box (fills remaining)
        Column(modifier = Modifier.fillMaxSize()) {

            // Header: only shown during MEMORIZE and SOLVE
            if (showHeader) {
                Column(
                    Modifier
                        .wrapContentHeight()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                        ThemeSelector(
                            builtInThemes = Default,
                            selectedTheme = theme,
                            unlockedNames = uiState.theme.unlockedThemes,
                            onSelect = { viewModel.onAction(MathMemoryAction.SelectTheme(it)) }
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center // Top right
                        ) {
                            IconButton(
                                onClick = { navigateToThemeUnlock(userId) },
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color.White.copy(alpha = 0.9f), CircleShape)
                                    .border(1.dp, Color.Gray, CircleShape)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.theme), // Replace with your theme icon
                                    contentDescription = "Theme Selector",
                                    tint = Color(0xFF6A8BFF)
                                )
                            }
                        }





                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = "Level ${uiState.game.level.number}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = if ((uiState.game.level.number >= 2 && uiState.game.showResult && isCorrect) ||
                            (uiState.game.level.number == 1 && tutPhase == TutorialPhase.RESULT && isCorrect)
                        ) Color(0xFF388E3C) else theme.textColor
                    )
                }
            }

            // Phase area - fills all remaining space. If header is hidden, this is full screen.
            Box(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                // Tutorial (level 1)
                if (uiState.game.level.number == 1) {
                    when (tutPhase) {
                        TutorialPhase.WELCOME -> {
                            WelcomeTutorial(onStart = { tutPhase = TutorialPhase.MEMORIZE })
                        }

                        TutorialPhase.MEMORIZE -> {
                            // Keep header visible in MEMORIZE (we already did)
                            TutorialMemorize(
                                startNumber = uiState.game.level.start,
                                onProceed = { tutPhase = TutorialPhase.SOLVE }
                            )
                        }

                        TutorialPhase.SOLVE -> {
                            // The Solve tutorial view (header is visible)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Step 2: Tap the correct result!",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = theme.textColor,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )


                                Spacer(Modifier.height(16.dp))

                                AnswerOptionsColumn(
                                    options = answerOptions,
                                    onSelect = { selected ->
                                        viewModel.onAction(MathMemoryAction.InputChanged(selected.value.toString()))
                                        viewModel.onAction(MathMemoryAction.SubmitAnswer)
                                        tutPhase = TutorialPhase.RESULT
                                    },
                                    selectedTheme = theme
                                )

                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Hint: start at ${uiState.game.level.start} and apply each step.",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        TutorialPhase.RESULT -> {
                            // RESULT: hide header and show full-screen result
                            ResultFullScreen(
                                isCorrect = uiState.game.isCorrect,
                                level = uiState.game.level.number,
                                totalXp = viewModel.totalXp.collectAsState().value,
                                totalCoins = viewModel.totalCoins.collectAsState().value,
                                xpEarned = viewModel.getXpForLevel(uiState.game.level.number),
                                coinsEarned = coinsEarned,
                                streak = viewModel.currentStreak.collectAsState().value,
                                bestStreak = viewModel.bestStreak.collectAsState().value,
                                onNext = { viewModel.onAction(MathMemoryAction.NextLevel) },
                                onRetry = {
                                    viewModel.onAction(MathMemoryAction.ResetGame)
                                    tutPhase = TutorialPhase.WELCOME
                                },
                                navigateToGames = navigateToGames
                            )
                        }
                    }
                } else {
                    // Normal flow (level >= 2)
                    val normalPhase = when {
                        uiState.game.isShowCards -> "MEMORIZE"
                        !uiState.game.showResult -> "SOLVE"
                        else -> "RESULT"
                    }

                    when (normalPhase) {
                        "MEMORIZE" -> {
                            // Header visible here
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = viewModel.remainingTime.collectAsState().value.toString(),
                                    style = androidx.compose.ui.text.TextStyle(
                                        fontSize = 72.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )

                                Text(
                                    text = "👀 Memorize the moves left to right",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = theme.textColor,
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )

                                Spacer(Modifier.height(10.dp))

                                CardsRow1(
                                    startNumber = uiState.game.level.start,
                                    cards = uiState.game.level.cards,
                                    textColor = theme.textColor
                                )

                                Spacer(Modifier.height(10.dp))
                                Text(
                                    "Try to remember all the moves",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        "SOLVE" -> {
                            // Header visible here as well
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Now solve: do steps left→right (no BODMAS).",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = theme.textColor,
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )

                                Spacer(Modifier.height(16.dp))

                                AnswerOptionsColumn(
                                    options = answerOptions,
                                    onSelect = { selected ->
                                        viewModel.onAction(MathMemoryAction.InputChanged(selected.value.toString()))
                                        viewModel.onAction(MathMemoryAction.SubmitAnswer)
                                    },
                                    selectedTheme = theme
                                )

                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Tip: Start at ${uiState.game.level.start}, apply every step in order.",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(Modifier.height(8.dp))

                                if (!secondChanceUsed) {
                                    Button(
                                        onClick = {
                                            if (activity != null) {
                                                googleAdManager.showRewardedAd(
                                                    activity,
                                                    onUserEarnedReward = {
                                                        viewModel.onAction(MathMemoryAction.HideCards)
                                                        viewModel.useHint()
                                                        secondChanceUsed = true
                                                        showMemorizePhaseAgain = true
                                                    },
                                                    onClosed = {
                                                        if (!secondChanceUsed) {
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
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFFFD600)
                                        )
                                    ) {
                                        Text("👁 Watch Ad to See Moves Again")
                                    }
                                }

                            }
                        }

                        else -> {
                            // RESULT - hide header (Box is full size) and show ResultFullScreen
                            ResultFullScreen(
                                isCorrect = uiState.game.isCorrect,
                                level = uiState.game.level.number,
                                totalXp = totalXp,
                                totalCoins = totalCoins,
                                xpEarned = viewModel.getXpForLevel(uiState.game.level.number),
                                coinsEarned = coinsEarned,
                                streak = viewModel.currentStreak.collectAsState().value,
                                bestStreak = viewModel.bestStreak.collectAsState().value,
                                onNext = { viewModel.onAction(MathMemoryAction.NextLevel) },
                                onRetry = { viewModel.onAction(MathMemoryAction.ResetGame) },
                                navigateToGames = navigateToGames
                            )
                        }
                    }
                }
            }

        }
    }
}

fun getMemorizationTime(levelNumber: Int, numCards: Int): Long {
    val baseTime = 2000L                 // 2 seconds minimum
    val perCardTime = 500L               // extra per card
    val difficultyFactor = 1 + levelNumber / 10f // higher levels slightly slower
    return ((baseTime + numCards * perCardTime) * difficultyFactor).toLong()
}



@Composable
private fun WelcomeTutorial(onStart: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1976D2))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "👋 Welcome to Math Memory!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "In this quick tutorial you'll see the numbers and learn how to answer.",
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(Modifier.height(48.dp))
            Button(onClick = onStart) {
                Text("Start Tutorial →")
            }
        }
    }
}

@Composable
private fun TutorialMemorize(
    viewModel: MathMemoryViewModel = hiltViewModel(),
    startNumber: Int,
    onProceed: () -> Unit
) {

    val uiState = viewModel.uiState.value


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Solve the equation from left to right ",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )


        CardsRow1(
            startNumber = startNumber,
            cards = uiState.game.level.cards,
            textColor = uiState.theme.selectedTheme.textColor
        )

        Spacer(modifier = Modifier.height(24.dp))


        Button(onClick = onProceed) {
            Text("I Got It →")
        }
    }
}


@Composable
fun ResultFullScreen(
    isCorrect: Boolean,
    level: Int,
    totalXp: Int,
    totalCoins: Int,
    xpEarned: Int,
    coinsEarned: Int,
    streak: Int,
    bestStreak: Int,
    onNext: () -> Unit,
    onRetry: () -> Unit,
    navigateToGames: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }


    // Trigger entrance animation
    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
    }

    BackHandler {
        navigateToGames()
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF724DD3)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    Modifier
                        .clickable(onClick = navigateToGames)
                        .background(
                            shape = CircleShape,
                            color = Color.White
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = navigateToGames) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Unlock Theme"
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                val xpAnim by animateIntAsState(
                    targetValue = totalXp,
                    animationSpec = tween(700)
                )

                StatBadge(
                    icon = painterResource(R.drawable.xp_figma),
                    iconColor = Color(0xFFFFD700),
                    value = xpAnim.toString(),
                    label = "XP"
                )

                Spacer(modifier = Modifier.width(12.dp))

                val coinsAnim by animateIntAsState(
                    targetValue = totalCoins,
                    animationSpec = tween(700)
                )

                // Total Coins
                StatBadge(
                    icon = painterResource(R.drawable.figma_coin),
                    iconColor = Color(0xFFFF9800),
                    value = coinsAnim.toString(),
                    label = "Coins"
                )

//                Column {
//                    Text(
//                        "XP",
//                        fontSize = 16.sp,
//                        color = Color(0xFFFFD700),
//                        fontWeight = FontWeight.Bold
//                    )
//                    val xpAnim by animateIntAsState(
//                        targetValue = totalXp,
//                        animationSpec = tween(700)
//                    )
//                    Text(
//                        xpAnim.toString(),
//                        fontSize = 42.sp,
//                        color = Color(0xFFFFD700),
//                        fontWeight = FontWeight.ExtraBold
//                    )
//                }
//                Column {
//                    Text(
//                        "Coins",
//                        fontSize = 16.sp,
//                        color = Color(0xFFFF9800),
//                        fontWeight = FontWeight.Bold
//                    )
//                    val coinsAnim by animateIntAsState(
//                        targetValue = totalCoins,
//                        animationSpec = tween(700)
//                    )
//                    Text(
//                        coinsAnim.toString(),
//                        fontSize = 42.sp,
//                        color = Color(0xFFFF9800),
//                        fontWeight = FontWeight.ExtraBold
//                    )
//                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(800, easing = FastOutSlowInEasing)
                ) + fadeIn(tween(800))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Trophy Icon
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                Color.White.copy(alpha = 0.15f),
                                CircleShape
                            )
                            .border(
                                2.dp,
                                Color.White.copy(alpha = 0.3f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = if (isCorrect) painterResource(R.drawable.figma_trophy) else painterResource(
                                R.drawable.warning
                            ),
                            contentDescription = "Trophy",
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (isCorrect) "\uD83C\uDF89 Correct!" else "Better Luck Next Time",
                        textAlign = TextAlign.Center,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isCorrect) "LEVEL $level COMPLETED" else "",
                        fontSize = 22.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(1000, delayMillis = 400)
                ) + fadeIn(tween(1000, delayMillis = 400))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // XP and Coins Row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            icon = painterResource(R.drawable.xp_figma),
                            iconColor = Color(0xFFFFD700),
                            title = "XP EARNED",
                            value = "+${if (isCorrect) xpEarned else 10}",
                            subtitle = "This Level"
                        )

                        StatCard(
                            modifier = Modifier.weight(1f),
                            icon = painterResource(R.drawable.figma_coin),
                            iconColor = Color(0xFFFF9800),
                            title = "COINS EARNED",
                            value = "+${coinsEarned}",
                            subtitle = "This Level"
                        )
                    }

                    StatCard(
                        modifier = Modifier.fillMaxWidth(),
                        icon = painterResource(R.drawable.fire_icon),
                        iconColor = Color(0xFFFF5722),
                        title = "STREAK POWER",
                        value = "${streak} / ${bestStreak}",
                        subtitle = "Current / Best Streak"
                    )

                }
            }

//            Text(
//                text = if (isCorrect) "\uD83C\uDF89 Correct!" else "Better Luck Next Time",
//                fontSize = 34.sp,
//                fontWeight = FontWeight.ExtraBold,
//                color = if (isCorrect) Color(0xFF34B233) else Color.White
//            )
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            Text(
//                "LEVEL $level",
//                fontSize = 22.sp,
//                color = Color.White,
//                fontWeight = FontWeight.Bold,
//                letterSpacing = 1.5.sp
//            )


            Spacer(modifier = Modifier.height(40.dp))


            Button(
                onClick = if (isCorrect) onNext else onRetry,
                shape = RoundedCornerShape(36.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(
                    if (isCorrect) "Next Level" else "Restart",
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun StatChip(label: String, value: Int) {
    ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.size(width = 110.dp, height = 60.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                label,
                fontSize = 14.sp,
                color = Color(0xFF6246D6),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                value.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}


@Composable
private fun StatBadge(
    icon: Painter,
    iconColor: Color,
    value: String,
    label: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Image(
                painter = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}


@Composable
fun CardsRow1(
    cards: List<MemoryCard>,
    textColor: Color,
    startNumber: Int? = null // NEW: Accept start number optionally
) {
    // New: Create a composable for start number card
    @Composable
    fun StartNumberCard(number: Int) {
        Box(
            Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Black.copy(alpha = 0.94f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                color = textColor,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

    val allCards: List<Any> = if (startNumber != null) {
        listOf(startNumber) + cards
    } else {
        cards
    }

    val rows = (allCards.size + 4) / 5 // Show 5 per row, adapt as needed
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        for (row in 0 until rows) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                val thisRow = allCards.drop(row * 5).take(5)
                thisRow.forEach { item ->
                    if (item is Int) {
                        StartNumberCard(item)
                    } else if (item is MemoryCard) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.94f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (item.op) {
                                    Operations.ADD -> "+${item.value}"
                                    Operations.SUB -> "-${item.value}"
                                    Operations.MUL -> "×${item.value}"
                                    Operations.DIV -> "÷${item.value}"
                                },
                                color = textColor,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun AnswerOptionsColumn(
    options: List<AnswerOption>,
    onSelect: (AnswerOption) -> Unit,
    selectedTheme: GameTheme
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        options.forEach { option ->
            Button(
                onClick = {
                    onSelect(option)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = selectedTheme.buttonColor,
                    contentColor = selectedTheme.buttonTextColor
                )
            ) {
                Text(
                    text = option.value.toString(),
                    color = selectedTheme.buttonTextColor,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}


@Composable
fun ThemeSelector(
    builtInThemes: List<GameTheme>,
    selectedTheme: GameTheme,
    unlockedNames: Set<String>,
    onSelect: (GameTheme) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        builtInThemes.forEach { theme ->
            val unlocked = theme.name in unlockedNames
            val borderColor = if (selectedTheme.name == theme.name)
                theme.buttonColor
            else Color.Gray
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .padding(6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        3.dp, borderColor, RoundedCornerShape(12.dp)
                    )
                    .clickable(enabled = unlocked) { onSelect(theme) },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(theme.backgroundImage),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
                if (!unlocked)
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color(0xBB000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔒", color = Color.White)
                    }

            }
        }
    }
}

//prop improve layout of memorize