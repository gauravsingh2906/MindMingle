package com.futurion.apps.mindmingle.presentation.themes_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.futurion.apps.mindmingle.domain.model.GameTheme

@Composable
fun ThemeUnlockScreen(
    themeViewModel: ThemeViewModel=hiltViewModel(),
    modifier: Modifier = Modifier,
    onThemeSelected: (GameTheme) -> Unit
) {
    val loading by remember { derivedStateOf { themeViewModel.loading.value } }
    val coins by remember { derivedStateOf { themeViewModel.userCoins.value } }
    val unlockedThemes by remember { derivedStateOf { themeViewModel.unlockedThemes.value } }
    val selectedThemeState by remember { derivedStateOf { themeViewModel.selectedTheme.value } }

    if (loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
            .padding(16.dp)
    ) {
        Text(
            text = "Your Coins: $coins",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(themeViewModel.unlockableThemes) { unlockable ->
                val isUnlocked = unlockedThemes.contains(unlockable.theme.name)
                val isSelected = selectedThemeState?.theme?.name == unlockable.theme.name
                ThemeItem(
                    theme = unlockable.theme,
                    coinCost = unlockable.coinCost,
                    isUnlocked = isUnlocked,
                    isSelected = isSelected,
                    onUnlockClick = { themeViewModel.unlockThemeByCoins(unlockable.theme.name) },
                    onSelectClick = {
                        if (isUnlocked) {
                            themeViewModel.selectTheme(unlockable.theme.name)
                            onThemeSelected(unlockable.theme)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ThemeItem(
    theme: GameTheme,
    coinCost: Int,
    isUnlocked: Boolean,
    isSelected: Boolean,
    onUnlockClick: () -> Unit,
    onSelectClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(14.dp))
            .shadow(if (isSelected) 8.dp else 0.dp, RoundedCornerShape(14.dp))
            .clickable(enabled = isUnlocked) { if (isUnlocked) onSelectClick() },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
            Image(
                painter = painterResource(theme.backgroundImage),
                contentDescription = "Theme ${theme.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            Spacer(modifier = Modifier.size(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = theme.name,
                    color = theme.textColor,
                    style = MaterialTheme.typography.titleLarge
                )
                if (isUnlocked) {
                    Text(
                        text = "Unlocked",
                        color = theme.textColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(
                        text = "Unlock for $coinCost coins",
                        color = theme.textColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (isUnlocked) {
                Button(
                    onClick = onSelectClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = theme.buttonColor,
                        contentColor = theme.buttonTextColor
                    )
                ) {
                    Text(text = if (isSelected) "Selected" else "Select")
                }
            } else {
                Button(
                    onClick = onUnlockClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = theme.buttonColor,
                        contentColor = theme.buttonTextColor
                    )
                ) {
                    Text(text = "Unlock")
                }
            }
        }
    }
}
