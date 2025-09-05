package com.futurion.apps.mindmingle.presentation.level_selection

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futurion.apps.mindmingle.domain.repository.LevelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LevelSelectionViewModel @Inject constructor(
    private val repository: LevelRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val id = savedStateHandle.get<String>("id")

    val maxUnlockedLevel = repository.getMaxUnlockedLevelOnce(gameId = id ?: "algebra")
        .map { level -> if (level < 1) 1 else level }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            1
        ) // default 1



}