package com.futurion.apps.mindmingle.domain.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

data class GameTheme(
    val name: String,
    val backgroundImage: Int, // Resource ID of drawable
    val textColor: Color,
    val buttonColor: Color,
    val buttonTextColor: Color,
    val accentColor: Color = Color.Unspecified, // For highlights / progress / selection
    val overlayColor: Color? = null, // For darkening/lightening busy backgrounds
    val fontFamily: FontFamily? = null, // Optional custom font
    val fontWeight: FontWeight = FontWeight.Normal // Default weight
)