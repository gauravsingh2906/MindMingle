package com.futurion.apps.mathmingle.presentation.games

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.futurion.apps.mathmingle.R
import com.futurion.apps.mathmingle.domain.model.GameItem
import com.futurion.apps.mathmingle.domain.model.GameTheme

object SampleGames {


    val gameItems = listOf(
        GameGridItem(
            name = "Sudoku",
            description = "Sharpen your mind with logic puzzles",
            aboutGame = "Sudoku sharpens your logical thinking and concentration by challenging you to fill a 9x9 grid with numbers without repeats in rows, columns, or blocks. It’s a fun brain workout that boosts memory, patience, and problem-solving skills.",
            imageResId = R.drawable.suddoku,
            cardColor = Color(0xFF56CCF2), // Light Blue
            xp = 1200,
            coins = listOf(
                "Earn coins by winning Sudoku games—higher difficulties give more coins",
                "Win 3 consecutive games to get a bonus coin",
                "Complete a game in 5 minutes to get a bonus coin"
            ),
            id = "sudoku",
            steps = listOf(
                "The goal is to fill every empty square with numbers 1 to 9.",
                "Each row, column, and 3x3 box must contain all numbers from 1 to 9 without any repeats.",
                "Some numbers are already filled in as clues — use these to help you.",
                "Use logic to figure out where each number can go—no guessing!"
            ),
            isComingSoon = false
        ),
        GameGridItem(
            name = "Math Memory",
            description = "Boost your memory and math skills",
            aboutGame = "Boost your memory and mental math skills by applying sequential math steps accurately. This game sharpens your focus, calculation, and cognitive agility in a fun and challenging way.",
            imageResId = R.drawable.final_math,
            cardColor = Color(0xFF6A0572), // Purple
            xp = 1500,
            coins = listOf(
                "Earn coins by beating your previous best streak",
                "Get bonus coins for achieving a streak greater than 3"
            ),
            id = "math_memory",
            steps = listOf(
                "A sequence of math operations (like +2, -1, ×3) appears one after another.",
                "Your task is to apply these math steps in order, from left to right, starting with the initial number.",
                "The game challenges your memory and math calculation skills by increasing the length and complexity of steps."
            ),
            isComingSoon = false,
        ),
        GameGridItem(
            name = "Algebra Quest",
            description = "Master algebra through fun challenge",
            imageResId = R.drawable.final_algebra,
            cardColor = Color(0xFFFF9800),
            xp = 900,
            coins = listOf(
                "Earn coins by beating your previous best streak",
                "Get bonus coins for achieving a streak greater than 3",
                "Earn coins by reaching a reward levels in the game"
            ),
            id = "algebra",
            aboutGame = "Train your algebra skills with a variety of math challenges including missing numbers, operators, true/false expressions, and more. This game builds your problem-solving and critical thinking in an engaging, step-by-step way",
            steps = listOf(
                "Read the algebra question carefully—it could ask for a missing number, missing operator, or whether an expression is true or false",
                "Choose or input the correct answer based on the question type",
                "Think through the math operations step-by-step to solve the problem accurately",
                "Answer within the time limit to progress and improve your skills."
            ),
            isComingSoon = false,
        ),
        GameGridItem(
            name = "New Game 1",
            description = "Coming soon",
            imageResId = R.drawable.fourthone,
            cardColor = Color.Black,
            xp = 0,
            coins = listOf(),
            isComingSoon = true,
            id = "4",
            aboutGame = "",
            steps = listOf()
        ),
        GameGridItem(
            name = "New Game 2",
            description = "Coming soon",
            imageResId = R.drawable.fourthone,
            cardColor = Color.Black,
            xp = 0,
            coins = listOf(),
            isComingSoon = true,
            id = "5",
            aboutGame = "",
            steps = listOf()
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
            name = "Paper",
            backgroundImage = R.drawable.fifthone,
            textColor = Color.White,
            buttonColor = Color.White,
            buttonTextColor = Color.Black
        ),
        GameTheme(
            name = "Classic",
            backgroundImage = R.drawable.theme_ocean_waves,
            textColor = Color.White,
            buttonColor = Color(0xFFE0E0E0),
            buttonTextColor = Color.Black,
            accentColor = Color(0xFFFFC107), // Yellow highlight
            overlayColor = Color.Black.copy(alpha = 0.25f),
            fontWeight = FontWeight.Bold
        ),
        GameTheme(
            name = "City",
            backgroundImage = R.drawable.city_skyline_portrait,
            textColor = Color.White,
            buttonColor = Color(0xFF3DB2FF),
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
            textColor = Color.White,
            buttonColor = Color.Black,
            buttonTextColor = Color.White
        ),
        GameTheme(
            name = "Night",
            backgroundImage = R.drawable.seventh_one,
            textColor = Color.White,
            buttonColor = Color.White,
            buttonTextColor = Color.Black
        ),
        GameTheme(
            name = "Dark",
            backgroundImage = R.drawable.sixth_one,
            textColor = Color.White,
            buttonColor = Color.White,
            buttonTextColor = Color.Black
        ),



    )



}