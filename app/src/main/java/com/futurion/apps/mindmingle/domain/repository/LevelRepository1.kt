package com.futurion.apps.mindmingle.domain.repository

import kotlinx.coroutines.flow.Flow

interface LevelRepository1 {

    suspend fun getMaxUnlockedLevelOnce(gameId: String): Flow<Int>

    suspend fun unlockNextLevelIfNeeded(currentLevel: Int,gameId: String)

    suspend fun ensureInitialized(gameId: String)

}