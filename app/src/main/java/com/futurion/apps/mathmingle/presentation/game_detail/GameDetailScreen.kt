package com.futurion.apps.mathmingle.presentation.game_detail

import ContentWithMessageBar
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.futurion.apps.mathmingle.R
import com.futurion.apps.mathmingle.domain.model.Difficulty
import com.futurion.apps.mathmingle.presentation.game_detail.component.FlavorChip
import com.futurion.apps.mathmingle.presentation.utils.Alpha
import com.futurion.apps.mathmingle.presentation.utils.BebasNeueFont
import com.futurion.apps.mathmingle.presentation.utils.ButtonDisabled
import com.futurion.apps.mathmingle.presentation.utils.ButtonPrimary
import com.futurion.apps.mathmingle.presentation.utils.FontSize
import com.futurion.apps.mathmingle.presentation.utils.IconPrimary
import com.futurion.apps.mathmingle.presentation.utils.Resources
import com.futurion.apps.mathmingle.presentation.utils.RobotoCondensedFont
import com.futurion.apps.mathmingle.presentation.utils.Surface
import com.futurion.apps.mathmingle.presentation.utils.SurfaceBrand
import com.futurion.apps.mathmingle.presentation.utils.SurfaceError
import com.futurion.apps.mathmingle.presentation.utils.TextPrimary
import com.futurion.apps.mathmingle.presentation.utils.TextWhite
import rememberMessageBarState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GameDetailScreen(
    gameTitle: String,
    gameSubtitle: String, // e.g., "Boost your logic and memory skills!"
    xpReward: Int,
    coinsReward: Int,
    userId: String,
    knowledgeBadges: List<String>, // e.g., ["Logic", "Focus", "Math"]
    howToPlaySteps: List<String>, // List of short instruction steps
    howToEarnCons:List<String>,
    navigateToThemeUnlock:(String)->Unit,
    howToPlayImages: List<Int>, // Resource IDs for image carousel
    onStart: (String) -> Unit,
    navigateBack: () -> Unit,
    navigateToSudokuResult: () -> Unit,
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
                actions = {
                    AnimatedVisibility(
                        visible = gameTitle=="Sudoku"
                    ) {
                        IconButton(onClick = navigateToSudokuResult) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Back Arrow icon",
                                tint = IconPrimary
                            )
                        }
                    }
                    AnimatedVisibility(
                        visible = gameTitle=="Math Memory"
                    ) {
                        IconButton(
                            onClick = {
                                navigateToThemeUnlock(userId)
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.White.copy(alpha = 0.9f), CircleShape)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.theme), // Replace with your theme icon
                                contentDescription = "Theme Selector",
                                tint = Color(0xFF6A8BFF)
                            )
                        }
                    }
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
            // Color(0xFFF7FAFC)
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 14.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = gameSubtitle,
                    fontFamily = RobotoCondensedFont(),
                    fontSize = FontSize.EXTRA_MEDIUM,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 14.dp),
                    color = TextPrimary,
                )

                // Knowledge / Badge Section
//                Row(
//                    Modifier.padding(vertical = 8.dp),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp),
//                ) {
//                    knowledgeBadges.forEach {
//                        Badge(label = it)
//                    }
//                }

                // XP and Coins Rewards
//                Row(
//                    Modifier.padding(top = 14.dp),
//                    horizontalArrangement = Arrangement.spacedBy(14.dp)
//                ) {
//                    RewardChip(icon = R.drawable.dollar, amount = xpReward, label = "XP")
//                    RewardChip(icon = R.drawable.figma_coin, amount = coinsReward, label = "Coins")
//                }

                Spacer(modifier = Modifier.height(14.dp))

                // How to Play Section
                SectionTitle("How to Play")
                Column(Modifier.padding(vertical = 16.dp)) {
                    howToPlaySteps.forEachIndexed { i, step ->
                        Text(
                            text = "Step ${i + 1}: $step",
                            fontSize = FontSize.REGULAR,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(vertical = 2.dp),
                            color = Color(0xFF222831)
                        )
                    }
                }

                SectionTitle("How to Earn Coins")
                Column(Modifier.padding(vertical = 16.dp)) {
                    howToEarnCons.forEachIndexed { i, step ->
                        Text(
                            text = "Step ${i + 1}: $step",
                            fontSize = FontSize.REGULAR,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(vertical = 2.dp),
                            color = Color(0xFF222831)
                        )
                    }
                }



                // How to Play Carousel
                Spacer(Modifier.height(6.dp))
//                ImageCarousel(
//                    images = howToPlayImages,
//                    currentIndex = carouselIndex,
//                    onIndexChange = { carouselIndex = it }
//                )


                AnimatedVisibility(
                    visible = gameTitle == "Sudoku"
                ) {
                    SectionTitle("Select Difficulty")
                }

                AnimatedVisibility(
                    visible = gameTitle == "Sudoku"
                ) {
                    Spacer(modifier = Modifier.height(14.dp))
                }

                AnimatedVisibility(
                    modifier = Modifier.fillMaxSize(),
                    visible = gameTitle == "Sudoku"
                ) {

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Difficulty.entries.forEach { difficulty ->
                            FlavorChip(
                                label = difficulty.name,
                                isSelected = selectedDifficulty == difficulty,
                                onClick = {
                                    selectedDifficulty = difficulty
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                        }
                    }
                }


                Button(
                    onClick = {
                        onStart(selectedDifficulty.name)
                    },
                    enabled = Difficulty.entries.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonPrimary,
                        contentColor = TextPrimary,
                        disabledContainerColor = ButtonDisabled,
                        disabledContentColor = TextPrimary.copy(alpha = Alpha.DISABLED)
                    ),
                ) {
                    Text(
                        text = "Start $gameTitle",
                        textAlign = TextAlign.Center,
                        fontSize = FontSize.REGULAR,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                    )
                }

            }
        }

    }

}

// Section Title composable
@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontFamily = RobotoCondensedFont(),
        fontSize = FontSize.EXTRA_MEDIUM,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF0077B6),
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
        Image(
            painter = painterResource(icon)
            , contentDescription = label,
            modifier = Modifier.size(40.dp)
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
