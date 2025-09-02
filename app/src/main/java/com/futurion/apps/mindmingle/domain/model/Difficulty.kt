package com.futurion.apps.mindmingle.domain.model

import com.futurion.apps.mindmingle.R

enum class Difficulty(val blanks: IntRange,val icon: Int) {
    EASY(blanks = 36..40, icon = R.drawable.check),
    MEDIUM(41..46,icon = R.drawable.check),
    HARD(47..54,icon = R.drawable.check)
}