package com.futurion.apps.mindmingle.presentation.math_memory

import com.futurion.apps.mindmingle.domain.model.MemoryLevel

data class MathMemoryGameState(
    val level: MemoryLevel,
    val isShowCards: Boolean = true,
    val userInput: String = "",
    val showResult: Boolean = false,
    val isCorrect: Boolean = false
)