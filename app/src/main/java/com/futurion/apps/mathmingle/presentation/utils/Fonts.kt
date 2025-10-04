package com.futurion.apps.mathmingle.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.futurion.apps.mathmingle.R

@Composable
fun BebasNeueFont() = FontFamily(
    Font(R.font.bebas_neue_regular)
)

@Composable
fun RobotoCondensedFont() = FontFamily(
    Font(R.font.roboto_condensed_medium)
)

object FontSize {
    val EXTRA_SMALL = 10.sp
    val SMALL = 12.sp
    val REGULAR = 14.sp
    val EXTRA_REGULAR = 16.sp
    val MEDIUM = 18.sp
    val EXTRA_MEDIUM = 20.sp
    val LARGE = 30.sp
    val EXTRA_LARGE = 40.sp
}