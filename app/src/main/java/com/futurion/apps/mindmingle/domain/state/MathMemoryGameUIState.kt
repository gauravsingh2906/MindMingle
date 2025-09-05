package com.futurion.apps.mindmingle.domain.state

import com.futurion.apps.mindmingle.domain.model.GameTheme
import com.futurion.apps.mindmingle.presentation.games.SampleGames.Default
import com.futurion.apps.mindmingle.presentation.math_memory.MathMemoryGameState

data class MathMemoryGameUIState(
    val game: MathMemoryGameState,
    val theme: ThemeState = ThemeState()
)

data class ThemeState(
    val unlockedThemes: Set<String> = setOf(Default.first().name), // store theme names
    val selectedTheme: GameTheme = Default.first() // store full GameTheme object
)