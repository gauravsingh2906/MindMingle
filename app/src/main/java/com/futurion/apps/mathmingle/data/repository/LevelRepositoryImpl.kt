package com.futurion.apps.mathmingle.data.repository

import com.futurion.apps.mathmingle.data.local.dao.LevelProgressDao
import com.futurion.apps.mathmingle.data.local.entity.LevelProgressEntity
import com.futurion.apps.mathmingle.domain.repository.LevelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LevelRepositoryImpl(
    private val dao: LevelProgressDao
): LevelRepository {
    override fun getMaxUnlockedLevelOnce(gameId: String): Flow<Int> {
       return dao.getMaxUnlockedLevel(gameId).map { it ?: 1 }
    }

    override suspend fun unlockNextLevelIfNeeded(currentLevel: Int, gameId: String) {
        val maxLevel = dao.getMaxUnlockedLevelOnce(gameId) ?: 1
        if (currentLevel >= maxLevel) {
            dao.updateMaxUnlockedLevel(gameId, currentLevel + 1)
        }
    }

    override suspend fun ensureInitialized(gameId: String) {
        val existing = dao.getMaxUnlockedLevelOnce(gameId)
        if (existing == null) {
            dao.insert(LevelProgressEntity(gameId = gameId, maxUnlockedLevel = 1))
        }
    }


}