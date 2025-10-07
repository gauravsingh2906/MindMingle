package com.futurion.apps.mathmingle.presentation.themes_screen

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import com.futurion.apps.mathmingle.R
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.futurion.apps.mathmingle.GoogleRewardedAdManager
import com.futurion.apps.mathmingle.domain.model.GameTheme
import com.futurion.apps.mathmingle.presentation.math_memory.isInternetAvailable
import com.futurion.apps.mathmingle.presentation.profile.StatsViewModel
import com.futurion.apps.mathmingle.presentation.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeUnlockScreen(
    themeViewModel: ThemeViewModel = hiltViewModel(),
    statsViewModel: StatsViewModel = hiltViewModel(),
    onThemeSelected: (GameTheme) -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    val profile by statsViewModel.profile.collectAsStateWithLifecycle()
    val coins = profile?.coins ?: 0
    val unlockedThemes by remember { derivedStateOf { themeViewModel.unlockedThemes.value } }
    val selectedThemeState by remember { derivedStateOf { themeViewModel.selectedTheme.value } }

    var showAdDialog by remember { mutableStateOf(false) }
    var showCoinDialog by remember { mutableStateOf<GameTheme?>(null) }

    val dailyAdsWatched = profile?.adWatchCount ?: 0
    val adsLeft = 5 - dailyAdsWatched
    val context = LocalContext.current
    val activity = context as? Activity
    val googleAdManager = remember { GoogleRewardedAdManager(context, Constants.AD_Unit) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Themes") },
                navigationIcon = {
                    if (onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.figma_coin),
                            contentDescription = "Coins",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("$coins", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.width(12.dp))
                        Button(
                            onClick = { showAdDialog = true },
                            shape = RoundedCornerShape(20.dp),
                            enabled = adsLeft > 0,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (adsLeft > 0) Color(0xFFFF9800) else Color.Gray
                            )
                        ) {
                            Text(if (adsLeft > 0) "Earn Coins" else "Limit Reached")
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(padding)
        ) {
            items(themeViewModel.unlockableThemes) { unlockable ->
                val isUnlocked = unlockedThemes.contains(unlockable.theme.name) || unlockable.coinCost == 0
                val isSelected = selectedThemeState?.theme?.name == unlockable.theme.name

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp), // taller card so the full image feels visible
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(if (isSelected) 8.dp else 3.dp)
                ) {
                    Box {
                        // 🔹 Full theme image
                        Image(
                            painter = painterResource(unlockable.theme.backgroundImage),
                            contentDescription = unlockable.theme.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop // ✅ keeps aspect ratio, fills nicely
                        )

                        // 🔹 Lock overlay
                        if (!isUnlocked) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0x80000000)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }

                        // 🔹 Info bar at the very bottom (overlay)
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter) // ✅ stick to bottom
                                .fillMaxWidth()
                                .background(Color(0xAA000000)) // semi-transparent black
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    unlockable.theme.name,
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                if (isUnlocked) {
                                    Text(
                                        if (isSelected) "Selected" else "Unlocked",
                                        color = Color.LightGray,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                } else {
                                    Text("Unlock for ${unlockable.coinCost} coins", color = Color.Yellow)
                                }
                            }

                            Button(
                                onClick = {
                                    if (isUnlocked) {
                                        themeViewModel.selectTheme(unlockable.theme.name)
                                        onThemeSelected(unlockable.theme)
                                    } else if (coins >= unlockable.coinCost) {
                                        themeViewModel.unlockThemeByCoins(unlockable.theme.name)
                                    } else {
                                        showCoinDialog = unlockable.theme
                                    }
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isUnlocked) Color(0xFF4CAF50) else Color(0xFF2196F3)
                                )
                            ) {
                                Text(if (isUnlocked) if (isSelected) "Selected" else "Select" else "Unlock")
                            }
                        }
                    }
                }



            }
        }
    }

    // 🔹 Dialog for watching ads
    if (showAdDialog) {
        AlertDialog(
            onDismissRequest = { showAdDialog = false },
            title = { Text("Earn Coins") },
            text = { Text("Watch an ad to earn +10 coins. You can watch up to 5 ads per day.") },
            confirmButton = {
                Button(onClick = {
                    showAdDialog = false
                    if (!isInternetAvailable(context)) {
                        Toast.makeText(
                            context,
                            "No internet connection. Please connect to the internet.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    if (activity != null) {
                        statsViewModel.canWatchAd(themeViewModel.userId ?: "") { canWatch, _ ->
                            if (canWatch) {
                                googleAdManager.showRewardedAd(
                                    activity,
                                    onUserEarnedReward = {
                                        statsViewModel.rewardUserForAd(themeViewModel.userId ?: "")
                                        Toast.makeText(context, "+10 Coins earned!", Toast.LENGTH_SHORT).show()
                                    },
                                    onClosed = { }
                                )
                            } else {
                                Toast.makeText(context, "Daily ad limit reached!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }) {
                    Text("Watch Ad")
                }
            },
            dismissButton = { TextButton(onClick = { showAdDialog = false }) { Text("Cancel") } }
        )
    }

    // 🔹 Dialog when user doesn’t have enough coins
    if (showCoinDialog != null) {
        AlertDialog(
            onDismissRequest = { showCoinDialog = null },
            title = { Text("Not enough coins!") },
            text = { Text("You don’t have enough coins to unlock ${showCoinDialog!!.name}. Watch ads to earn more.") },
            confirmButton = {
                Button(onClick = {
                    showCoinDialog = null
                    if (!isInternetAvailable(context)) {
                        Toast.makeText(
                            context,
                            "No internet connection. Please connect to the internet.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    if (activity != null) {
                        statsViewModel.canWatchAd(themeViewModel.userId ?: "") { canWatch, _ ->
                            if (canWatch) {
                                googleAdManager.showRewardedAd(
                                    activity,
                                    onUserEarnedReward = {
                                        statsViewModel.rewardUserForAd(themeViewModel.userId ?: "")
                                        Toast.makeText(context, "+10 Coins earned!", Toast.LENGTH_SHORT).show()
                                    },
                                    onClosed = { }
                                )
                            } else {
                                Toast.makeText(context, "Daily ad limit reached!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }) { Text("Earn Coins") }
            },
            dismissButton = { TextButton(onClick = { showCoinDialog = null }) { Text("Cancel") } }
        )
    }
}


@Composable
private fun ThemeItem(
    theme: GameTheme,
    coinCost: Int,
    currentCoins: Int,
    isUnlocked: Boolean,
    isSelected: Boolean,
    onUnlockClick: () -> Boolean,
    onSelectClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(14.dp))
            .shadow(if (isSelected) 10.dp else 2.dp, RoundedCornerShape(14.dp))
            .clickable(enabled = isUnlocked) { if (isUnlocked) onSelectClick() },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            // Theme image with lock overlay
            Box {
                Image(
                    painter = painterResource(theme.backgroundImage),
                    contentDescription = "Theme ${theme.name}",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
                if (!isUnlocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(32.dp)
                            .background(Color(0x80000000), CircleShape)
                            .padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.size(12.dp))

            // Theme info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = theme.name,
                    color = theme.textColor,
                    style = MaterialTheme.typography.titleLarge
                )

                if (isUnlocked) {
                    Text(
                        text = if (isSelected) "Selected" else "Unlocked",
                        color = theme.textColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(
                        text = "Unlock for $coinCost coins",
                        color = theme.textColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    LinearProgressIndicator(
                        progress = (currentCoins / coinCost.toFloat()).coerceAtMost(1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        color = Color(0xFFFFC107),
                        trackColor = Color.LightGray
                    )
                }
            }

            // Action button
            if (isUnlocked) {
                Button(
                    onClick = onSelectClick,
                    enabled = !isSelected,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = theme.buttonColor,
                        contentColor = theme.buttonTextColor
                    )
                ) {
                    Text(text = if (isSelected) "Selected" else "Select")
                }
            } else {
                Button(
                    onClick = { onUnlockClick() },
                    enabled = coinCost > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = theme.buttonColor,
                        contentColor = theme.buttonTextColor
                    )
                ) {
                    Icon(Icons.Default.Lock, contentDescription = "Unlock")
                    Spacer(Modifier.width(6.dp))
                    Text("Unlock")
                }
            }
        }
    }
}
