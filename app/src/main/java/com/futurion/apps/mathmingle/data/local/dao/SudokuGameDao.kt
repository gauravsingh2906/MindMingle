package com.futurion.apps.mathmingle.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.futurion.apps.mathmingle.data.local.entity.SavedSudokuGameEntity

@Dao
interface SudokuGameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(game: SavedSudokuGameEntity)

    @Query("SELECT * FROM saved_sudoku_game WHERE id = 0")
    suspend fun getSavedGame(): SavedSudokuGameEntity?

    @Query("DELETE FROM saved_sudoku_game WHERE id = 0")
    suspend fun clearSavedGame()
}
