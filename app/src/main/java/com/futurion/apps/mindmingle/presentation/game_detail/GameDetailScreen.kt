package com.futurion.apps.mindmingle.presentation.game_detail

import ContentWithMessageBar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.futurion.apps.mindmingle.R
import com.futurion.apps.mindmingle.domain.Difficulty
import com.futurion.apps.mindmingle.presentation.utils.BebasNeueFont
import com.futurion.apps.mindmingle.presentation.utils.FontSize
import com.futurion.apps.mindmingle.presentation.utils.IconPrimary
import com.futurion.apps.mindmingle.presentation.utils.Resources
import com.futurion.apps.mindmingle.presentation.utils.Surface
import com.futurion.apps.mindmingle.presentation.utils.SurfaceBrand
import com.futurion.apps.mindmingle.presentation.utils.SurfaceError
import com.futurion.apps.mindmingle.presentation.utils.TextPrimary
import com.futurion.apps.mindmingle.presentation.utils.TextWhite
import rememberMessageBarState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(
    gameTitle: String,
    gameSubtitle: String, // e.g., "Boost your logic and memory skills!"
    xpReward: Int,
    coinsReward: Int,
    knowledgeBadges: List<String>, // e.g., ["Logic", "Focus", "Math"]
    howToPlaySteps: List<String>, // List of short instruction steps
    howToPlayImages: List<Int>, // Resource IDs for image carousel
    onStart: (String) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDifficulty by remember { mutableStateOf<Difficulty>(Difficulty.EASY) }
    var carouselIndex by remember { mutableStateOf(0) }
    val messageBarState = rememberMessageBarState()

    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = gameTitle,
                        fontFamily = BebasNeueFont(),
                        fontSize = FontSize.LARGE,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            painter = painterResource(Resources.Icon.BackArrow),
                            contentDescription = "Back Arrow icon",
                            tint = IconPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface,
                    scrolledContainerColor = Surface,
                    navigationIconContentColor = IconPrimary,
                    titleContentColor = TextPrimary,
                    actionIconContentColor = IconPrimary
                )
            )
        }
    ) { padding ->

        ContentWithMessageBar(
            contentBackgroundColor = Surface,
            modifier = Modifier
                .padding(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding()
                ),
            messageBarState = messageBarState,
            errorMaxLines = 2,
            errorContainerColor = SurfaceError,
            errorContentColor = TextWhite,
            successContainerColor = SurfaceBrand,
            successContentColor = TextPrimary
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(Color(0xFFF7FAFC)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = gameSubtitle,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp),
                    color = Color(0xFF31363B),
                )

                // Knowledge / Badge Section
                Row(
                    Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    knowledgeBadges.forEach {
                        Badge(label = it)
                    }
                }

                // XP and Coins Rewards
                Row(
                    Modifier.padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    RewardChip(icon = R.drawable.dollar, amount = xpReward, label = "XP")
                    RewardChip(icon = R.drawable.check, amount = coinsReward, label = "Coins")
                }

                // How to Play Section
                SectionTitle("How to Play")
                Column(Modifier.padding(horizontal = 24.dp)) {
                    howToPlaySteps.forEachIndexed { i, step ->
                        Text(
                            text = "Step ${i + 1}: $step",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 2.dp),
                            color = Color(0xFF222831)
                        )
                    }
                }

                // How to Play Carousel
                Spacer(Modifier.height(6.dp))
                ImageCarousel(
                    images = howToPlayImages,
                    currentIndex = carouselIndex,
                    onIndexChange = { carouselIndex = it }
                )

                // Difficulty Selection
                SectionTitle("Select Difficulty")
                Row(
                    Modifier
                        .padding(top = 18.dp, bottom = 4.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Difficulty.entries.forEach { difficulty ->
                        DifficultyOption(
                            label = difficulty.name,
                            icon = difficulty.icon,
                            selected = selectedDifficulty == difficulty,
                            onClick = {
                                selectedDifficulty = difficulty
                            }
                        )
                    }
                }

                // CTA Button
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = {
                        onStart(selectedDifficulty.name)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF76ABAE))
                ) {
                    Text(
                        "Start $gameTitle",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(16.dp))
                // Motivational footer
                Text(
                    text = "Earn XP & Coins for each session. Beat your best score!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF444A53)
                )
            }
        }

    }

}

// Section Title composable
@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        color = Color(0xFF0077B6),
        modifier = Modifier.padding(top = 28.dp, bottom = 8.dp)
    )
}

// Simple badge for knowledge/motivation
@Composable
fun Badge(label: String) {
    Box(
        Modifier
            .background(Color(0xFFDDE5EC), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color(0xFF444A53))
    }
}

// XP/Coins chip
@Composable
fun RewardChip(icon: Int, amount: Int, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color(0xFFF3F9FB), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Icon(
            painterResource(id = icon),
            contentDescription = label,
            modifier = Modifier.size(20.dp),
            tint = Color(0xFF76ABAE)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            "$amount $label",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF222831)
        )
    }
}

// Difficulty Option Button
@Composable
fun DifficultyOption(
    label: String,
    icon: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (selected) Color(0xFF0077B6) else Color(0xFFEEEEEE)
    val contentColor = if (selected) Color.White else Color(0xFF222831)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(16.dp))
            .clickable {
                onClick()
            }
            .padding(14.dp)
    ) {
        Icon(
            painterResource(id = icon),
            contentDescription = label,
            modifier = Modifier.size(32.dp),
            tint = contentColor
        )
        Text(label, style = MaterialTheme.typography.bodySmall, color = contentColor)
    }
}

// Simple carousel for images
@Composable
fun ImageCarousel(images: List<Int>, currentIndex: Int, onIndexChange: (Int) -> Unit) {
    Box(
        modifier = Modifier
            .height(160.dp)
            .fillMaxWidth(), contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = images[currentIndex]),
            contentDescription = "How to play step",
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        // Dot indicators
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(images.size) { i ->
                val color = if (i == currentIndex) Color(0xFF0077B6) else Color(0xFFB0B6C3)
                Box(
                    Modifier
                        .size(8.dp)
                        .background(color, CircleShape)
                        .padding(horizontal = 3.dp)
                        .clickable { onIndexChange(i) }
                )
            }
        }
    }
}

//@Composable
//fun CategoriesScreen(
//    navigateToGamesScreen: (String) -> Unit,
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(all = 12.dp),
//        verticalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        Difficulty.entries.forEach { category ->
//            CategoryCard(
//                onClick = {
//                    navigateToGamesScreen(category.name)
//                }
//            )
//        }
//    }
//}
