package com.futurion.apps.mindmingle.presentation.profile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.futurion.apps.mindmingle.data.local.entity.PerGameStatsEntity

@Composable
fun PerGameStatsSection(
    perGameStats: List<PerGameStatsEntity>
) {
    val gameColors = listOf(
        Brush.horizontalGradient(listOf(Color(0xFF4C6EF5), Color(0xFF364FC7))),
        Brush.horizontalGradient(listOf(Color(0xFF37B24D), Color(0xFF2B8A3E))),
        Brush.horizontalGradient(listOf(Color(0xFFFD7E14), Color(0xFFE8590C))),
        Brush.horizontalGradient(listOf(Color(0xFF9C27B0), Color(0xFF6A1B9A))),
        Brush.horizontalGradient(listOf(Color(0xFFF03E3E), Color(0xFFC92A2A)))
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        perGameStats.forEachIndexed { index, stat ->
            GameStatCard1(
                gameName = stat.gameName,
                gamesPlayed = stat.gamesPlayed,
                wins = stat.wins,
                losses = stat.losses,
                xp = stat.xp,
                background = gameColors[index % gameColors.size],
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun GameStatCard1(
    gameName: String,
    gamesPlayed: Int,
    wins: Int,
    losses: Int,
    xp: Int,
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
                    text = gameName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    GameStatItem(icon = "🎮", label = "Games", count = gamesPlayed)
                    GameStatItem(icon = "🏆", label = "Wins", count = wins)
                    GameStatItem(icon = "❌", label = "Losses", count = losses)
                    GameStatItem(icon = "⭐", label = "XP", count = xp)
                }
            }
        }
    }
}

@Composable
fun GameStatItem(icon: String, label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = icon, fontSize = 28.sp)
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )
        Text(text = label, style = MaterialTheme.typography.bodySmall.copy(color = Color.White))
    }
}
