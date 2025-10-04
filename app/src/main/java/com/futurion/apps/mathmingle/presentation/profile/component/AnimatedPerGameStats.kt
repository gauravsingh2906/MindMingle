package com.futurion.apps.mathmingle.presentation.profile.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.futurion.apps.mathmingle.data.local.entity.OverallProfileEntity
import com.futurion.apps.mathmingle.data.local.entity.PerGameStatsEntity
import com.futurion.apps.mathmingle.presentation.utils.FontSize
import com.futurion.apps.mathmingle.presentation.utils.RobotoCondensedFont

@Composable
fun AnimatedPerGameStats(
    perGameStats: List<PerGameStatsEntity>,
    profile: OverallProfileEntity
) {
    val pastelGameColors = listOf(
        Brush.horizontalGradient(listOf(Color(0xFFBBDEFB), Color(0xFF90CAF9))), // soft blue
        Brush.horizontalGradient(listOf(Color(0xFFC8E6C9), Color(0xFFA5D6A7))), // soft green
        Brush.horizontalGradient(listOf(Color(0xFFFFE0B2), Color(0xFFFFCC80))), // soft orange
        Brush.horizontalGradient(listOf(Color(0xFFE1BEE7), Color(0xFFCE93D8))), // soft purple
        Brush.horizontalGradient(listOf(Color(0xFFFFCDD2), Color(0xFFEF9A9A)))  // soft red
    )

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = screenWidth * 0.85f // 85% of screen width

    Column(Modifier.padding(horizontal = 12.dp)) {

        AnimatedVisibility(
            visible = profile.totalGamesPlayed > 0
        ) {
            Text(
                text = "Per Game Stats",
                fontFamily = RobotoCondensedFont(),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 8.dp), // padding on both ends
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(perGameStats.size) { index ->
                val stat = perGameStats[index]

                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(
                        initialOffsetY = { it / 2 }, // smooth slide
                        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(400)),
                ) {
                    GameStatCards(
                        gameName = stat.gameName.uppercase(),
                        gamesPlayed = stat.gamesPlayed,
                        wins = stat.wins,
                        losses = stat.losses,
                        xp = stat.xp,
                        hints = stat.totalHintsUsed,
                        coins = stat.coinsEarned,
                        background = pastelGameColors[index % pastelGameColors.size],
                        modifier = Modifier
                            .width(cardWidth) // fixed width
                            .padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}






@Composable
fun GameStatCards(
    gameName: String,
    gamesPlayed: Int,
    wins: Int,
    losses: Int,
    xp: Int,
    coins: Int,
    hints: Int,
    background: Brush,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .background(background)
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Column {
                // Game Title
                Text(
                    text = if (gameName == "MATH_MEMORY") "MATH MEMORY" else gameName,
                    fontFamily = RobotoCondensedFont(),
                    fontSize = FontSize.EXTRA_MEDIUM,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Spacer(Modifier.height(12.dp))

                // Stats with dividers
                StatRow(label = "Games Played", value = gamesPlayed.toString(), showDivider = true)
                StatRow(label = "Wins", value = wins.toString(), showDivider = true)
                StatRow(label = "Losses", value = losses.toString(), showDivider = true)
                StatRow(label = "XP", value = xp.toString(), showDivider = true)
                StatRow(label = "Hints Used", value = hints.toString(), showDivider = true)
                StatRow(
                    label = "Coins",
                    value = coins.toString(),
                    showDivider = false
                ) // last row no divider
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: String, showDivider: Boolean) {
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF424242)
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
            )
        }

        if (showDivider) {
            Spacer(Modifier.height(4.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE0E0E0))
            )
        }
    }
}


@Composable
fun StatRow(icon: String, label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 20.sp, modifier = Modifier.padding(end = 8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF424242) // dark gray label
                )
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121) // dark gray value
            )
        )
    }
}
