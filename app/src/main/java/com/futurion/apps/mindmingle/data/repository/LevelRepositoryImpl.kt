package com.futurion.apps.mindmingle.data.repository

import com.futurion.apps.mindmingle.data.local.dao.LevelProgressDao
import com.futurion.apps.mindmingle.data.local.entity.LevelProgressEntity
import com.futurion.apps.mindmingle.domain.repository.LevelRepository1
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.text.insert

class LevelRepositoryImpl(
    private val dao: LevelProgressDao
): LevelRepository1 {
    override suspend fun getMaxUnlockedLevelOnce(gameId: String): Flow<Int> {
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