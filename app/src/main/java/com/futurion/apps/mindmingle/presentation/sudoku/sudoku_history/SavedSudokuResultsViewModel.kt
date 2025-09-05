package com.futurion.apps.mindmingle.presentation.sudoku.sudoku_history

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futurion.apps.mindmingle.data.local.dao.SudokuResultDao
import com.futurion.apps.mindmingle.data.local.entity.SudokuResultEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedSudokuResultsViewModel @Inject constructor(
    private val dao: SudokuResultDao
) : ViewModel() {

    private val _results = mutableStateOf<List<SudokuResultEntity>>(emptyList())
    val results: State<List<SudokuResultEntity>> = _results

    init {
        viewModelScope.launch {
            _results.value = dao.getAllResults()
        }
    }
}
