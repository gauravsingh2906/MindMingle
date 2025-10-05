package com.futurion.apps.mathmingle.presentation.level_selection

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futurion.apps.mathmingle.domain.repository.LevelRepository
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

    fun generateRewardLevels(): Map<Int, String> {
        val icons = listOf("⭐", "💎", "🔥", "🎁", "🌟", "🍀", "💡", "🎯") // unique reward icons
        val rewardLevels = mutableMapOf<Int, String>()
        var currentLevel = 1

        while (currentLevel <= 1000) {
            val icon = icons.random() // pick a random icon for each reward
            rewardLevels[currentLevel] = icon
            currentLevel += (4..10).random() // minimum gap 4, randomize a bit for variety
        }

        return rewardLevels.toSortedMap()
    }

}