package com.futurion.apps.mathmingle.data.repository

import com.futurion.apps.mathmingle.data.local.dao.OverallProfileDao
import com.futurion.apps.mathmingle.data.local.dao.PerGameStatsDao
import com.futurion.apps.mathmingle.data.local.entity.OverallProfileEntity
import com.futurion.apps.mathmingle.data.local.entity.PerGameStatsEntity
import com.futurion.apps.mathmingle.domain.repository.GameResultRepository
import javax.inject.Singleton

@Singleton
class GameResultRepositoryImpl(
    private val perGameStatsDao: PerGameStatsDao,
    private val overallProfileDao: OverallProfileDao
) : GameResultRepository {

    override suspend fun getPerGameStats(userId: String, gameName: String): PerGameStatsEntity? {
        return perGameStatsDao.getStatsForGame(userId, gameName)
    }

    override suspend fun getOverallProfile(userId: String): OverallProfileEntity? {
        return overallProfileDao.getProfile(userId)
    }
}
