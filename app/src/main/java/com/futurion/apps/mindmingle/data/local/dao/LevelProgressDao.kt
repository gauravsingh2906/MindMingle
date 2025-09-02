package com.futurion.apps.mindmingle.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.futurion.apps.mindmingle.data.local.entity.LevelProgressEntity

import kotlinx.coroutines.flow.Flow

@Dao
interface LevelProgressDao {

    @Query("SELECT maxUnlockedLevel FROM level_progress WHERE gameId = :gameId LIMIT 1")
    fun getMaxUnlockedLevel(gameId: String): Flow<Int?>

    @Query("SELECT maxUnlockedLevel FROM level_progress WHERE gameId = :gameId LIMIT 1")
    suspend fun getMaxUnlockedLevelOnce(gameId: String): Int?

    @Query("UPDATE level_progress SET maxUnlockedLevel = :level WHERE gameId = :gameId")
    suspend fun updateMaxUnlockedLevel(gameId: String, level: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: LevelProgressEntity)
}
