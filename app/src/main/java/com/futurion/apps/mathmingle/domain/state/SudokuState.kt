package com.futurion.apps.mathmingle.domain.state

import com.futurion.apps.mathmingle.domain.model.Cell
import com.futurion.apps.mathmingle.domain.model.Difficulty

data class SudokuState(
    val board: List<List<Cell>> = emptyList(),
    val originalBoard: List<List<Cell>> = emptyList(),
    val solutionBoard: List<List<Cell>> = emptyList(),
    val selectedCell: Pair<Int, Int>? = null,
    val selectedNumber: Int? = null,
    val invalidCells: Set<Pair<Int, Int>> = emptySet(),
    val hintsUsed: Int=0,
    val xpEarned:Int=0,
    val mistakes: Int = 0,
    val isGameOver: Boolean = false,
    val isGameWon: Boolean = false,
    var elapsedTime: Int = 0,
    val isTimerRunning: Boolean = true,
    val difficulty: Difficulty? = Difficulty.EASY
)
