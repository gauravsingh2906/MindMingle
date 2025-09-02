package com.futurion.apps.mindmingle.presentation.games

import androidx.compose.ui.graphics.Color
import com.futurion.apps.mindmingle.R
import com.futurion.apps.mindmingle.domain.model.GameItem
import com.futurion.apps.mindmingle.domain.model.GameTheme

object SampleGames {

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