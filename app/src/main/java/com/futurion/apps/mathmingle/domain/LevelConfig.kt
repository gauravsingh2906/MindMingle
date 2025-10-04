package com.futurion.apps.mathmingle.domain

data class LevelConfig(val level: Int) {

    fun numberRange(): IntRange {
        return when {
            level <= 5 -> 0..10
            level <= 10 -> 0..20
            level <= 15 -> -20..30
            level <= 20 -> -50..50
            else -> -100..200
        }
    }

    fun allowedOperators(): List<Char> {
        return when {
            level <= 5 -> listOf('+', '-')                 // only + and - at easy levels
            level <= 10 -> listOf('+', '-', '×')           // introduce multiplication
            level <= 15 -> listOf('+', '-', '×', '÷')      // add division
            else -> listOf('+', '-', '×', '÷')             // keep all for higher levels
        }
    }

    fun allowDecimals(): Boolean {
        // Allow decimals after level 10
        return false
    }

    fun timeLimitSeconds(): Int {
        return when {
            level <= 5 -> 10
            level <= 10 -> 12
            level <= 15 -> 14
            level <= 20 -> 16
            else -> 5
        }
    }

    fun xpForCorrectBase(): Int {
        return when {
            level <= 5 -> 10
            level <= 10 -> 15
            level <= 15 -> 25
            level <= 20 -> 35
            else -> 50
        }
    }


}
