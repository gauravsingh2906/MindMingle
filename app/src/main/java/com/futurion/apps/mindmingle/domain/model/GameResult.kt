package com.futurion.apps.mindmingle.domain.model

data class GameResult(
    val won: Boolean,
    val xpEarned: Int,
    val score: Int,
    val streak: Int,
    val bestStreak: Int,
    val hintsUsed: Int,
    val timeSpent: Long,
    val level: Int
)

