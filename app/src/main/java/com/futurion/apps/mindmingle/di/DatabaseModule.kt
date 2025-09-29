package com.futurion.apps.mindmingle.di

import android.content.Context
import androidx.room.Room
import com.futurion.apps.mindmingle.data.local.AppDatabase
import com.futurion.apps.mindmingle.data.local.dao.LevelProgressDao
import com.futurion.apps.mindmingle.data.local.dao.OverallProfileDao
import com.futurion.apps.mindmingle.data.local.dao.PerGameStatsDao
import com.futurion.apps.mindmingle.data.local.dao.SudokuGameDao
import com.futurion.apps.mindmingle.data.local.dao.SudokuResultDao
import com.futurion.apps.mindmingle.data.repository.GameResultRepositoryImpl
import com.futurion.apps.mindmingle.data.repository.LevelRepositoryImpl
import com.futurion.apps.mindmingle.data.repository.StatsRepositoryImpl
import com.futurion.apps.mindmingle.domain.LevelManager
import com.futurion.apps.mindmingle.domain.model.generateMemoryLevel
import com.futurion.apps.mindmingle.domain.repository.GameResultRepository
import com.futurion.apps.mindmingle.domain.repository.LevelRepository
import com.futurion.apps.mindmingle.domain.repository.StatsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DB_NAME = "game_stats_db"

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideLevelManager(): LevelManager =
        LevelManager(::generateMemoryLevel)


    @Provides
    fun providePerGameStatsDao(db: AppDatabase): PerGameStatsDao = db.perGameStatsDao()

    @Provides
    fun provideOverallProfileDao(db: AppDatabase): OverallProfileDao = db.overallProfileDao()

    @Provides
    @Singleton
    fun provideStatsRepository(
        perGameStatsDao: PerGameStatsDao,
        overallProfileDao: OverallProfileDao
    ): StatsRepository = StatsRepositoryImpl(perGameStatsDao, overallProfileDao)

    @Provides
    @Singleton
    fun provideGameResultRepository(
        perGameStatsDao: PerGameStatsDao,
        overallProfileDao: OverallProfileDao
    ): GameResultRepository {
        return GameResultRepositoryImpl(perGameStatsDao, overallProfileDao)
    }

  //  @Provides
  //  fun provideSudokuResultDao(db: AppDatabase): SudokuResultDao = db.sudokuResultDao()

    @Provides
    fun provideSudokuResultDao(database: AppDatabase): SudokuResultDao {
        return database.sudokuResultDao()
    }

//    @Provides
//    fun provideSudokuGameDao(db: AppDatabase): SudokuGameDao = db.sudokuGameDao()

    @Provides
    fun provideLevelProgressDao(db: AppDatabase): LevelProgressDao = db.levelProgressDao()

    @Provides
    @Singleton
    fun provideLevelProgressRepository(
        levelProgressDao: LevelProgressDao
    ): LevelRepository = LevelRepositoryImpl(levelProgressDao)

//    @Provides
//    fun provideThemeUnlockDao(database: AppDatabase): ThemeUnlockDao {
//        return database.themeUnlockDao()
//    }

}