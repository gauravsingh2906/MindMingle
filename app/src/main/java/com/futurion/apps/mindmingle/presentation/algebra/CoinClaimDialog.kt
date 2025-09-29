package com.futurion.apps.mindmingle.presentation.algebra

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.futurion.apps.mindmingle.R

@Composable
fun CoinClaimDialog(
    coinsEarned: Int,
    onClaim: () -> Unit,
    onDismiss: () -> Unit
) {
    // Lottie Animation for coins (put your coin animation JSON in res/raw/coin_lottie.json)
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.cycle))
    val progress by animateLottieCompositionAsState(composition, iterations = 3, speed = 1.5f)

    // Animate coin count from 0 to coinsEarned over 1.5 seconds
    val animatedCoinCount = animateIntAsState(
        targetValue = coinsEarned,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
    )

    // Pulsing scale animation for coin icon and number
    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color(0xFF1E2328),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Coin Lottie animation with pulsing
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(48.dp).scale(pulseScale)
                )
                Spacer(Modifier.width(12.dp))

                // Animated coin count with pulsing scale
                Text(
                    text = "+${animatedCoinCount.value} Coins!",
                    fontSize = 28.sp,
                    color = Color(0xFFFFC107),
                    modifier = Modifier.scale(pulseScale),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        },
        text = {
            Text(
                "Congratulations! Your coin balance has increased.",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onClaim,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
            ) {
                Text("Claim", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss", color = Color.White.copy(alpha = 0.6f))
            }
        }
    )
}
