package com.futurion.apps.mindmingle.presentation.profile

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.futurion.apps.mindmingle.GoogleRewardedAdManager
import com.futurion.apps.mindmingle.R
import com.futurion.apps.mindmingle.data.local.entity.OverallProfileEntity
import com.futurion.apps.mindmingle.data.local.entity.PerGameStatsEntity
import com.futurion.apps.mindmingle.presentation.profile.component.AnimatedEditableUsername
import com.futurion.apps.mindmingle.presentation.profile.component.AnimatedOverallStatsCard
import com.futurion.apps.mindmingle.presentation.profile.component.AnimatedPerGameStats
import com.futurion.apps.mindmingle.presentation.profile.component.AnimatedPerGameStatsSection
import com.futurion.apps.mindmingle.presentation.profile.component.LevelProgressCircle
import com.futurion.apps.mindmingle.presentation.profile.component.OverallStatCard
import com.futurion.apps.mindmingle.presentation.profile.component.OverallStatsCard
import com.futurion.apps.mindmingle.presentation.profile.component.PerGameStatsSection
import com.futurion.apps.mindmingle.presentation.utils.Constants
import kotlinx.coroutines.delay


@Composable
fun ProfileScreen(
    statsViewModel: StatsViewModel = hiltViewModel(),
    profile: OverallProfileEntity,
    navigateToProfileScreen:()-> Unit,
    perGameStats: List<PerGameStatsEntity> = emptyList()
) {
    var showAvatarDialog by remember { mutableStateOf(false) }
    var showUsernameDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as Activity
    val rewardedAdManager = remember {
        GoogleRewardedAdManager(context, Constants.AD_Unit)
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
            userLevel = profile.finalLevel,
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
                showAvatarDialog = false
            },
            rewardedAdManager = rewardedAdManager,
            activity = activity
        )
    }

    val demoSpecialUsernames = listOf(
        "⚡ LightningAlex",
        "👑 Champion",
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
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(vertical = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile header with avatar, username and XP progress
        ProfileHeader(
            avatarRes = profile.avatarUri ?: R.drawable.avatar_1,
            username = profile.username,
            level = profile.finalLevel,
            currentXP = profile.currentLevelXP,
            xpForNextLevel = (profile.finalLevel + 1) * 100,
            onAvatarClick = { showAvatarDialog = true },
            onUsernameClick = { showUsernameDialog = true },
            coins = profile.coins.toString()
        )



        Spacer(Modifier.height(16.dp))

        // Overall stats section with colorful cards
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {

            // Overall stats
//            Text(
//                text = "Overall Stats",
//                fontWeight = FontWeight.Bold,
//                fontSize = 18.sp,
//                modifier = Modifier
//                    .fillMaxWidth()
//            )
            Spacer(Modifier.height(8.dp))


            OverallStatsCard(profile)
         //   StatsGrid1(profile = profile)

            val winPercent: Int = if (profile.totalGamesPlayed>0) (profile.totalWins*100)/profile.totalGamesPlayed else 0

        }

        Spacer(Modifier.height(16.dp))

//        AnimatedVisibility(
//            visible = profile.totalGamesPlayed > 0,
//        ) {
//            Text(
//                text = "Per-Game Stats",
//                fontWeight = FontWeight.Bold,
//                fontSize = 18.sp,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 16.dp)
//                    .padding(horizontal = 16.dp)
//            )
//        }

        AnimatedPerGameStats(perGameStats,profile)
      //  AnimatedPerGameStat(perGameStats = perGameStats)

    }

}

@Composable
fun UsernameChangeDialog(
    currentUsername: String,
    unlockedUsernames: List<String> = listOf<String>("Special1", "Special2"),
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit,
    onUnlockSpecial: (String) -> Unit,
    rewardedAdManager: GoogleRewardedAdManager,
    activity: Activity
) {
    var text by remember { mutableStateOf(currentUsername) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Username") },
        text = {
            Column {
                // Free text input
                TextField(
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
            TextButton(onClick = {
                if (text.isNotBlank()) {
                    onSubmit(text)
                    onDismiss()
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
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
    rewardedAdManager: GoogleRewardedAdManager,
    activity: Activity
) {
    val avatarList = listOf(
        R.drawable.avatar_1 to UnlockCondition.AlwaysUnlocked,
        R.drawable.avatar_2 to UnlockCondition.Level(5),
        R.drawable.avatar_4 to UnlockCondition.Coins(1500),
        R.drawable.avatar_5 to UnlockCondition.Coins(3000),
        R.drawable.avatar_6 to UnlockCondition.Level(10),
        R.drawable.avatar_7 to UnlockCondition.Level(15),
        R.drawable.avatar_special_1 to UnlockCondition.Level(30),
    )

    val context = LocalContext.current

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

                    val selected = unlockedAvatars.contains(avatarRes)

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
                                text = "Unlocked",
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
                                        textAlign = TextAlign.Center,
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
                                                if (activity != null) {
                                                    rewardedAdManager.showRewardedAd(
                                                        activity,
                                                        onUserEarnedReward = {
                                                            onNeedCoins()
                                                        },
                                                        onClosed = {
                                                            // Toast.makeText(context, "Ad not ready, please try again.", Toast.LENGTH_SHORT).show()
                                                        }
                                                    )
                                                }
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


@Composable
fun AnimatedAvatarSection(
    avatarRes: Int,
    username: String,
    onAvatarClick: () -> Unit,
    onEditClick: () -> Unit,
) {
    val pulseAnim = rememberInfiniteTransition()
    val pulse by pulseAnim.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            tween(1000, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .size(120.dp)
            .clickable { onAvatarClick() },
        contentAlignment = Alignment.Center
    ) {
        // Glowing pulse circle behind avatar
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    scaleX = pulse
                    scaleY = pulse
                }
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF6A8BFF).copy(alpha = 0.3f), Color.Transparent),
                    ),
                    shape = CircleShape
                )
        )
        // Avatar image or placeholder icon
        Image(
            painter = painterResource(id = avatarRes),
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )

        // Edit icon at bottom-right corner
        IconButton(
            onClick = onEditClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(32.dp)
                .background(Color.White, shape = CircleShape)
                .border(1.dp, Color(0xFF6A8BFF), shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Avatar",
                tint = Color(0xFF6A8BFF),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


sealed class UnlockCondition {
    object AlwaysUnlocked : UnlockCondition()
    data class Level(val requiredLevel: Int) : UnlockCondition()
    data class Coins(val requiredCoins: Int) : UnlockCondition()
}


@Composable
fun ProfileHeader(
    avatarRes: Int,
    username: String,
    level: Int,
    coins: String,
    currentXP: Int,
    xpForNextLevel: Int,
    onAvatarClick: () -> Unit,
    onUsernameClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Avatar with glowing pulse
            AnimatedAvatarSection(
                avatarRes = avatarRes,
                username = username,
                onAvatarClick = onAvatarClick,
                onEditClick = onUsernameClick
            )

            Spacer(Modifier.width(16.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f)
            ) {
                // Username with edit icon
                AnimatedEditableUsername(
                    username = username,
                    onEditClicked = onUsernameClick
                )

                Spacer(Modifier.height(8.dp))

                // Coins display
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.figma_coin), // Replace with coin drawable
                        contentDescription = "Coins",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = coins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF333333)
                    )
                }
            }
        }

        Spacer(Modifier.height(26.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center // This centers the circle
        ) {
            LevelProgressCircle(
                currentLevel = level,
                currentXp = currentXP,
                xpForNextLevel = xpForNextLevel
            )
        }

        Spacer(Modifier.height(8.dp))


    }
}

@Composable
fun OverallStatItem(label: String, value: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$value",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color(0xFF333333)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF777777)
        )
    }
}














