package com.futurion.apps.mathmingle.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.futurion.apps.mathmingle.data.converters.Converters
import com.futurion.apps.mathmingle.data.converters.IntListConverter
import com.futurion.apps.mathmingle.data.converters.StringListConverter
import com.futurion.apps.mathmingle.data.local.dao.LevelProgressDao
import com.futurion.apps.mathmingle.data.local.dao.OverallProfileDao
import com.futurion.apps.mathmingle.data.local.dao.PerGameStatsDao
import com.futurion.apps.mathmingle.data.local.dao.SudokuResultDao
import com.futurion.apps.mathmingle.data.local.entity.LevelProgressEntity
import com.futurion.apps.mathmingle.data.local.entity.OverallProfileEntity
import com.futurion.apps.mathmingle.data.local.entity.PerGameStatsEntity
import com.futurion.apps.mathmingle.data.local.entity.SudokuResultEntity

@Database(
    entities = [OverallProfileEntity::class, PerGameStatsEntity::class, LevelProgressEntity::class,SudokuResultEntity::class],
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

  //  abstract fun sudokuGameDao(): SudokuGameDao




  //   abstract fun gameResultDao(): GameResultDao


}