package com.futurion.apps.mindmingle.domain.mapping

import com.futurion.apps.mindmingle.data.local.entity.PerGameStatsEntity
import com.futurion.apps.mindmingle.presentation.games.GameStats

fun mapToGameStats(perGameStats: List<PerGameStatsEntity>): List<GameStats> {
    return perGameStats.map { entity ->
        val gamesPlayed = entity.wins + entity.losses

        val winPercentage = if (gamesPlayed>0) {
            ((entity.wins.toDouble() / gamesPlayed.toDouble()) * 100).toInt()
        } else 0

        GameStats(
            gamesPlayed = gamesPlayed,
            wins = entity.wins,
            xpTotal = entity.xp,
            currentStreak = entity.currentStreak,
        )

    }
}