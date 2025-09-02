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


}