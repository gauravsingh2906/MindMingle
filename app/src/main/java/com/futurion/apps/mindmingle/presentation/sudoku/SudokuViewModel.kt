package com.futurion.apps.mindmingle.presentation.sudoku

import androidx.lifecycle.ViewModel
import com.futurion.apps.mindmingle.presentation.games.GameGridItem

import com.futurion.apps.mindmingle.presentation.home.gameItems
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SudokuViewModel @Inject constructor() : ViewModel() {

    private val games = gameItems // Your sampleGames list

    fun getGameById(id: String): GameGridItem? {
        return games.find { it.id == id }
    }


}