package com.futurion.apps.mindmingle.presentation.game_result

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.futurion.apps.mindmingle.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.futurion.apps.mindmingle.domain.model.AllGames
import com.futurion.apps.mindmingle.domain.model.UniversalResult
import com.futurion.apps.mindmingle.presentation.math_memory.MathMemoryAction
import com.futurion.apps.mindmingle.presentation.math_memory.MathMemoryViewModel


import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin


@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameResultScreen(
    data: UniversalResult,
    currentLevel: Int = 1,
    gameType: AllGames = AllGames.ALGEBRA,
    currentDifficulty: String = "Easy",
    highestLevelCompleted: Int = 1,
    viewModel: MathMemoryViewModel = hiltViewModel(),
    onReplay: (Int, String) -> Unit = { _, _ -> },
    onNext: (Int, String) -> Unit = { _, _ -> },
    onHome: () -> Unit = {},
    navigateToGameDetail: (String) -> Unit,
    navigateToMathMemory: (Int) -> Unit,
) {
    var isVisible by remember { mutableStateOf(false) }

    val gameTypeq = when (gameType) {
        AllGames.ALGEBRA -> "algebra"
        AllGames.SUDOKU -> "sudoku"
        AllGames.MEMORY -> "math_memory"
    }


    BackHandler {
        navigateToGameDetail(gameTypeq)
    }


    // Trigger entrance animation
    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
    }

    val canProgressToNext = data.isMatchWon == true && when (gameType) {
        AllGames.ALGEBRA -> currentLevel >= highestLevelCompleted
        AllGames.SUDOKU -> true // Sudoku can always replay or try harder difficulty
        AllGames.MEMORY -> true
    }

    // Main gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = when (gameType) {
                        AllGames.ALGEBRA -> listOf(
                            Color(0xFF667eea),
                            Color(0xFF764ba2),
                            Color(0xFF667eea)
                        )

                        AllGames.SUDOKU -> listOf(
                            Color(0xFF4facfe),
                            Color(0xFF00f2fe),
                            Color(0xFF4facfe)
                        )

                        AllGames.MEMORY -> listOf(  // ✅ New Memory Mix colors
                            Color(0xFFf093fb),
                            Color(0xFFf5576c),
                            Color(0xFFf093fb)
                        )
                    },
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
            .systemBarsPadding()
            .navigationBarsPadding()

    ) {

        // Floating particles background
        AnimatedParticles()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
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
                        .clickable(onClick = onHome)
                        .background(
                            shape = CircleShape,
                            color = Color.White
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onHome) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Unlock Theme"
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Total XP
                StatBadge(
                    icon = painterResource(R.drawable.xp_figma),
                    iconColor = Color(0xFFFFD700),
                    value = data.xpEarned.toString(),
                    label = "XP"
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Total Coins
                StatBadge(
                    icon = painterResource(R.drawable.figma_coin),
                    iconColor = Color(0xFFFF9800),
                    value = data.coinsEarned.toString(),
                    label = "Coins"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🎉 Success Title with Animation
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
                            painter = if (data.isMatchWon == true) painterResource(R.drawable.figma_trophy) else painterResource(
                                R.drawable.weight
                            ),
                            contentDescription = "Trophy",
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = data.resultTitle.uppercase(),
                        textAlign = TextAlign.Center,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )

//                    Text(
//                        text = data.resultMessage,
//                        textAlign = TextAlign.Center,
//                        fontSize = 32.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = Color.White,
//                        letterSpacing = 2.sp
//                    )

                    Text(
                        text = when (gameType) {
                            AllGames.ALGEBRA -> if (data.isMatchWon == true) "LEVEL $currentLevel COMPLETED" else "Better Luck Next Time!"
                            AllGames.SUDOKU -> "${currentDifficulty.uppercase()} SUDOKU ${if (data.won == true) "COMPLETED" else "Better Luck Next Time!"}"
                            AllGames.MEMORY -> if (data.isMatchWon == true) "LEVEL $currentLevel COMPLETED" else "Better Luck Next Time!"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.8f),
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 📊 Stats Cards
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(1000, delayMillis = 400)
                ) + fadeIn(tween(1000, delayMillis = 400))
            ) {
                when (gameType) {
                    AllGames.ALGEBRA -> AlgebraStatsSection(data)
                    AllGames.SUDOKU -> SudokuStatsSection(data, currentDifficulty)
                    AllGames.MEMORY -> MemoryMixStatsSection(data, currentLevel)
                }
            }
//                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
//
//                    // XP and Coins Row
//                    Row(
//                        horizontalArrangement = Arrangement.spacedBy(16.dp),
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        StatCard(
//                            modifier = Modifier.weight(1f),
//                            icon = painterResource(R.drawable.xp_icon),
//                            iconColor = Color(0xFFFFD700),
//                            title = "XP EARNED",
//                            value = "+${data.eachGameXp}",
//                            subtitle = "This Level"
//                        )
//
//                        StatCard(
//                            modifier = Modifier.weight(1f),
//                            icon = painterResource(R.drawable.coin_icon),
//                            iconColor = Color(0xFFFF9800),
//                            title = "COINS EARNED",
//                            value = data.eachGameCoin.toString(),
//                            subtitle = "This Level"
//                        )
//
//                    }
//
//                    // Streak Card (Full Width)
//                    StatCard(
//                        modifier = Modifier.fillMaxWidth(),
//                        icon = painterResource(R.drawable.fire_icon),
//                        iconColor = Color(0xFFFF5722),
//                        title = "STREAK POWER",
//                        value = "${data.currentStreak} / ${data.bestStreak}",
//                        subtitle = "Current / Best Streak"
//                    )
//                }
//            }

            Spacer(modifier = Modifier.height(32.dp))

            // 🎁 Unlock Rewards (if any)
            data.unlockAvatarInfo?.let { avatarInfo ->
                AnimatedVisibility(
                    visible = isVisible,
                    enter = scaleIn(
                        animationSpec = tween(600, delayMillis = 800)
                    ) + fadeIn(tween(600, delayMillis = 800))
                ) {
                    UnlockCard(
                        title = if (avatarInfo.unlocked) "🎊 NEW AVATAR UNLOCKED!" else "🔒 AVATAR LOCKED",
                        subtitle = if (avatarInfo.unlocked) avatarInfo.name else "${avatarInfo.name} • ${avatarInfo.unlockAt}",
                        isUnlocked = avatarInfo.unlocked
                    )
                }
            }

            // 💬 Unlock Message
            data.unlockMessage?.let { message ->
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(700, delayMillis = 1000)
                    ) + fadeIn(tween(700, delayMillis = 1000))
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            1.dp,
                            Color.White.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(
                            text = message,
                            modifier = Modifier.padding(20.dp),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 🎮 Action Buttons
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(800, delayMillis = 1200)
                ) + fadeIn(tween(800, delayMillis = 1200))
            ) {
                when (gameType) {
                    AllGames.ALGEBRA -> AlgebraActionButtons(
                        data = data,
                        currentLevel = currentLevel,
                        canProgressToNext = canProgressToNext,
                        onReplay = { onReplay(currentLevel, "algebra") },
                        onNext = { onNext(currentLevel + 1, "algebra") },
                        onHome = onHome
                    )

                    AllGames.SUDOKU -> SudokuActionButtons(
                        data = data,
                        currentDifficulty = currentDifficulty,
                        onReplay = { onReplay(0, currentDifficulty) },
                        onNext = { difficulty -> onReplay(0, difficulty) },
                        onHome = onHome
                    )

                    AllGames.MEMORY -> MemoryMixActionButtons(
                        data = data,
                        currentLevel = currentLevel,
                        canProgressToNext = data.isMatchWon == true,
                        onReplay = { onReplay(currentLevel, "math_memory") },
                        onNext = {
                            viewModel.onAction(MathMemoryAction.NextLevel)
                            //    navigateToMathMemory(currentLevel+1)
                            onNext(currentLevel + 1, "math_memory")
                        },
                        onHome = onHome
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun AlgebraStatsSection(data: UniversalResult) {
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
                value = "+${data.eachGameXp}",
                subtitle = "This Level"
            )

            StatCard(
                modifier = Modifier.weight(1f),
                icon = painterResource(R.drawable.figma_coin),
                iconColor = Color(0xFFFF9800),
                title = "COINS EARNED",
                value = "+${data.eachGameCoin}", // changed
                subtitle = "This Level"
            )
        }

        // Streak Card
        StatCard(
            modifier = Modifier.fillMaxWidth(),
            icon = painterResource(R.drawable.fire_icon),
            iconColor = Color(0xFFFF5722),
            title = "STREAK POWER",
            value = "${data.currentStreak} / ${data.bestStreak}",
            subtitle = "Current / Best Streak"
        )
    }
}

@Composable
private fun SudokuStatsSection(data: UniversalResult, difficulty: String) {
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
                value = "+${data.eachGameXp}",
                subtitle = "This Game"
            )

            StatCard(
                modifier = Modifier.weight(1f),
                icon = painterResource(R.drawable.figma_coin),
                iconColor = Color(0xFFFF9800),
                title = "COINS EARNED",
                value = "+${data.coinsEarned}",
                subtitle = "This Game"
            )
        }

        // Streak Card
        StatCard(
            modifier = Modifier.fillMaxWidth(),
            icon = painterResource(R.drawable.fire_icon),
            iconColor = Color(0xFFFF5722),
            title = "STREAK POWER",
            value = "${data.currentStreak} / ${data.bestStreak}",
            subtitle = "Current / Best Streak"
        )

        // Time and Hints Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = painterResource(R.drawable.time_icon),
                iconColor = Color(0xFF2196F3),
                title = "TIME TAKEN",
                value = formatTime(data.bestTimeSec ?: 0),
                subtitle = "This Game"
            )

            StatCard(
                modifier = Modifier.weight(1f),
                icon = painterResource(R.drawable.hint_without_ad),
                iconColor = Color(0xFFFFC107),
                title = "HINTS USED",
                value = "${data.hintsUsed ?: 0}",
                subtitle = "This Game"
            )
        }

        // Difficulty and Best Time Card
        StatCard(
            modifier = Modifier.fillMaxWidth(),
            icon = painterResource(R.drawable.grid),
            iconColor = when (difficulty.lowercase()) {
                "easy" -> Color(0xFF4CAF50)
                "medium" -> Color(0xFFFF9800)
                "hard" -> Color(0xFFFF5722)
                else -> Color(0xFF9C27B0)
            },
            title = "DIFFICULTY",
            value = difficulty.uppercase(),
            subtitle = "Best Time: ${formatTime(data.bestTimeSec ?: 0)}"
        )
    }
}

@Composable
private fun MemoryMixStatsSection(data: UniversalResult, currentLevel: Int) {
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
                value = "+${data.xpEarned}",
                subtitle = "This Level"
            )

            StatCard(
                modifier = Modifier.weight(1f),
                icon = painterResource(R.drawable.figma_coin),
                iconColor = Color(0xFFFF9800),
                title = "COINS EARNED",
                value = "+${data.coinsEarned}",
                subtitle = "This Level"
            )
        }

        // Score and Time Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = painterResource(R.drawable.delete),
                iconColor = Color(0xFF9C27B0),
                title = "FINAL SCORE",
                value = "${data.score ?: 0}",
                subtitle = "Points"
            )

            StatCard(
                modifier = Modifier.weight(1f),
                icon = painterResource(R.drawable.check),
                iconColor = Color(0xFF2196F3),
                title = "COMPLETION TIME",
                value = formatTime(data.bestTimeSec ?: 0),
                subtitle = "Minutes:Seconds"
            )
        }

        // Level Progress Card
        StatCard(
            modifier = Modifier.fillMaxWidth(),
            icon = painterResource(R.drawable.grid),
            iconColor = Color(0xFFE91E63),
            title = "MEMORY CHALLENGE",
            value = "LEVEL $currentLevel",
            subtitle = "Memory Pattern Recognition"
        )
    }
}

@Composable
private fun MemoryMixActionButtons(
    data: UniversalResult,
    currentLevel: Int,
    canProgressToNext: Boolean,
    onReplay: () -> Unit,
    onNext: () -> Unit,
    onHome: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        // Next Level Button
        if (data.canNextLevel && canProgressToNext) {
            GameButton(
                text = "NEXT LEVEL",
                icon = Icons.Default.PlayArrow,
                isPrimary = true,
                modifier = Modifier.fillMaxWidth(),
                onClick = onNext
            )
        } else if (data.canNextLevel && !canProgressToNext) {
            DisabledGameButton(
                text = "COMPLETE LEVEL TO CONTINUE",
                icon = Icons.Default.Lock,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Action Buttons Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            GameButton(
                text = "PLAY AGAIN",
                icon = Icons.Default.Refresh,
                modifier = Modifier.weight(1f),
                onClick = onReplay
            )

            GameButton(
                text = "HOME",
                icon = Icons.Default.Home,
                modifier = Modifier.weight(1f),
                onClick = onHome
            )
        }

        // Level Selection Row (if you want quick level access)
        if (currentLevel > 1) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val availableLevels = (1..currentLevel).toList()
                availableLevels.take(5).forEach { level -> // Show max 5 levels
                    LevelButton(
                        text = "$level",
                        isSelected = level == currentLevel,
                        modifier = Modifier.weight(1f)
                    ) {
                        onReplay() // For now, just replay current level
                    }
                }
            }
        }
    }
}

@Composable
private fun LevelButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected)
                Color(0xFFE91E63)
            else
                Color.White.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 12.sp
        )
    }
}


@Composable
private fun AlgebraActionButtons(
    data: UniversalResult,
    currentLevel: Int,
    canProgressToNext: Boolean,
    onReplay: () -> Unit,
    onNext: () -> Unit,
    onHome: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Next Level Button
        if (data.canNextLevel && canProgressToNext) {
            GameButton(
                text = "NEXT LEVEL",
                icon = Icons.Default.PlayArrow,
                isPrimary = true,
                modifier = Modifier.fillMaxWidth(),
                onClick = onNext
            )
        } else if (data.canNextLevel && !canProgressToNext) {
            DisabledGameButton(
                text = "COMPLETE LEVEL TO CONTINUE",
                icon = Icons.Default.Lock,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Action Buttons Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (data.canReplay) {
                GameButton(
                    text = "REPLAY",
                    icon = Icons.Default.Refresh,
                    modifier = Modifier.weight(1f),
                    onClick = onReplay
                )
            }


//            GameButton(
//                text = "NEXT LEVEL",
//                icon = Icons.Default.PlayArrow,
//                isPrimary = true,
//                modifier = Modifier.fillMaxWidth(),
//                onClick = onNext
//            )
        }
    }
}

@Composable
private fun SudokuActionButtons(
    data: UniversalResult,
    currentDifficulty: String,
    onReplay: () -> Unit,
    onNext: (String) -> Unit,
    onHome: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        // Try Harder Difficulty (if won and not already hardest)
        if (data.won == true && currentDifficulty.lowercase() != "expert") {
            val nextDifficulty = when (currentDifficulty.lowercase()) {
                "easy" -> "Medium"
                "medium" -> "Hard"
                "hard" -> "Expert"
                else -> "Medium"
            }

            GameButton(
                text = "TRY $nextDifficulty",
                icon = Icons.Default.AddCircle,
                isPrimary = true,
                modifier = Modifier.fillMaxWidth()
            ) {
                onNext(nextDifficulty)
            }
        }

        // Action Buttons Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            GameButton(
                text = "PLAY AGAIN",
                icon = Icons.Default.Refresh,
                modifier = Modifier.weight(1f),
                onClick = onReplay
            )

            GameButton(
                text = "HOME",
                icon = Icons.Default.Home,
                modifier = Modifier.weight(1f),
                onClick = onHome
            )
        }

        // Difficulty Selection Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val difficulties = listOf("Easy", "Medium", "Hard", "Expert")
            difficulties.forEach { difficulty ->
                DifficultyButton(
                    text = difficulty,
                    isSelected = difficulty.equals(currentDifficulty, ignoreCase = true),
                    modifier = Modifier.weight(1f)
                ) {
                    onNext(difficulty)
                }
            }
        }
    }
}

@Composable
private fun DifficultyButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected)
                Color(0xFF4CAF50)
            else
                Color.White.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 10.sp
        )
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "${minutes}:${remainingSeconds.toString().padStart(2, '0')}"
}


@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: Painter,
    iconColor: Color,
    title: String,
    value: String,
    subtitle: String
) {

    Card(
        modifier = modifier.shadow(8.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = icon,
                contentDescription = title,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 0.5.sp
            )

            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2C3E50)
            )

            Text(
                text = subtitle,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                textAlign = TextAlign.Center
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
private fun DisabledGameButton(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(56.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Gray.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = text,
                color = Color.White.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}


@Composable
private fun UnlockCard(
    title: String,
    subtitle: String,
    isUnlocked: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked)
                Color(0xFF4CAF50).copy(alpha = 0.9f)
            else
                Color(0xFF9E9E9E).copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isUnlocked) Icons.Default.CheckCircle else Icons.Default.Lock,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
private fun GameButton(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPrimary)
                Color(0xFF4CAF50)
            else
                Color.White.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isPrimary) Color.White else Color(0xFF2C3E50),
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            color = if (isPrimary) Color.White else Color(0xFF2C3E50),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun AnimatedParticles() {
    var animationTime by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            animationTime += 0.016f // ~60fps
            delay(16)
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val particles = listOf(
            Offset(size.width * 0.1f, size.height * 0.2f),
            Offset(size.width * 0.8f, size.height * 0.1f),
            Offset(size.width * 0.2f, size.height * 0.7f),
            Offset(size.width * 0.9f, size.height * 0.6f),
            Offset(size.width * 0.5f, size.height * 0.3f),
        )

        particles.forEachIndexed { index, basePosition ->
            val offsetX = sin(animationTime + index) * 20f
            val offsetY = cos(animationTime + index * 0.5f) * 15f
            val position = Offset(
                basePosition.x + offsetX,
                basePosition.y + offsetY
            )

            drawCircle(
                color = Color.White.copy(alpha = 0.1f),
                radius = 4f + sin(animationTime + index * 2) * 2f,
                center = position
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Preview() {

}
