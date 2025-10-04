package com.futurion.apps.mathmingle.presentation.games

import androidx.compose.foundation.Image
import com.futurion.apps.mathmingle.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.futurion.apps.mathmingle.presentation.utils.FontSize

// Reuse/keep your GameGridItem data class (slightly trimmed here)


// Small data holder for stats — you'll pull real values from your repository
data class GameStats(
    val gameId: String?=null,
    val gamesPlayed: Int = 0,
    val wins: Int = 0,
    val xpTotal: Int = 0,
    val currentStreak: Int = 0
) {
    val winPercent: Int get() = if (gamesPlayed > 0) (wins * 100) / gamesPlayed else 0
}

@Composable
fun GamesScreenWithStats(
    games: List<GameGridItem>,
    statsForGame: (gameId: String) -> GameStats, // pass a lambda to fetch stats from repo
    onGameClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(games) { game ->
            if (game.isComingSoon) {
                ComingSoonCard(onClick = { /* maybe show unlock or preview */ })
            } else {
                val stats = statsForGame(game.id)
                GameCardWithStats(
                    game = game,
                    stats = stats,
                    onClick = { onGameClick(game.id) }
                )
            }
        }
    }
}

@Composable
fun GameCardWithStats(
    game: GameGridItem,
    stats: GameStats,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = game.cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top image area
            Image(
                painter = painterResource(id = game.imageResId),
                contentDescription = "${game.name} image",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )

            // Title + description
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = game.name,
                    fontSize = FontSize.MEDIUM,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2, // ✅ allow wrapping
                    overflow = TextOverflow.Ellipsis // ✅ prevent cutoff
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = game.description,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.95f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Rounded stats strip at the bottom — matches the mockup
            StatsStrip(
                gamesPlayed = stats.gamesPlayed,
                winPercent = stats.winPercent,
                xp = stats.xpTotal, // example: prefer total xp, fallback to card xp
                streak = stats.currentStreak,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun StatsStrip(
    gamesPlayed: Int,
    winPercent: Int,
    xp: Int,
    streak: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp)),
        color = Color.White.copy(alpha = 0.15f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItemWithIcon1(iconRes = "🎮", value = gamesPlayed.toString())
            StatItemWithIcon1(iconRes = "🏆", value = "$winPercent%")
            StatItemWithIcon1(iconRes = "⭐", value = xp.toString())
            StatItemWithIcon1(iconRes =  "\uD83D\uDD25", value = streak.toString())
        }
    }
}

@Composable
fun StatItemWithIcon1(iconRes: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = iconRes,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
//        Icon(
//            painter = painterResource(id = iconRes),
//            contentDescription = null,
//            tint = Color.White,
//            modifier = Modifier.size(18.dp)
//        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}

@Composable
fun StatItemWithIcon(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Color.Yellow,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(6.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
            Text(text = label, fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f))
        }
    }
}

@Composable
fun ComingSoonCard(onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111217)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // big lock icon
            Icon(
                painter = painterResource(id = R.drawable.unlock),
                contentDescription = "Locked",
                tint = Color.White,
                modifier = Modifier.size(56.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(text = "Coming Soon", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = "More fun awaits!", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
  //          Spacer(Modifier.height(16.dp))
            // small dimmed stats for preview (optional)
//            Surface(
//                modifier = Modifier
//                    .fillMaxWidth(0.85f)
//                    .height(44.dp)
//                    .clip(RoundedCornerShape(10.dp)),
//                color = Color.White.copy(alpha = 0.06f)
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxSize(),
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    StatItemWithIcon1(iconRes = "", value = "")
//                    StatItemWithIcon1(iconRes = "", value = "")
//                }
//            }
        }
    }
}

