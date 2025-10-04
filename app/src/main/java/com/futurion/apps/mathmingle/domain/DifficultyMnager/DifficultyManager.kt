package com.futurion.apps.mathmingle.domain.DifficultyMnager

import com.futurion.apps.mathmingle.domain.model.Operations

class DifficultyManager {

    // Parameters controlled by difficulty:
    data class DifficultySettings(
        val numCards: Int,
        val allowedOps: List<Operations>,
        val addSubRange: IntRange,
        val mulRange: List<Int>,
        val clampRange: Int
    )

    // Adjust difficulty based on streak and recent success (e.g. last few levels)
//    fun getSettings(levelNumber: Int, currentStreak: Int, successRate: Double): DifficultySettings {
//        // Example logic:
//        // Increase difficulty faster if streak >3 and successRate > 0.8
//        val baseLevel = when {
//            currentStreak > 5 && successRate > 0.9 -> levelNumber + 2
//            currentStreak > 3 && successRate > 0.8 -> levelNumber + 1
//            else -> levelNumber
//        }
//
//        val numCards = minOf(2 + baseLevel / 2, 8)
//
//        val allowedOps = when {
//            baseLevel < 5 -> listOf(Op.ADD)
//            baseLevel < 10 -> listOf(Op.ADD, Op.SUB)
//            baseLevel < 15 -> listOf(Op.ADD, Op.SUB, Op.MUL)
//            else -> Op.entries.toList()
//        }
//
//        val addSubRange = when {
//            baseLevel < 5 -> 1..5
//            else -> 1..9
//        }
//
//        val mulRange = when {
//            baseLevel < 10 -> listOf(2)
//            baseLevel < 15 -> (2..3).toList()
//            else -> (2..4).toList()
//        }
//
//        val clampRange = when {
//            baseLevel < 5 -> 20
//            baseLevel < 10 -> 50
//            else -> 100
//        }
//
//        return DifficultySettings(numCards, allowedOps, addSubRange, mulRange, clampRange)
//    }
}
