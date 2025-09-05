package com.futurion.apps.mindmingle.presentation.profile

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futurion.apps.mindmingle.R
import com.futurion.apps.mindmingle.data.local.entity.OverallProfileEntity
import com.futurion.apps.mindmingle.data.local.entity.PerGameStatsEntity
import com.futurion.apps.mindmingle.domain.repository.StatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val statsRepo: StatsRepository,
) : ViewModel() {

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()

//    private val _missions = MutableStateFlow<List<DailyMissionEntity>>(emptyList())
//    val missions: StateFlow<List<DailyMissionEntity>> = _missions


    private val _profile = MutableStateFlow<OverallProfileEntity?>(null)
    val profile: StateFlow<OverallProfileEntity?> = _profile

    private val _perGameStats = MutableStateFlow<List<PerGameStatsEntity>>(emptyList())
    val perGameStats: StateFlow<List<PerGameStatsEntity>> = _perGameStats

    //    init {
//        loadProfile(userId = "c97f320d-4681-4e07-aeca-f305ea33d7e9")
//    }'

//    val adjectives = listOf("Cool", "Silent", "Funky", "Smart", "Dark", "Fire")
//    val nouns = listOf("Ninja", "Cat", "Wizard", "Dragon", "Knight", "Fox")
//    val number = (100..999).random()
//
//    val username = "${adjectives.random()}${nouns.random()}_$number"

    var defaultAvatarId = listOf<Int>(R.drawable.avatar_1, R.drawable.avatar_5)
    val defaultUnlockedAvatars = defaultAvatarId


    init {
        // create user row if needed and set _userId
        viewModelScope.launch {
            val id = statsRepo.initUserIfNeeded()
            Log.d("Id-stats", id)
            _userId.value = id
            loadProfile(id)
       //     loadMissions(id)
        }
    }

    fun loadProfile(userId: String) {
        viewModelScope.launch {
            _profile.value = statsRepo.getProfile(userId)
            Log.d("User", _profile.value.toString())
            _perGameStats.value = listOfNotNull(
                statsRepo.getPerGameStats(userId, "sudoku"),
                statsRepo.getPerGameStats(userId, "math_memory"),
                statsRepo.getPerGameStats(userId, "algebra")
            )
        }
    }

    fun updateGameAndProfile(
        userId: String, gameName: String, level: Int,coins:Int, won: Boolean, xp: Int,
        hints: Int, timeSec: Long,currentStreak:Int,bestStreak:Int, resultTitle:String,
        resultMessage:String,isMatchWon:Boolean,eachGameXp:Int,eachGameCoin:Int
    ) = viewModelScope.launch {
        statsRepo.updateGameResult(
            userId = userId,
            gameName = gameName,
            levelReached = level,
            coinsEarned = coins,
            won = won,
            xpGained = xp,
            hintsUsed = hints,
            timeSpentSeconds = timeSec,
            currentStreak = currentStreak,
            bestStreak = bestStreak,
            eachGameXp = eachGameXp,
            eachGameCoin = eachGameCoin,
            resultTitle = resultTitle,
            resultMessage = resultMessage,
            isMatchWon = isMatchWon,
        )
        loadProfile(userId)
    }

    fun rewardUserForAd(userId: String) {
        viewModelScope.launch {
            val profile = statsRepo.getProfile(userId) ?: return@launch
            statsRepo.updateCoins(userId, profile.coins + 10) // ✅ +10 coins for ad
            loadProfile(userId)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleMidnightRefresh() {
        val now = LocalDateTime.now()
        val midnight = now.toLocalDate().plusDays(1).atStartOfDay()
        val delayMillis = Duration.between(now, midnight).toMillis()

        viewModelScope.launch {
            delay(delayMillis)
            // Reload missions after midnight
          //  loadMissions(_userId.value ?: return@launch)
            // Optionally, schedule again for next day
            scheduleMidnightRefresh()
        }
    }

//    private fun loadMissions(userId: String) {
//        viewModelScope.launch {
//            _missions.value = dailyMissionRepo.getMissionsForToday(userId)
//        }
//    }

//    fun updateProgress(gameName: String, missionType: String, minutes: Int) {
//        viewModelScope.launch {
//            dailyMissionRepo.updateMissionProgress(
//                userId = _userId.value ?: return@launch,
//                gameName = gameName,
//                missionType = missionType,
//                incrementBy = minutes
//            )
//            loadMissions(_userId.value ?: return@launch)
//        }
//    }

    fun changeUsername(userId: String, username: String) = viewModelScope.launch {
        statsRepo.updateUsername(userId, username)
        loadProfile(userId)
    }

    fun unlockUsername(userId: String, username: String) = viewModelScope.launch {
        statsRepo.unlockUsername(userId, username)
        changeUsername(userId, username)
    }




    fun changeAvatar(userId: String, avatarUri: Int) = viewModelScope.launch {
        statsRepo.updateAvatar(userId, avatarUri)
        loadProfile(userId)
    }

    fun unlockAvatar(userId: String, avatarId: Int) = viewModelScope.launch {
        val profile = statsRepo.getProfile(userId) ?: return@launch

        val cost = when (avatarId) {
            R.drawable.avatar_4, R.drawable.avatar_5 -> 500
            else -> 0
        }
        if (cost > 0 && profile.coins < cost) {
            // Show some coin shortage UI or rewarded ad prompt
            return@launch
        }

        if (cost > 0) {
            statsRepo.updateCoins(userId, profile.coins - cost)
        }
        statsRepo.unlockAvatar(userId, avatarId)
        statsRepo.updateAvatar(userId, avatarId)
        loadProfile(userId)
    }

}
