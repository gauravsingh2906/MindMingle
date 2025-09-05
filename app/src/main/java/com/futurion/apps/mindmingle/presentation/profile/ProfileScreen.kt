package com.futurion.apps.mindmingle.presentation.profile

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.futurion.apps.mindmingle.R
import com.futurion.apps.mindmingle.RewardedAdManager
import com.futurion.apps.mindmingle.data.local.entity.OverallProfileEntity
import com.futurion.apps.mindmingle.data.local.entity.PerGameStatsEntity


@Composable
fun ProfileScreen(
    statsViewModel: StatsViewModel = hiltViewModel(),
    profile: OverallProfileEntity,
    perGameStats: List<PerGameStatsEntity> = emptyList(),

) {
    var showAvatarDialog by remember { mutableStateOf(false) }
    var showUsernameDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as Activity
    val rewardedAdManager = remember {
        RewardedAdManager(context, "ca-app-pub-3940256099942544/5224354917")
    }

//    SideEffect {
//        setStatusBarColor(
//            activity,
//            Color(0xFF6A8BFF).toArgb(), // Use your gradient's top color
//            false   // Light icons for dark background
//        )
//    }

    if (showAvatarDialog) {
        AvatarUnlockDialog(
            unlockedAvatars = profile.unlockedAvatars,
            userLevel = profile.overallHighestLevel,
            userCoins = profile.coins,
            onDismiss = { showAvatarDialog = false },
            onUnlockRequested = { avatarId ->
                // Call ViewModel to unlock avatar (deduct coins if needed)
                statsViewModel.unlockAvatar(profile.userId, avatarId)
                showAvatarDialog = false
            },
            onAvatarSelected = { avatarId ->
                statsViewModel.changeAvatar(profile.userId, avatarId)
                showAvatarDialog = false
            },
            onNeedCoins = {
                // Show rewarded ad prompt or call ViewModel method to reward coins
                statsViewModel.rewardUserForAd(profile.userId)
            },
            rewardedAdManager = rewardedAdManager,
            activity = activity
        )
    }

    val demoSpecialUsernames = listOf(
        "⚡ LightningAlex",
        "👑 Champion",
        "🔥 UltraGaurav",
        "🎉 WinnerX"
    )



    if (showUsernameDialog) {
        UsernameChangeDialog(
            currentUsername = profile.username,
            unlockedUsernames = profile.unlockedUsernames.ifEmpty { demoSpecialUsernames },
            onDismiss = { showUsernameDialog = false },
            onSubmit = { newUsername ->
                statsViewModel.changeUsername(profile.userId, newUsername)
                showUsernameDialog = false
            },
            onUnlockSpecial = { specialUsername ->
                statsViewModel.unlockUsername(profile.userId, specialUsername)
                showUsernameDialog = false
            },
            rewardedAdManager = rewardedAdManager,
            activity = activity
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.Black.copy(alpha = 0.7f)
            )
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Box(
            Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.3f))
                .clickable {
                    showAvatarDialog = true
                    //   onChangeAvatar()
                },
            contentAlignment = Alignment.Center
        ) {
            if (profile.avatarUri.toString().isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(profile.avatarUri),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = "Avatar",
                    modifier = Modifier.fillMaxSize(),
                    tint = Color.White
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Username
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = profile.username,
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
            IconButton(
                onClick = {
                    showUsernameDialog = true
                }
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit username",
                    tint = Color.White
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // XP & Coins chips
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatChip(icon = "⭐", label = "XP", value = profile.currentLevelXP.toString())
            StatChip(icon = "💰", label = "Coins", value = profile.coins.toString())
        }

        Spacer(Modifier.height(20.dp))

        // 🔥 NEW: Level carousel with XP progress
        LevelCarousel(profile = profile)

        Spacer(Modifier.height(20.dp))

        // Overall stats
        SectionCard(title = "Overall Stats") {
            StatsRow("Total Games", profile.totalGamesPlayed)
            StatsRow("Total Wins 🏆", profile.totalWins)
            StatsRow("Total Losses ❌", profile.totalLosses)
            StatsRow("Total XP ", profile.totalXP)
            StatsRow("Highest Level 📈", profile.overallHighestLevel)
            StatsRow("Total Hints Used 💡", profile.totalHintsUsed)
            StatsRow("Time ⏱", "${profile.totalTimeSeconds / 60} min")
        }

        Spacer(Modifier.height(16.dp))

        // Per-Game stats
        if (perGameStats.isNotEmpty()) {
            SectionCard(title = "Per-Game Stats") {
                perGameStats.forEach { gameStats ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.9f),
                        tonalElevation = 4.dp,
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(
                                gameStats.gameName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color(0xFF2C1A4A),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(Modifier.height(6.dp))
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Games Played: ${gameStats.gamesPlayed}")
                                Text("XP: ${gameStats.xp}")
                                Text("Wins: ${gameStats.wins}")
                            }
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Losses: ${gameStats.losses}")
                                Text("Hints: ${gameStats.totalHintsUsed}")
                                Text("Time: ${gameStats.totalTimeSeconds / 60} min")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UsernameChangeDialog(
    currentUsername: String,
    unlockedUsernames: List<String> = listOf<String>("Special1", "Special2"),
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit,
    onUnlockSpecial: (String) -> Unit,
    rewardedAdManager: RewardedAdManager,
    activity: Activity
) {
    var text by remember { mutableStateOf(currentUsername) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Username") },
        text = {
            Column {
                // Free text input
                androidx.compose.material3.TextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Enter username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                // Special unlockable usernames section
                Text("Special Usernames:", style = MaterialTheme.typography.titleMedium)

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .heightIn(max = 200.dp)
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(unlockedUsernames) { username ->
                        SpecialUsernameItem(
                            username = username,
                            onClick = { onUnlockSpecial(username) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = {
                if (text.isNotBlank()) {
                    onSubmit(text)
                    onDismiss()
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SpecialUsernameItem(
    username: String,
    onClick: () -> Unit,
) {
    // Glowing effect colors
    val glowColors = listOf(
        Color(0xFF6A8BFF).copy(alpha = 0.8f),
        Color(0xFFB46CFF).copy(alpha = 0.6f),
        Color(0xFF6A8BFF).copy(alpha = 0.4f),
        Color(0xFFB46CFF).copy(alpha = 0.2f),
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(glowColors)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = username,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier
        )
    }
}


@Composable
fun AvatarUnlockDialog(
    unlockedAvatars: List<Int>,
    userLevel: Int,
    userCoins: Int,
    onDismiss: () -> Unit,
    onUnlockRequested: (Int) -> Unit,
    onAvatarSelected: (Int) -> Unit,
    onNeedCoins: () -> Unit,
    rewardedAdManager: RewardedAdManager,
    activity: Activity
) {
    val avatarList = listOf(
        R.drawable.avatar_1 to UnlockCondition.AlwaysUnlocked,
        R.drawable.avatar_2 to UnlockCondition.Level(5),
        R.drawable.avatar_4 to UnlockCondition.Coins(500),
        R.drawable.avatar_5 to UnlockCondition.Coins(500),
        R.drawable.avatar_6 to UnlockCondition.Level(10),
        R.drawable.avatar_7 to UnlockCondition.Level(15)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Select Avatar",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .heightIn(max = 400.dp)
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(avatarList) { (avatarRes, condition) ->
                    val isUnlocked =
                        unlockedAvatars.contains(avatarRes) || avatarRes == R.drawable.avatar_1
                    val avatarAlpha = if (isUnlocked) 1f else 0.4f

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isUnlocked) Color(0xFFE0F7FA)
                                else Color(0xFFFFEBEE)
                            )
                            .clickable(
                                enabled = isUnlocked,
                            ) {
                                Log.d("AvatarUnlockDialog", "Avatar clicked: $avatarRes")
                                onAvatarSelected(avatarRes)
                            }
                            .padding(8.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(avatarRes),
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .alpha(avatarAlpha)
                        )
                        Spacer(Modifier.height(6.dp))

                        if (isUnlocked) {
                            Text(
                                text = if (avatarRes == R.drawable.avatar_1) "Default unlocked" else "Unlocked",
                                color = Color(0xFF00796B),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        } else {
                            when (condition) {
                                is UnlockCondition.Level -> {
                                    Text(
                                        "Reach Level ${condition.requiredLevel}",
                                        color = Color(0xFFD32F2F),
                                        fontSize = 12.sp
                                    )
                                    if (userLevel >= condition.requiredLevel) {
                                        TextButton(
                                            onClick = { onUnlockRequested(avatarRes) }
                                        ) {
                                            Text("Claim")
                                        }
                                    } else {
                                        Text(
                                            "Locked", color = Color(0xFFD32F2F), fontSize = 12.sp
                                        )
                                    }
                                }

                                is UnlockCondition.Coins -> {
                                    Text(
                                        "Buy for ${condition.requiredCoins} coins",
                                        color = Color(0xFFD32F2F),
                                        fontSize = 12.sp
                                    )
                                    if (userCoins >= condition.requiredCoins) {
                                        TextButton(
                                            onClick = { onUnlockRequested(avatarRes) }
                                        ) {
                                            Text("Buy")
                                        }
                                    } else {
                                        TextButton(
                                            onClick = {
                                                rewardedAdManager.showAd(
                                                    activity,
                                                    onUserEarnedReward = {
                                                        onNeedCoins()
                                                    },
                                                    onAdDismissed = {
                                                        rewardedAdManager.loadAd()
                                                    })
                                            }
                                        ) {
                                            Text("Get Coins")
                                        }
                                        Text(
                                            "(Watch ads to earn coins)",
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                UnlockCondition.AlwaysUnlocked -> {
                                    TextButton(
                                        onClick = { onUnlockRequested(avatarRes) }
                                    ) {
                                        Text(
                                            "Default unlocked",
                                            color = Color(0xFF00796B),
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}


sealed class UnlockCondition {
    object AlwaysUnlocked : UnlockCondition()
    data class Level(val requiredLevel: Int) : UnlockCondition()
    data class Coins(val requiredCoins: Int) : UnlockCondition()
}


@Composable
fun StatChip(icon: String, label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color.White.copy(alpha = 0.2f),
        tonalElevation = 4.dp,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 16.sp)
            Spacer(Modifier.width(4.dp))
            Text("$label: $value", fontSize = 14.sp, color = Color.White)
        }
    }
}


@Composable
fun LevelCarousel(profile: OverallProfileEntity) {
    val currentLevel = profile.finalLevel
    val currentLevelXP = profile.currentLevelXP ?: 0
    val xpForNextLevel = (currentLevel) * 100  // example XP rule

    val progress = currentLevelXP.toFloat() / xpForNextLevel.toFloat()
    val xpNeeded = xpForNextLevel - currentLevelXP
//    if (currentXP >= xpForNextLevel) {
//        currentLevel+=1
//        currentXP = 0
//        xpForNextLevel = currentLevel*100
//    }
//
//    val progress = currentXP.toFloat() / xpForNextLevel.toFloat()
//    val xpNeeded = xpForNextLevel - currentXP


    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.95f),
                tonalElevation = 8.dp,
                shadowElevation = 12.dp,
                modifier = Modifier
                    .width(250.dp)
                    .height(140.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "LEVEL $currentLevel",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C1A4A)
                    )
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = progress.coerceIn(0f, 1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(50)),
                        color = Color(0xFF6A8BFF)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Earn $xpNeeded XP to reach Level ${currentLevel + 1}",
                        fontSize = 14.sp,
                        color = Color(0xFF5B4D7B)
                    )
                }
            }
        }
    }
}


@Composable
fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.9f),
        tonalElevation = 8.dp,
        shadowElevation = 12.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38225E)
                )
            )
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun StatsRow(label: String, value: Any) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color(0xFF5B4D7B))
        Text(value.toString(), fontWeight = FontWeight.Bold, color = Color(0xFF2C1A4A))
    }
}



