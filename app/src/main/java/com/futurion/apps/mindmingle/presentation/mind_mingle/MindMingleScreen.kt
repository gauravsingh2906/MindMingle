package com.futurion.apps.mindmingle.presentation.mind_mingle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random


@Composable
fun MindMingleScreen(modifier: Modifier = Modifier) {
    LevelCompleteScreenWithConfetti(
        level = 39,
        progressItems = listOf(
            LevelUnlockProgress(unlockLevel = 40, current = 34, required = 40),
            LevelUnlockProgress(unlockLevel = 35, current = 20, required = 35)
        ),
        onNextLevel = { /* TODO: Navigate to next level */ }
    )
}

@Composable
fun LevelCompleteScreenWithConfetti(
    level: Int,
    progressItems: List<LevelUnlockProgress>,
    onNextLevel: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // 🎉 Confetti background
        ConfettiEffect()

        // 🎯 Main content
        LevelCompleteScreen(
            level = level,
            progressItems = progressItems,
            onNextLevel = onNextLevel
        )
    }
}

@Composable
fun LevelCompleteScreen(
    level: Int,
    progressItems: List<LevelUnlockProgress>,
    onNextLevel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = "EXCELLENT!",
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Yellow,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Stars
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(3) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Star",
                    modifier = Modifier.size(64.dp),
                    tint = Color(0xFFFFD600) // gold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Progress Unlocks
        progressItems.forEach { item ->
            UnlockProgressItem(item)
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Next Level Button
        Button(
            onClick = onNextLevel,
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth(0.6f),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
            )
        ) {
            Text(
                text = "LEVEL ${level + 1}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun UnlockProgressItem(item: LevelUnlockProgress) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Unlocks at Lv. ${item.unlockLevel}",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = item.current.toFloat() / item.required.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF4CAF50),
                    trackColor = Color.Gray
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${item.current}/${item.required}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Unlock reward image placeholder
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🎁",
                    fontSize = 24.sp
                )
            }
        }
    }
}

// 🎁 Data model
data class LevelUnlockProgress(
    val unlockLevel: Int,
    val current: Int,
    val required: Int
)

@Composable
fun ConfettiEffect() {
    val confettiCount = 30
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    Box(modifier = Modifier.fillMaxSize()) {
        repeat(confettiCount) { index ->
            val randomX = remember { Random.nextFloat() * screenWidth.value }
            val duration = remember { (3000..6000).random() }
            val delay = remember { (0..2000).random() }

            var yOffset by remember { mutableStateOf(0f) }

            LaunchedEffect(index) {
                delay(delay.toLong())
                while (true) {
                    animate(
                        initialValue = 0f,
                        targetValue = screenHeight.value,
                        animationSpec = tween(durationMillis = duration, easing = LinearEasing)
                    ) { value, _ ->
                        yOffset = value
                    }
                    yOffset = 0f
                }
            }

            Box(
                modifier = Modifier
                    .offset(x = randomX.dp, y = yOffset.dp)
                    .size((8..14).random().dp)
                    .background(
                        color = randomConfettiColor(),
                        shape = RoundedCornerShape((2..8).random().dp)
                    )
            )
        }
    }
}

fun randomConfettiColor(): Color {
    val colors = listOf(
        Color.Red, Color.Yellow, Color.Green,
        Color.Blue, Color.Magenta, Color.Cyan, Color(0xFFFF9800)
    )
    return colors.random()
}
