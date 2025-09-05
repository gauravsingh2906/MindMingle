package com.futurion.apps.mindmingle.presentation.game_result

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futurion.apps.mindmingle.R
import com.futurion.apps.mindmingle.domain.model.UniversalResult
import com.futurion.apps.mindmingle.domain.model.UnlockAvatarInfo
import com.futurion.apps.mindmingle.domain.repository.GameResultRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameResultViewModel @Inject constructor(
    private val repo: GameResultRepository
) : ViewModel() {

    private val _resultState = MutableStateFlow<UniversalResult?>(null)
    val resultState: StateFlow<UniversalResult?> get() = _resultState

    fun loadResult(userId: String, gameName: String, algebraScore: Int) {
        viewModelScope.launch {
            val perGame = repo.getPerGameStats(userId, gameName)
            val profile = repo.getOverallProfile(userId)
            Log.e("Profile",profile.toString())
            Log.e("PerGame",perGame.toString())
            if (perGame != null && profile != null) {
                val avatarInfo = UnlockAvatarInfo(
                    name = "Wizard Avatar",
                    unlockAt = "Level 10",
                    unlocked = profile.overallHighestLevel >= 10,
                    avatarId = R.drawable.avatar_7,
                    backgroundId = R.drawable.fourthone
                )
                val themeInfo = UnlockAvatarInfo(
                    name = "Neon Pulse",
                    unlockAt = "Level 10",
                    unlocked = profile.overallHighestLevel >= 10,
                    avatarId = R.drawable.avatar_5,
                    backgroundId = R.drawable.fourthone
                )
                val coins = profile.coins
                val unlockMsg = "Reach Level 15 to unlock Arctic Theme!"
                _resultState.value = UniversalResult(
                    xpEarned = profile.currentLevelXP, // total xp displayed on result
                    coinsEarned = profile.coins, // total coins displayed on result
                    currentStreak = perGame.currentStreak,
                    bestStreak = perGame.bestStreak,
                    isMatchWon = perGame.isMatchWon,
                    won = perGame.isMatchWon, // Add win/lose if you track last session), // Add win/lose if you track last session
                    unlockAvatarInfo = avatarInfo,
                    unlockThemeInfo = themeInfo,
                    unlockMessage = unlockMsg,
                    canReplay = true,
                    canNextLevel = true,
                    title = "Congratulations!",
                    overallLevel = profile.overallHighestLevel,
                    score = algebraScore,
                    eachGameXp = perGame.eachGameXp,
                    eachGameCoin = perGame.eachGameCoin,
                    resultTitle = perGame.resultTitle,
                    resultMessage = perGame.resultMessage,
                    mathMemoryLevel = profile.mathMemoryCurrentLevel,
                    bestTimeSec = perGame.totalTimeSeconds.toInt()
                )

            }
        }
    }
}
