package com.futurion.apps.mathmingle.domain.repository

import kotlinx.coroutines.flow.Flow

interface LevelRepository {

     fun getMaxUnlockedLevelOnce(gameId: String): Flow<Int>

    suspend fun unlockNextLevelIfNeeded(currentLevel: Int,gameId: String)

    suspend fun ensureInitialized(gameId: String)

}