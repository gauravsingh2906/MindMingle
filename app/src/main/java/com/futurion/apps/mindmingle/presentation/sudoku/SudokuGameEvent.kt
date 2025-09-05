package com.futurion.apps.mindmingle.presentation.sudoku

sealed class SudokuGameEvent {
    object GameOver : SudokuGameEvent()
    object PuzzleSolved : SudokuGameEvent()
}