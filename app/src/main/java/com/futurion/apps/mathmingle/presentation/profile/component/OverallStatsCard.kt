package com.futurion.apps.mathmingle.presentation.profile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@Composable
fun OverallStatsCard(profile: OverallProfileEntity) {
    val stats = listOf(
        "Games" to profile.totalGamesPlayed.toString(),
        "Wins" to profile.totalWins.toString(),
        "Losses" to profile.totalLosses.toString(),
        "Win %" to if (profile.totalGamesPlayed > 0) {
            "${(profile.totalWins * 100) / profile.totalGamesPlayed}%"
        } else "0%",
        "Total XP" to profile.totalXP.toString(),
        "Hints" to "${profile.totalHintsUsed}"
    )
    // "Level" to profile.overallHighestLevel.toString(),

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFAFAFA), Color(0xFFE3F2FD))
                    )
                )
                .padding(20.dp)
        ) {
            Text(
                text = "Overall Stats",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            stats.forEachIndexed { index, (label, value) ->
                var visible by remember { mutableStateOf(false) }

                // Trigger animation with delay for each row


                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF555555)
                            )
                        )
                        Text(
                            text = value,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111111)
                            )
                        )
                    }

                    if (index != stats.lastIndex) {
                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color(0xFFE0E0E0),
                            thickness = 1.dp
                        )
                    }
                }
            }
        }
    }
}


