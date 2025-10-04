package com.futurion.apps.mathmingle.presentation.sudoku

sealed class SudokuGameEvent {
    object GameOver : SudokuGameEvent()
    object PuzzleSolved : SudokuGameEvent()
}