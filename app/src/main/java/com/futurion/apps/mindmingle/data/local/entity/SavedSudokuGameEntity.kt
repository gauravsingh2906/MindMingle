package com.futurion.apps.mindmingle.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "saved_sudoku_game")
data class SavedSudokuGameEntity(
    @PrimaryKey val id: Int = 0, // singleton record
    val boardJson: String,
    val originalBoardJson: String,
    val solutionBoardJson: String,
    val selectedCellJson: String?,
    val selectedNumber: Int?,
    val invalidCellsJson: String,
    val hintsUsed: Int,
    val xpEarned: Int,
    val mistakes: Int,
    val isGameOver: Boolean,
    val isGameWon: Boolean,
    val elapsedTime: Int,
    val isTimerRunning: Boolean,
    val difficultyOrdinal: Int?
)
// to resume the game





