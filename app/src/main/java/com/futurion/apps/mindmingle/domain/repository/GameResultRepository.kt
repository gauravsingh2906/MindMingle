package com.futurion.apps.mindmingle.domain.repository

import com.futurion.apps.mindmingle.data.local.entity.OverallProfileEntity
import com.futurion.apps.mindmingle.data.local.entity.PerGameStatsEntity


interface GameResultRepository {
    suspend fun getPerGameStats(userId: String, gameName: String): PerGameStatsEntity?
    suspend fun getOverallProfile(userId: String): OverallProfileEntity?
}
