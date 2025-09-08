package com.futurion.apps.mindmingle.presentation.sudoku

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futurion.apps.mindmingle.data.local.dao.SudokuResultDao
import com.futurion.apps.mindmingle.data.local.entity.SudokuResultEntity
import com.futurion.apps.mindmingle.domain.model.Cell
import com.futurion.apps.mindmingle.domain.model.Difficulty
import com.futurion.apps.mindmingle.domain.repository.PuzzleGenerator
import com.futurion.apps.mindmingle.domain.state.SudokuState
import com.futurion.apps.mindmingle.presentation.games.GameGridItem
import com.futurion.apps.mindmingle.presentation.games.SampleGames.gameItems

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.collections.all
import kotlin.collections.emptyList
import kotlin.collections.emptySet
import kotlin.collections.filter
import kotlin.collections.find
import kotlin.collections.firstOrNull
import kotlin.collections.flatten
import kotlin.collections.joinToString
import kotlin.collections.map
import kotlin.collections.mapIndexed
import kotlin.collections.minus
import kotlin.collections.plus
import kotlin.collections.toMutableList
import kotlin.to


@HiltViewModel
class SudokuViewModel @Inject constructor(
    private val generator: PuzzleGenerator,
    private val dao: SudokuResultDao,
    savedStateHandle: SavedStateHandle// No longer DefaultPuzzleGenerator directly
) : ViewModel() {

    val difficulty = Difficulty.valueOf(savedStateHandle.get<String>("difficulty") ?: Difficulty.EASY.name)

    private val _state = mutableStateOf(SudokuState(isTimerRunning = true))
    val state: State<SudokuState> = _state

    private val _event = MutableSharedFlow<SudokuGameEvent>()
    val event = _event.asSharedFlow()

    private var timerJob: Job? = null

    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak

    private val _bestStreak = MutableStateFlow(0)
    val bestStreak: StateFlow<Int> = _bestStreak

    private val games = gameItems // Your sampleGames list

    fun getGameById(id: String): GameGridItem? {
        return games.find { it.id==id }
    }


    init {
//        viewModelScope.launch {
//            tryRestoreGame()
//        }

//        _state.value = _state.value.copy(difficulty = difficulty)
        generatePuzzle(difficulty)
        startTimer()
    }


    fun onAction(action: SudokuAction) {
        when (action) {
            is SudokuAction.SelectCell -> {
                _state.value = _state.value.copy(selectedCell = action.row to action.col)
            }

            is SudokuAction.EnterNumber -> {
                val selected = state.value.selectedCell ?: return
                val (row, col) = selected
                if (state.value.originalBoard[row][col].isFixed) return

                val boardCopy = state.value.board.map { it.toMutableList() }
                boardCopy[row][col] = Cell(row, col, action.number, false)

                val correctValue = state.value.solutionBoard[row][col].value
                val invalid = action.number != correctValue

                //    val invalid = !isValidMove(boardCopy, row, col, action.number)
                val newMistakes = state.value.mistakes + if (invalid) 1 else 0

                _state.value = state.value.copy(
                    board = boardCopy,
                    mistakes = newMistakes,
                    invalidCells = if (invalid)
                        state.value.invalidCells + (row to col)
                    else
                        state.value.invalidCells - (row to col),
                    isGameOver = newMistakes == 3
                )

                if (newMistakes == 3) {
                    _state.value = state.value.copy(
                        mistakes = newMistakes,
                        isTimerRunning = false,
                        board = boardCopy,
                        xpEarned = if (difficulty == Difficulty.EASY) 5
                        else if (difficulty == Difficulty.MEDIUM) 10
                        else 15
                    )
                    _state.value = _state.value.copy(
                        isGameOver = true,
                        isTimerRunning = false
                    )
                    _currentStreak.value = 0
                    _bestStreak.value = _bestStreak.value
                    viewModelScope.launch {
                        delay(500) // Let UI update mistake before showing dialog
                        saveSudokuResult()
                        _event.emit(SudokuGameEvent.GameOver)
                    }
                    return
                } else if (isPuzzleSolved(boardCopy)) {
                    _state.value = _state.value.copy(
                        isGameWon = true,
                        isTimerRunning = false,
                        xpEarned = when (difficulty) {
                            Difficulty.EASY -> 20
                            Difficulty.MEDIUM -> 35
                            Difficulty.HARD -> 50
                        }
                    )
                    _currentStreak.value +=1
                    Log.d("Current Streak Sudoku", _currentStreak.value.toString())
                    if (_currentStreak.value > _bestStreak.value) {
                        _bestStreak.value = _currentStreak.value
                    }
                    viewModelScope.launch {
                        saveSudokuResult()
                        _event.emit(SudokuGameEvent.PuzzleSolved)
                    }
                }
            }

            SudokuAction.UseHint -> {
                val currentState = _state.value
                val maxHints = 3

                if (currentState.hintsUsed >= maxHints) return

                val currentBoard = currentState.board
                val solution = currentState.solutionBoard

                // Find the first editable (non-fixed), empty or incorrect cell
                val hintCell = currentBoard.flatten().firstOrNull { cell ->
                    !cell.isFixed && (cell.value == 0 || cell.value != solution[cell.row][cell.col].value)
                }

                if (hintCell != null) {
                    val correctValue = solution[hintCell.row][hintCell.col].value

                    val updatedBoard = currentBoard.map { row ->
                        row.map { cell ->
                            if (cell.row == hintCell.row && cell.col == hintCell.col) {
                                cell.copy(value = correctValue, isHint = true)
                            } else {
                                cell
                            }
                        }
                    }
                    viewModelScope.launch {
                        _state.value = currentState.copy(
                            board = updatedBoard,
                            hintsUsed = currentState.hintsUsed + 1
                        )
                        delay(1000L) // Hint highlight shows for 1 second

                        val resetBoard = updatedBoard.map { row ->
                            row.map { it.copy(isHint = false) }
                        }

                        _state.value = _state.value.copy(board = resetBoard)
                    }
                }

                val boardCopy = state.value.board.map { it.toMutableList() }

                if (isPuzzleSolved(boardCopy)) {
                    _state.value = _state.value.copy(
                        isGameWon = true,
                        isTimerRunning = false,
                        xpEarned = when (difficulty) {
                            Difficulty.EASY -> 20
                            Difficulty.MEDIUM -> 35
                            Difficulty.HARD -> 50
                        }
                    )
                    _currentStreak.value +=1
                    Log.d("Current Streak Sudoku", _currentStreak.value.toString())
                    if (_currentStreak.value > _bestStreak.value) {
                        _bestStreak.value = _currentStreak.value
                    }


                    viewModelScope.launch {
                        saveSudokuResult()
                        _event.emit(SudokuGameEvent.PuzzleSolved)
                    }

                }
            }

            SudokuAction.RestartGame -> {
                generatePuzzle(difficulty)
            }
        }
    }

    private fun endGame() {
        timerJob?.cancel()
        _state.value = _state.value.copy(isGameOver = true, isTimerRunning = false)
       // clearSavedGame()
    }

    fun rewardUserForAd() {
        _state.value = _state.value.copy(
            xpEarned = 10,
            hintsUsed = _state.value.hintsUsed - 1
        )
    }

//    fun saveCurrentGame() = viewModelScope.launch {
//        sudokuGameRepository.saveGame(_state.value)
//    }

//    fun tryRestoreGame() = viewModelScope.launch {
//        val saved = sudokuGameRepository.loadGame()
//        if (saved != null) {
//            _state.value = saved
//            // Optionally resume timer, hints, etc.
//        } else {
//            generatePuzzle(difficulty)
//            startTimer()
//        }
//    }

//    fun clearSavedGame() = viewModelScope.launch {
//        sudokuGameRepository.clearGame()
//    }

    // Call saveCurrentGame() whenever game state changes meaningfully
//    private fun updateState(newState: SudokuState) {
//        _state.value = newState
//        viewModelScope.launch {
//            sudokuGameRepository.saveGame(newState)
//        }
//    }


    fun getCandidates(row: Int, col: Int): List<Int> {
        val currentState = _state.value
        if (currentState.board[row][col].value != 0) return emptyList()
        val board = currentState.board
        val candidates = (1..9).filter { num ->
            // check row
            board[row].all { it.value != num } &&
                    // check column
                    board.all { it[col].value != num } &&
                    // check box
                    (0..2).all { boxRow ->
                        (0..2).all { boxCol ->
                            board[row / 3 * 3 + boxRow][col / 3 * 3 + boxCol].value != num
                        }
                    }
        }
        return candidates
    }


    private suspend fun saveSudokuResult() {
        val currentState = _state.value

        val puzzleString =
            currentState.originalBoard.flatten().joinToString("") { it.value.toString() }
        val userSolutionString =
            currentState.board.flatten().joinToString("") { it.value.toString() }

        val result = SudokuResultEntity(
            difficulty = difficulty.name,
            puzzle = puzzleString,
            userSolution = userSolutionString,
            hintsUsed = currentState.hintsUsed,
            mistakesMade = currentState.mistakes,
            timeTakenSeconds = currentState.elapsedTime,
            xpEarned = currentState.xpEarned,
        )

        _state.value.elapsedTime = currentState.elapsedTime

        dao.insertResult(result)
    }


    fun generatePuzzle(difficulty: Difficulty) {
        val (puzzle, solution) = generator.generate(difficulty)
        val cells = puzzle.mapIndexed { row, list ->
            list.mapIndexed { col, value ->
                Cell(row, col, value, value != 0)
            }
        }

        val solutionBoard = solution.mapIndexed { row, list ->
            list.mapIndexed { col, value ->
                Cell(
                    row, col, value, isFixed = true,
                )
            }
        }

        Log.d("sol", solutionBoard.toString())

        _state.value = SudokuState(
            board = cells,
            originalBoard = cells,
            solutionBoard = solutionBoard,
            difficulty = _state.value.difficulty,
            hintsUsed = 0,
            mistakes = 0,
            isGameOver = false,
            isGameWon = false,
            elapsedTime = 0,
            invalidCells = emptySet(),
            selectedCell = null,
            selectedNumber = null
        )
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                val currentState = _state.value
                if (currentState.isTimerRunning) {
                    _state.value = currentState.copy(elapsedTime = currentState.elapsedTime + 1)
                } else {
                    break // Stop loop when timer stops
                }
            }
        }
    }


    private fun isValidMove(board: List<List<Cell>>, row: Int, col: Int, num: Int): Boolean {
        for (i in 0 until 9) {
            if (board[row][i].value == num && i != col) return false
            if (board[i][col].value == num && i != row) return false
        }

        val startRow = row - row % 3
        val startCol = col - col % 3
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                val cell = board[startRow + i][startCol + j]
                if (cell.value == num && (startRow + i != row || startCol + j != col)) return false
            }
        }
        return true
    }

//    private fun isPuzzleSolved(board: List<List<Cell>>): Boolean {
//        return board.all { row -> row.all { it.value != 0 } } &&
//                board.flatten().none { (row, col) ->
//                    !isValidMove(board, row, col, board[row][col].value)
//                }
//    }



    private fun isPuzzleSolved(board: List<List<Cell>>): Boolean {
        val solution = state.value.solutionBoard
        return board.flatten().all { cell ->
            cell.value == solution[cell.row][cell.col].value
        }
    }


    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
} // sudoku old code when i didn;t integrate resume where you left