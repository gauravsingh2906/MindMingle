package com.futurion.apps.mindmingle.presentation.profile.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun LevelProgressCircle(
    currentLevel: Int,
    currentXp: Int,
    xpForNextLevel: Int,
    modifier: Modifier = Modifier
) {
    val rawProgress = currentXp / xpForNextLevel.toFloat()

    // Smooth animation
    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "progressAnim"
    )

    // Pulse effect
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAnim"
    )

    val percentage = (animatedProgress * 100).toInt()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(150.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stroke = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round)

                // Background ring
                drawArc(
                    color = Color(0xFF2E2E2E),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = stroke
                )

                // Glowing gradient ring
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color(0xFFFF9800),
                            Color(0xFFFF5722),
                            Color(0xFFFFC107),
                            Color(0xFFFF9800)
                        ),
                        center = center
                    ),
                    startAngle = -90f,
                    sweepAngle = 360 * animatedProgress,
                    useCenter = false,
                    style = stroke
                )

                // Glow pulse
                drawArc(
                    color = Color(0xFFFFD54F).copy(alpha = pulseAlpha),
                    startAngle = -90f,
                    sweepAngle = 360 * animatedProgress,
                    useCenter = false,
                    style = Stroke(width = 36.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Level Number in center
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Level $currentLevel",
                    color = Color(0xFFFFA000),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "$percentage%",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.LightGray
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // XP Bar
        Card(
            shape = RoundedCornerShape(50),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(28.dp)
                .shadow(6.dp, RoundedCornerShape(50))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Progress bar inside
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFFA726), Color(0xFFFF5722))
                            ),
                            shape = RoundedCornerShape(50)
                        )
                )
                Text(
                    text = "$currentXp / $xpForNextLevel XP",
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}



