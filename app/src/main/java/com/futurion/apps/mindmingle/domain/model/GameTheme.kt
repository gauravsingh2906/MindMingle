package com.futurion.apps.mindmingle.domain.model

import androidx.compose.ui.graphics.Color

data class GameTheme(
    val name: String,
    val backgroundImage: Int, // Resource ID of drawable
    val textColor: Color,
    val buttonColor: Color,
    val buttonTextColor: Color,
)