package com.futurion.apps.mindmingle.presentation.home.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.futurion.apps.mindmingle.R
import com.futurion.apps.mindmingle.presentation.home.toBrushColor
import com.futurion.apps.mindmingle.presentation.utils.Surface

@Composable
fun AnimatedCoinsChip(coins: Int) {

    val animatedCoins by animateIntAsState(
        coins, tween(durationMillis = 800, easing = FastOutSlowInEasing)
    )
    val infiniteTransition = rememberInfiniteTransition()
    val coinBounce by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Brush.horizontalGradient(
            colors = listOf(Color(0xFFFFD700), Color(0xFFFFA000))
        ).toBrushColor(), // fallback use solid
        shadowElevation = 4.dp,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .defaultMinSize(minHeight = 36.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.figma_coin), // your coin icon drawable
                contentDescription = null,
                modifier = Modifier.size(24.dp).scale(coinBounce),
                tint = Color.Unspecified // use original colors or tint gold
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = animatedCoins.toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}
