package com.futurion.apps.mindmingle.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.futurion.apps.mindmingle.data.local.entity.PerGameStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PerGameStatsDao {
    @Query("SELECT * FROM per_game_statistics WHERE userId = :userId AND gameName = :gameName LIMIT 1")
    suspend fun getStatsForGame(userId: String, gameName: String): PerGameStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStats(stats: PerGameStatsEntity)
}


