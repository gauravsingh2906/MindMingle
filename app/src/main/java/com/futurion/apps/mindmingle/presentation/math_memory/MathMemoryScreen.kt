package com.futurion.apps.mindmingle.presentation.math_memory

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.futurion.apps.mindmingle.GoogleRewardedAdManager
import com.futurion.apps.mindmingle.R
import com.futurion.apps.mindmingle.domain.model.AnswerOption
import com.futurion.apps.mindmingle.domain.model.GameTheme
import com.futurion.apps.mindmingle.domain.model.MemoryCard
import com.futurion.apps.mindmingle.domain.model.Op
import com.futurion.apps.mindmingle.presentation.game_result.StatCard
import com.futurion.apps.mindmingle.presentation.games.SampleGames.Default
import com.futurion.apps.mindmingle.presentation.profile.StatsViewModel
import com.futurion.apps.mindmingle.presentation.utils.BebasNeueFont
import com.futurion.apps.mindmingle.presentation.utils.FontSize
import com.futurion.apps.mindmingle.presentation.utils.IconPrimary
import com.futurion.apps.mindmingle.presentation.utils.Resources
import com.futurion.apps.mindmingle.presentation.utils.Surface
import com.futurion.apps.mindmingle.presentation.utils.TextPrimary
import kotlinx.coroutines.delay


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

    val phase = when {
        uiState.game.isShowCards -> "MEMORIZE"
        !uiState.game.showResult -> "SOLVE"
        else -> "RESULT"
    }

    LaunchedEffect(uiState.game.level, uiState.game.isShowCards) {
        if (uiState.game.isShowCards) {
            val baseDelay = 2400L
            val perCardDelay = 600L
            viewModel.startMemorizationTimer(baseDelay + uiState.game.level.cards.size * perCardDelay)
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
        viewModel.currentStreak.value >= 3 -> 20
        else -> 0
    }

    val context = LocalContext.current
    val activity = context as? Activity
    val googleAdManager = GoogleRewardedAdManager(context)
    val adMobAdUnitId = "ca-app-pub-3940256099942544/5224354917"

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

    Scaffold(
        containerColor = Surface
    ) { it->
        Box(modifier = Modifier.fillMaxSize().padding(it)) {
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
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ThemeSelector(
                    builtInThemes = Default,
                    selectedTheme = theme,
                    unlockedNames = uiState.theme.unlockedThemes,
                    onSelect = { viewModel.onAction(MathMemoryAction.SelectTheme(it)) }
                )
                IconButton(
                    onClick = { navigateToThemeUnlock(userId) }
                ) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = "Unlock Theme")
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text = "Level ${uiState.game.level.number}",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 10.dp),
                    color = if (phase == "RESULT" && isCorrect) Color(0xFF388E3C) else theme.textColor
                )

                when (phase) {
                    "MEMORIZE" -> Text(
                        text = "👀 Memorize the moves! Start from:",
                        style = MaterialTheme.typography.titleMedium,
                        color = theme.textColor,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    "SOLVE" -> Text(
                        text = "Now solve: Start at the number below, do each step left-to-right (no BODMAS)!",
                        style = MaterialTheme.typography.titleMedium,
                        color = theme.textColor,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    else -> Unit
                }

                StartNumberBox(
                    value = uiState.game.level.start,
                    textColor = theme.buttonTextColor,
                    bgColor = theme.buttonColor
                )
                Spacer(Modifier.height(10.dp))

                if (phase == "MEMORIZE") {
                    CardsRow(cards = uiState.game.level.cards, textColor = theme.textColor)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Try to remember all the moves and the start number!",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (phase == "SOLVE") {
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
                        text = "Tip: Start at ${uiState.game.level.start}, do every step one after another.",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (!secondChanceUsed) {
                        Button(
                            onClick = {
                                googleAdManager.loadRewardedAd(adMobAdUnitId) { loaded ->
                                    if (loaded && activity != null) {
                                        googleAdManager.showRewardedAd(
                                            activity, onUserEarnedReward = {
                                                viewModel.onAction(MathMemoryAction.HideCards)
                                                viewModel.useHint()
                                            }, onClosed = {}
                                        )
                                    }
                                }

                                secondChanceUsed = true
                                showMemorizePhaseAgain = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD600))
                        ) {
                            Text("👁 Watch Ad to See Moves Again")
                        }
                    }
                }
            }

            if (phase == "RESULT") {
                ResultFullScreen(
                    isCorrect = uiState.game.isCorrect,
                    level = uiState.game.level.number,
                    totalXp = totalXp,
                    totalCoins = totalCoins,
                    xpEarned = viewModel.getXpForLevel(uiState.game.level.number),
                    coinsEarned = coinsEarned,
                    streak = viewModel.currentStreak.value,
                    bestStreak = viewModel.bestStreak.value,
                    onNext = { viewModel.onAction(MathMemoryAction.NextLevel) },
                    onRetry = { viewModel.onAction(MathMemoryAction.ResetGame) },
                    navigateToGames = navigateToGames
                )
            }
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

                IconButton(onClick = navigateToGames) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Unlock Theme"
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                val xpAnim by animateIntAsState(
                    targetValue = totalXp,
                    animationSpec = tween(700)
                )

                StatBadge(
                    icon = Icons.Default.Star,
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
                    icon = Icons.Default.AccountBox,
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
                        Icon(
                            painter = if (isCorrect) painterResource(R.drawable.book) else painterResource(
                                R.drawable.weight
                            ),
                            contentDescription = "Trophy",
                            tint = if (isCorrect) Color(0xFFFFD700) else Color(
                                0xFFFF5252
                            ),
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
                            icon = painterResource(R.drawable.close),
                            iconColor = Color(0xFFFFD700),
                            title = "XP EARNED",
                            value = "+${xpEarned}",
                            subtitle = "This Level"
                        )

                        StatCard(
                            modifier = Modifier.weight(1f),
                            icon = painterResource(R.drawable.book),
                            iconColor = Color(0xFFFF9800),
                            title = "COINS EARNED",
                            value = "+${coinsEarned}",
                            subtitle = "This Level"
                        )
                    }

                    StatCard(
                        modifier = Modifier.fillMaxWidth(),
                        icon = painterResource(R.drawable.grid),
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
fun AnimatedStatCounter(
    value: Int,
    label: String,
    color: Color = Color(0xFFFFD700)
) {
    val animatedValue by animateIntAsState(
        targetValue = value,
        animationSpec = tween(durationMillis = 800, easing = LinearOutSlowInEasing)
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Text(
            text = animatedValue.toString(),
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        )
    }
}

// Keep your StatChip composable from your current code unchanged


// The following can be outside or in separate file ----------------------------------
@Composable
fun StartNumberCircle(value: Int, textColor: Color, bgColor: Color) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(bgColor)
            .border(3.dp, textColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value.toString(),
            color = textColor,
            style = MaterialTheme.typography.headlineLarge
        )
    }
}

@Composable
private fun StatBadge(
    icon: ImageVector,
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
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(16.dp)
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
fun StartNumberBox(value: Int, textColor: Color, bgColor: Color) {
    Card(
        shape = RoundedCornerShape(13.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Text(
            "Start: $value",
            color = textColor,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 23.dp, vertical = 6.dp)
        )
    }
}

//@Composable
//fun StatChip(label: String, value: Int) {
//    ElevatedCard(
//        shape = RoundedCornerShape(14.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
//        modifier = Modifier.size(width = 70.dp, height = 42.dp)
//    ) {
//        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
//            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
//            Text(value.toString(), style = MaterialTheme.typography.titleMedium, color = Color.Black)
//        }
//    }
//}

@Composable
fun CardsRow(cards: List<MemoryCard>, textColor: Color) {
    val rows = (cards.size + 4) / 5 // Show 5 per row, adapt as needed
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        for (row in 0 until rows) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                val thisRow = cards.drop(row * 5).take(5)
                thisRow.forEach { card ->
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.94f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (card.op) {
                                Op.ADD -> "+${card.value}"
                                Op.SUB -> "-${card.value}"
                                Op.MUL -> "×${card.value}"
                                Op.DIV -> "÷${card.value}"
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

//@Composable
//fun AnimatedStatCounter(
//    value: Int,
//    label: String,
//    color: Color = Color(0xFFFFD700)
//) {
//    val animatedValue by animateIntAsState(
//        targetValue = value,
//        animationSpec = tween(durationMillis = 800, easing = LinearOutSlowInEasing)
//    )
//    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//        Text(
//            text = label,
//            color = Color.Gray,
//            fontWeight = FontWeight.Bold,
//            fontSize = 14.sp
//        )
//        Text(
//            text = animatedValue.toString(),
//            color = color,
//            fontWeight = FontWeight.Bold,
//            fontSize = 32.sp
//        )
//    }
//}


@Composable
fun ResultSection(
    isCorrect: Boolean,
    onNext: () -> Unit,
    onRetry: () -> Unit,
    textColor: Color,
    buttonColor: Color,
    buttonTextColor: Color,
    streak: Int,
    bestStreak: Int,
    xp: Int,
    coins: Int,
    totalXp: Int,
    totalCoin: Int
) {
    val color = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFD32F2F)
    val message = if (isCorrect) "🎉 Correct!" else "Try Again!"



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Total XP
            AnimatedStatCounter(value = totalXp, label = "XP", color = Color(0xFFFFD700))
            Spacer(modifier = Modifier.width(12.dp))
            AnimatedStatCounter(value = totalCoin, label = "Coins", color = Color(0xFFFF9800))

        }
        Text(
            text = message,
            color = color,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatChip(label = "Streak", value = streak)
            StatChip(label = "Best", value = bestStreak)
            StatChip(label = "XP", value = xp)
            StatChip(label = "Coins", value = coins)
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (isCorrect) {
            Button(
                onClick = onNext,
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = buttonTextColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Next Level", color = textColor)
            }
        } else {
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = buttonTextColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Restart", color = buttonTextColor)
            }
        }
    }
}

@Composable
fun AnimatedUnlockBanner(themeName: String, onContinue: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            SimpleLottieRawAnimation(
                rawRes = R.raw.cycle, // Place your animation under res/raw
                modifier = Modifier.size(140.dp)
            )
            Spacer(Modifier.height(18.dp))
            Text(
                text = "🎉 New Theme Unlocked!",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            Text(
                text = themeName,
                style = MaterialTheme.typography.titleLarge,
                color = Color.Yellow,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            Button(
                onClick = onContinue,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Continue")
            }
        }
    }
}


@Composable
fun SimpleLottieRawAnimation(rawRes: Int, modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(rawRes))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 4    // Play once for reward, or use LottieConstants.IterateForever
    )
    LottieAnimation(
        isPlaying = true,
        modifier = modifier,
        composition = composition,
    )
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

