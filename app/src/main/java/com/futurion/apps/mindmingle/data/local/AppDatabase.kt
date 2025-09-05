package com.futurion.apps.mindmingle.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.futurion.apps.mindmingle.data.converters.Converters
import com.futurion.apps.mindmingle.data.converters.IntListConverter
import com.futurion.apps.mindmingle.data.converters.StringListConverter
import com.futurion.apps.mindmingle.data.local.dao.LevelProgressDao
import com.futurion.apps.mindmingle.data.local.dao.OverallProfileDao
import com.futurion.apps.mindmingle.data.local.dao.PerGameStatsDao
import com.futurion.apps.mindmingle.data.local.dao.SudokuGameDao
import com.futurion.apps.mindmingle.data.local.dao.SudokuResultDao
import com.futurion.apps.mindmingle.data.local.dao.ThemeUnlockDao
import com.futurion.apps.mindmingle.data.local.entity.LevelProgressEntity
import com.futurion.apps.mindmingle.data.local.entity.OverallProfileEntity
import com.futurion.apps.mindmingle.data.local.entity.PerGameStatsEntity
import com.futurion.apps.mindmingle.data.local.entity.SavedSudokuGameEntity
import com.futurion.apps.mindmingle.data.local.entity.SudokuResultEntity
import com.futurion.apps.mindmingle.data.local.entity.ThemeUnlockEntity

@Database(
    entities = [OverallProfileEntity::class, PerGameStatsEntity::class, LevelProgressEntity::class, SavedSudokuGameEntity::class, SudokuResultEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(IntListConverter::class, StringListConverter::class, Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun perGameStatsDao(): PerGameStatsDao

    abstract fun overallProfileDao(): OverallProfileDao

    abstract fun levelProgressDao(): LevelProgressDao

    abstract fun sudokuResultDao(): SudokuResultDao

 //   abstract fun dailyMissionDao(): DailyMissionDao

//    abstract fun themeUnlockDao(): ThemeUnlockDao

    abstract fun sudokuGameDao(): SudokuGameDao




  //   abstract fun gameResultDao(): GameResultDao


}