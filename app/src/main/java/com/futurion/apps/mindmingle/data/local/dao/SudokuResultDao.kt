package com.futurion.apps.mindmingle.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.futurion.apps.mindmingle.data.local.entity.SudokuResultEntity

@Dao
interface SudokuResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: SudokuResultEntity)

    @Query("SELECT * FROM sudoku_results ORDER BY timestamp DESC")
    suspend fun getAllResults(): List<SudokuResultEntity>

    @Delete
    suspend fun deleteResult(result: SudokuResultEntity)
}