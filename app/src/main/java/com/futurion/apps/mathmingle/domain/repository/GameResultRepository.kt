package com.futurion.apps.mathmingle.domain.repository

import com.futurion.apps.mathmingle.data.local.entity.OverallProfileEntity
import com.futurion.apps.mathmingle.data.local.entity.PerGameStatsEntity


interface GameResultRepository {
    suspend fun getPerGameStats(userId: String, gameName: String): PerGameStatsEntity?
    suspend fun getOverallProfile(userId: String): OverallProfileEntity?
}
