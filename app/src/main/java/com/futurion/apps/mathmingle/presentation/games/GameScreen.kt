package com.futurion.apps.mathmingle.presentation.games

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class GameGridItem(
    val id: String,
    val name: String,
    val aboutGame: String,
    val steps:List<String>,
    val description: String,
    val imageResId: Int,
    val cardColor: Color,
    val xp: Int,
    val coins: List<String>,
    val isComingSoon: Boolean = false
)

@Composable
fun GamesScreen(
    games: List<GameGridItem>,
    statsForGame: (gameId: String) -> GameStats,
    onGameClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(games) { game ->
            if (game.isComingSoon) {
                ComingSoonCard()
            } else {
                val stats = statsForGame(game.id)
                GameCard(
                    gameName = game.name,
                    description = game.description,
                    imageRes = game.imageResId,
                    stats = stats,
                    onClick = {
                        onGameClick(game.id)
                    }
                )
            }
        }
    }
}

@Composable
fun GameCard(
    gameName: String,
    description: String,
    imageRes: Int,
    stats: GameStats,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column {
            // Game image
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = gameName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Crop
            )

            // Game title + description
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(gameName, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, style = MaterialTheme.typography.bodySmall)

                Spacer(modifier = Modifier.height(8.dp))

                // Stats row with icons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem(icon = "🎮" , value = stats.gamesPlayed, label = "Games")
                    StatItem(icon = "🏆", value = stats.winPercent, label = "Win%")
                    StatItem(icon = "\uD83D\uDD25", value = stats.currentStreak, label = "Streak")
                    StatItem(icon = "⭐", value = stats.xpTotal, label = "XP")
                }
            }
        }
    }
}

@Composable
fun StatItem(icon: String, value: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
//        Icon(
//            imageVector = icon,
//            contentDescription = label,
//            tint = MaterialTheme.colorScheme.primary
//        )
        Text(value.toString(), style = MaterialTheme.typography.bodyMedium)
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun GameCardItem(
    game: GameGridItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .shadow(8.dp, shape = MaterialTheme.shapes.medium)
            .background(game.cardColor, shape = MaterialTheme.shapes.medium)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = game.cardColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = game.imageResId),
                contentDescription = "${game.name} image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(130.dp)
                    .fillMaxWidth()
                    .background(Color.DarkGray)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = game.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )
            Text(
                text = game.description,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BadgeXP(xp = game.xp)
             //   BadgeCoins(coins = game.coins)
            }
        }
    }
}

@Composable
fun BadgeXP(xp: Int) {
    Surface(
        color = Color(0xFF4CAF50),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.padding(end = 4.dp)
    ) {
        Text(
            text = "XP: $xp",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = Color.White,
            fontSize = 12.sp
        )
    }
}

@Composable
fun BadgeCoins(coins: Int) {
    Surface(
        color = Color(0xFFFFD600),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = "Coins: $coins",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = Color.Black,
            fontSize = 12.sp
        )
    }
}

@Composable
fun ComingSoonCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .border(BorderStroke(3.dp, Color(0xFFB388FF)), shape = MaterialTheme.shapes.medium)
            .background(Color.Black, shape = MaterialTheme.shapes.medium),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color.Black)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                tint = Color.White,
                contentDescription = "Lock icon",
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Coming Soon",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color.White
            )
            Text(
                text = "More fun awaits!",
                fontSize = 15.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}



