package com.futurion.apps.mindmingle.presentation.profile.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.futurion.apps.mindmingle.data.local.entity.PerGameStatsEntity
import com.futurion.apps.mindmingle.presentation.utils.FontSize
import com.futurion.apps.mindmingle.presentation.utils.RobotoCondensedFont

@Composable
fun AnimatedPerGameStatsSection(
    perGameStats: List<PerGameStatsEntity>
) {
    val gameColors = listOf(
        Brush.horizontalGradient(listOf(Color(0xFF4C6EF5), Color(0xFF364FC7))),
        Brush.horizontalGradient(listOf(Color(0xFF37B24D), Color(0xFF2B8A3E))),
        Brush.horizontalGradient(listOf(Color(0xFFFD7E14), Color(0xFFE8590C))),
        Brush.horizontalGradient(listOf(Color(0xFF9C27B0), Color(0xFF6A1B9A))),
        Brush.horizontalGradient(listOf(Color(0xFFF03E3E), Color(0xFFC92A2A)))
    )

    val pastelGameColors = listOf(
        Brush.horizontalGradient(listOf(Color(0xFFBBDEFB), Color(0xFF90CAF9))), // soft blue
        Brush.horizontalGradient(listOf(Color(0xFFC8E6C9), Color(0xFFA5D6A7))), // soft green
        Brush.horizontalGradient(listOf(Color(0xFFFFE0B2), Color(0xFFFFCC80))), // soft orange
        Brush.horizontalGradient(listOf(Color(0xFFE1BEE7), Color(0xFFCE93D8))), // soft purple
        Brush.horizontalGradient(listOf(Color(0xFFFFCDD2), Color(0xFFEF9A9A)))  // soft red
    )


    Card(
        modifier = Modifier.padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column() {



            perGameStats.forEachIndexed { index, stat ->
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(
                        initialOffsetY = { 50 * (index + 1) },
                        animationSpec = tween(400 + index * 100, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(400 + index * 100)),
                ) {
                    GameStatCard(
                        gameName = stat.gameName.uppercase(),
                        gamesPlayed = stat.gamesPlayed,
                        wins = stat.wins,
                        losses = stat.losses,
                        xp = stat.xp,
                        hints = stat.totalHintsUsed,
                        coins = stat.coinsEarned,
                        background = pastelGameColors[index % pastelGameColors.size],
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }

            }
        }
    }


}

@Composable
fun GameStatCard(
    gameName: String,
    gamesPlayed: Int,
    wins: Int,
    losses: Int,
    xp: Int,
    coins:Int,
    hints:Int,
    background: Brush,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .background(background)
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Column {
                Text(
                    text = if (gameName=="MATH_MEMORY") "MATH MEMORY" else gameName,
                    fontFamily = RobotoCondensedFont(),
                    fontSize = FontSize.EXTRA_MEDIUM,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.horizontalScroll(state = rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                        AnimatedCountStatItem(icon = "🎮", label = "Games", count = gamesPlayed)
                        AnimatedCountStatItem(icon = "🏆", label = "Wins", count = wins)
                        AnimatedCountStatItem(icon = "❌", label = "Losses", count = losses)
                        AnimatedCountStatItem(icon = "⭐", label = "XP", count = xp)
                        //  AnimatedCountStatItem(icon = "📈", label = "Win %", count = (wins/gamesPlayed)*100)
                        AnimatedCountStatItem(icon = "💡", label = "Hints", count = hints)
                        AnimatedCountStatItem(icon = "💰", label = "Coins", count = coins)

                }
            }
        }
    }
}

@Composable
fun AnimatedCountStatItem(icon: String, label: String, count: Int) {
    val animatedCount by animateIntAsState(
        targetValue = count,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
    )
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = icon,
            fontSize = 28.sp,
        )
        Text(
            text = animatedCount.toString(),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )
        Text(text = label, style = MaterialTheme.typography.bodySmall.copy(color = Color.White))
    }
}
