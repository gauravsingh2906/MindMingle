package com.futurion.apps.mindmingle.presentation.games

import androidx.compose.ui.graphics.Color
import com.futurion.apps.mindmingle.R
import com.futurion.apps.mindmingle.domain.model.GameItem
import com.futurion.apps.mindmingle.domain.model.GameTheme

object SampleGames {

    val gameItems = listOf(
        GameGridItem(
            name = "Sudoku",
            description = "Sharpen your mind with logic puzzles",
            imageResId = R.drawable.figma_sudoku,
            cardColor = Color(0xFF56CCF2), // Light Blue
            xp = 1200,
            coins = 500,
            id = "sudoku"
        ),
        GameGridItem(
            name = "Math Memory",
            description = "Boost your memory and math skills",
            imageResId = R.drawable.fourthone,
            cardColor = Color(0xFF6A0572), // Purple
            xp = 1500,
            coins = 700,
            id = "math_memory",
        ),
        GameGridItem(
            name = "Algebra Quest",
            description = "Master algebra through fun challenges",
            imageResId = R.drawable.fourthone,
            cardColor = Color(0xFFEF476F), // Pinkish Red
            xp = 900,
            coins = 400,
            id = "algebra",
        ),
        GameGridItem(
            name = "New Game 1",
            description = "Coming soon",
            imageResId = R.drawable.fourthone,
            cardColor = Color.Black,
            xp = 0,
            coins = 0,
            isComingSoon = true,
            id = "4"
        ),
        GameGridItem(
            name = "New Game 2",
            description = "Coming soon",
            imageResId = R.drawable.fourthone,
            cardColor = Color.Black,
            xp = 0,
            coins = 0,
            isComingSoon = true,
            id = "5"
        )
    )

    val sampleGames = listOf(
        GameItem(
            id = "sudoku",
            name = "Sudoku",
            description = "Use logic to place numbers from 1-9 into each cell of a 9×9 grid.",
            coverImageUrl = R.drawable.fourthone,
        ),
        GameItem(
            id = "math_memory",
            name = "Math Memory Mix",
            description = "Use logic to place numbers from 1-9 into each cell of a 9×9 grid.",
            coverImageUrl = R.drawable.fourthone,
        ),
        GameItem(
            id = "algebra",
            name = "Algebra Game",
            description = "Use logic to place numbers from 1-9 into each cell of a 9×9 grid.",
            coverImageUrl = R.drawable.fourthone,
        ),
        GameItem(
            id = "1",
            name = "New Game 1",
            description = "Use logic to place numbers from 1-9 into each cell of a 9×9 grid.",
            coverImageUrl = R.drawable.fourthone,
            isComingSoon = true
        ),
        GameItem(
            id = "2",
            name = "New Game 2",
            description = "Use logic to place numbers from 1-9 into each cell of a 9×9 grid.",
            coverImageUrl = R.drawable.fourthone,
            isComingSoon = true
        )
    )


    val Default = listOf(
        GameTheme(
            name = "Classic",
            backgroundImage = R.drawable.theme1,
            textColor = Color.Black,
            buttonColor = Color(0xFFE0E0E0),
            buttonTextColor = Color.Black,
        ),
        GameTheme(
            name = "Nature",
            backgroundImage = R.drawable.secondone,
            textColor = Color.Black,
            buttonColor = Color(0xFF388E3C),
            buttonTextColor = Color.White
        ),
        GameTheme(
            name = "Galaxy",
            backgroundImage = R.drawable.thirdone,
            textColor = Color.White,
            buttonColor = Color(0xFF512DA8),
            buttonTextColor = Color.White
        ),
        GameTheme(
            name = "Cyber",
            backgroundImage = R.drawable.fourthone,
            textColor = Color(0xFF00E5FF),
            buttonColor = Color.Black,
            buttonTextColor = Color(0xFF00E5FF)
        ),
        GameTheme(
            name = "Paper",
            backgroundImage = R.drawable.fifthone,
            textColor = Color.DarkGray,
            buttonColor = Color.White,
            buttonTextColor = Color.Black
        )
    )



}