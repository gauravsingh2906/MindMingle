package com.futurion.apps.mindmingle.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.futurion.apps.mindmingle.data.local.entity.ThemeUnlockEntity

@Dao
interface ThemeUnlockDao {
    @Query("SELECT * FROM theme_unlocks")
    suspend fun getAllThemeUnlocks(): List<ThemeUnlockEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateThemeUnlock(entity: ThemeUnlockEntity)

    @Query("SELECT * FROM theme_unlocks WHERE themeName = :themeName LIMIT 1")
    suspend fun getUnlockByThemeName(themeName: String): ThemeUnlockEntity?
}
