package com.futurion.apps.mathmingle.presentation.profile.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.futurion.apps.mathmingle.data.local.entity.OverallProfileEntity
import kotlinx.coroutines.delay


@Composable
fun AnimatedOverallStatsCard(profile: OverallProfileEntity) {
    val stats = listOf(
        "Games" to profile.totalGamesPlayed.toString(),
        "Wins" to profile.totalWins.toString(),
        "Losses" to profile.totalLosses.toString(),
        "Win %" to if (profile.totalGamesPlayed > 0) {
            "${(profile.totalWins * 100) / profile.totalGamesPlayed}%"
        } else "0%",
        "XP" to profile.totalXP.toString(),
        "Level" to profile.overallHighestLevel.toString(),
        "Hints" to "${profile.totalHintsUsed}m"
    )

    // Gradient (you can try other palettes like purple→pink, green→teal, etc.)
    val background = Brush.horizontalGradient(
        colors = listOf(Color(0xFF4C6EF5), Color(0xFF364FC7))
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(background)
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Overall Stats",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                stats.forEachIndexed { index, (label, value) ->
                    var visible by remember { mutableStateOf(false) }

                    // staggered animation
                    LaunchedEffect(Unit) {
                        delay(index * 150L)
                        visible = true
                    }

                    AnimatedVisibility(
                        visible = visible,
                        enter = slideInVertically(
                            initialOffsetY = { it / 2 }
                        ) + fadeIn()
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                )
                                Text(
                                    text = value,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }

                            if (index != stats.lastIndex) {
                                Divider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = Color.White.copy(alpha = 0.2f),
                                    thickness = 1.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
