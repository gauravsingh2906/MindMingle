package com.futurion.apps.mindmingle.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Screen {

    @Serializable
    data object HomeGraph: Screen()

    @Serializable
    data object Home: Screen()

    @Serializable
    data object Games: Screen()

    @Serializable
    data object Profile : Screen()

    @Serializable
    data class GameDetailScreen(val gameId: String) : Screen()

    @Serializable
    data class SudokuScreen(val difficulty: String) : Screen()

    @Serializable
    data object SudokuHistoryScreen : Screen()

    @Serializable
    data class MathMemoryScreen(val level:Int) : Screen()

    @Serializable
    data class AlgebraGameScreen(val level: Int) : Screen()

    @Serializable
    data class LevelSelection(val id: String): Screen()

    @Serializable
    data class ThemeSelectionScreen(val userId: String): Screen()

    @Serializable
    data object CommonResultScreen : Screen()
}