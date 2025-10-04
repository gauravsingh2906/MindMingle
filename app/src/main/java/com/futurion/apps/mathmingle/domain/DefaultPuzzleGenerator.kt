package com.futurion.apps.mathmingle.domain



import android.util.Log
import com.futurion.apps.mathmingle.domain.model.Difficulty
import com.futurion.apps.mathmingle.domain.repository.PuzzleGenerator
import javax.inject.Inject

class DefaultPuzzleGenerator @Inject constructor() : PuzzleGenerator {

    override fun generate(difficulty: Difficulty): Pair<List<List<Int>>, List<List<Int>>> {
        val board = Array(9) { IntArray(9) }
        fillDiagonalBoxes(board)
        solve(board)
        val solutionBoard = board.map { it.copyOf().toList() }
        Log.d("Solution", solutionBoard.toString())
        removeCells(board, difficulty)
        val puzzleBoard = board.map { it.toList() }
        return puzzleBoard to solutionBoard
    }

    private fun fillDiagonalBoxes(board: Array<IntArray>) {
        for (i in 0 until 9 step 3) {
            fillBox(board, i, i)
        }
    }

    private fun fillBox(board: Array<IntArray>, row: Int, col: Int) {
        val nums = (1..9).shuffled()
        var idx = 0
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                board[row + i][col + j] = nums[idx++]
            }
        }
    }

    private fun solve(board: Array<IntArray>): Boolean {
        for (row in 0..8) {
            for (col in 0..8) {
                if (board[row][col] == 0) {
                    for (num in 1..9) {
                        if (isValid(board, row, col, num)) {
                            board[row][col] = num
                            if (solve(board)) return true
                            board[row][col] = 0
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    fun isValid(board: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
        for (i in 0..8) {
            if (board[row][i] == num || board[i][col] == num) return false
        }

        val boxRowStart = row - row % 3
        val boxColStart = col - col % 3
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                if (board[boxRowStart + i][boxColStart + j] == num) return false
            }
        }

        return true
    }

    private fun countSolutions(board: Array<IntArray>, limit: Int = 2): Int {
        var count = 0
        fun backtrack(): Boolean {
            for (row in 0..8) {
                for (col in 0..8) {
                    if (board[row][col] == 0) {
                        for (num in 1..9) {
                            if (isValid(board, row, col, num)) {
                                board[row][col] = num
                                if (backtrack()) return true
                                board[row][col] = 0
                            }
                        }
                        return false
                    }
                }
            }
            count++
            return count >= limit
        }
        backtrack()
        return count
    }

    private fun removeCells(board: Array<IntArray>, difficulty: Difficulty) {
//        val attempts = when (difficulty) {
//            Difficulty.EASY -> 30
//            Difficulty.MEDIUM -> 40
//            Difficulty.HARD -> 50
//        }
        val attempts = difficulty.blanks.random()
        Log.d("Attempts", attempts.toString())
        Log.d("Attempts", difficulty.toString())
        // Log.d("Attempts", Difficulty.valueOf(difficulty))

        val positions = mutableListOf<Pair<Int, Int>>()
        for (i in 0..8) {
            for (j in 0..8) {
                positions.add(i to j)
            }
        }

        positions.shuffle()
        var removed = 0

        for ((row, col) in positions) {
            val backup = board[row][col]
            board[row][col] = 0

            val boardCopy = Array(9) { r -> board[r].clone() }
            if (countSolutions(boardCopy) != 1) {
                board[row][col] = backup
            } else {
                removed++
                if (removed >= attempts) break
            }
        }
    }
}