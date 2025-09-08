package com.futurion.apps.mindmingle.presentation.level_selection

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun LevelBasedScreen(
    maxUnlockedLevel: Int,
    onLevelClick: (Int) -> Unit,
    rewardLevels: Map<Int, String> = mapOf(
        5 to "🎁",
        10 to "⭐",
        14 to "🔥",
        20 to "💎",
        28 to "💎",
        35 to "💎",
    ),
) {
    val totalLevels = Int.MAX_VALUE // infinite levels (technically limited by Int)
    val levelList = remember { (1..1000).toList() } // Load chunks as needed

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
            .systemBarsPadding()
            .navigationBarsPadding()
    ) {

        Text(
            text = "LEVELS",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(levelList.size) { index ->
                val levelNumber = levelList[index]
                val unlocked = levelNumber <= maxUnlockedLevel
                val hasReward = rewardLevels.containsKey(levelNumber)

                val backgroundColor = when {
                    hasReward -> if (!unlocked) Color(0xFF4444AA) else Color.DarkGray// Special color for reward levels
                    unlocked -> Color.DarkGray
                    else -> Color.Gray.copy(alpha = 0.4f)
                }

                Column(
                    modifier = Modifier
                        .padding(6.dp)
                        .size(60.dp)
                        .background(backgroundColor)
                        .clickable(enabled = unlocked) {
                            onLevelClick(levelNumber)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = levelNumber.toString(),
                        color = if (unlocked) Color.White else Color.LightGray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Show reward indicator even if locked
                    rewardLevels[levelNumber]?.let { rewardIcon ->
                        Text(
                            text = rewardIcon,
                            fontSize = 14.sp,
                            color = if (unlocked) Color.Yellow else Color.LightGray,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

//        LazyVerticalGrid(
//            columns = GridCells.Fixed(4),
//            contentPadding = PaddingValues(8.dp),
//            verticalArrangement = Arrangement.spacedBy(12.dp),
//            horizontalArrangement = Arrangement.spacedBy(12.dp),
//            modifier = Modifier.fillMaxSize()
//        ) {
//            items(levelList.size) { index ->
//                val levelNumber = levelList[index]
//                val isUnlocked = levelNumber <= maxUnlockedLevel
//                val justUnlocked = levelNumber == maxUnlockedLevel
//                val isReward = levelNumber % 10 == 0
//                //    Log.e("Level is Unlocked",isUnlocked.toString() + "max-${maxUnlockedLevel}"+ "level=${levelNumber}")
//
//                LevelItems(
//                    level = levelNumber,
//                    isUnlocked = isUnlocked,
//                    onClick = { if (isUnlocked) onLevelClick(levelNumber) },
//                    isReward = isReward,
//                )
//            }
//        }
    }
}

@Composable
fun infiniteTransitionGlowing(shouldGlow: Boolean): State<Float> {
    val infiniteTransition = rememberInfiniteTransition()
    return infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (shouldGlow) 1f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
}


@Composable
fun LevelItems(level: Int, isUnlocked: Boolean, isReward: Boolean,onClick: () -> Unit) {
    val scale = remember { Animatable(1f) }

    // Bounce animation for unlocked
    LaunchedEffect(isUnlocked) {
        if (isUnlocked) {
            scale.animateTo(
                targetValue = 1.2f,
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            )
            scale.animateTo(1f, tween(200))
        }
    }

    Box(
        modifier = Modifier
            .size(80.dp)
            .scale(scale.value)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isUnlocked) Color.White else Color(0xFFB0BEC5))
            .border(3.dp, Color.Black, RoundedCornerShape(16.dp))
            .clickable(enabled = isUnlocked) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        when {
            isReward -> {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Email, // badge 🏆
                    contentDescription = "Reward",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(40.dp)
                )
            }
            isUnlocked -> {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Star, // star ⭐ for unlocked
                    contentDescription = "Unlocked Level",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(40.dp)
                )
            }
            else -> {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Lock, // lock 🔒
                    contentDescription = "Locked",
                    tint = Color(0xFF546E7A),
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        // Level Number (small text under icon)
        Text(
            text = "$level",
            style = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 6.dp),
            color = if (isUnlocked) Color.Black else Color.DarkGray
        )
    }
}



