package com.futurion.apps.mathmingle.presentation.sudoku

sealed class SudokuAction {


    data class SelectCell(val row: Int, val col: Int) : SudokuAction()
    data class EnterNumber(val number: Int) : SudokuAction()
    object UseHint : SudokuAction()
    object RestartGame : SudokuAction()

}