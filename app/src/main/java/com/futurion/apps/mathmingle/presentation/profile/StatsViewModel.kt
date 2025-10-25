package com.futurion.apps.mathmingle.presentation.profile

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futurion.apps.mindmingle.R
import com.futurion.apps.mathmingle.data.local.entity.OverallProfileEntity
import com.futurion.apps.mathmingle.data.local.entity.PerGameStatsEntity
import com.futurion.apps.mathmingle.domain.mapping.mapToGameStats
import com.futurion.apps.mathmingle.domain.repository.StatsRepository
import com.futurion.apps.mathmingle.presentation.games.GameStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    val statsRepo: StatsRepository,
) : ViewModel() {

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()

//    private val _missions = MutableStateFlow<List<DailyMissionEntity>>(emptyList())
//    val missions: StateFlow<List<DailyMissionEntity>> = _missions

//    private val _gameStats = MutableStateFlow<List<GameStats>>(emptyList())
//    val gameStats: StateFlow<List<GameStats>> = _gameStats.asStateFlow()

    private val _profile = MutableStateFlow<OverallProfileEntity?>(null)
    val profile: StateFlow<OverallProfileEntity?> = _profile

    val profile1: StateFlow<OverallProfileEntity?> = _userId
        .filterNotNull() // wait until userId is set
        .flatMapLatest { id -> statsRepo.getProfileFlow(id) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

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

    private val _perGameStats1 =
        MutableStateFlow<Map<String, PerGameStatsEntity?>>(emptyMap())
    val perGameStats1: StateFlow<Map<String, PerGameStatsEntity?>> = _perGameStats1.asStateFlow()

    init {
        // create user row if needed and set _userId
        viewModelScope.launch {
            val id = statsRepo.initUserIfNeeded()
            Log.d("Id-stats", id)
            _userId.value = id

            id?.let {
                listOf("math_memory", "sudoku", "algebra").forEach { gameName ->
                    launch {
                        statsRepo.getPerGameStatsFlow(it, gameName).collect { stats ->
                            Log.d("Stats", "StatsView ${stats.toString()}")
                            _perGameStats1.update { currentMap ->
                                currentMap + (gameName to stats)
                            }
                        }
                    }
                }
            }

            loadProfile(id)
           // loadMissions(id)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                scheduleDailyAdReset()
            }
        }
    }

     fun loadProfile(userId: String) {
        viewModelScope.launch {
            _profile.value = statsRepo.getProfile(userId)
            Log.d("User Profile", _profile.value.toString())
            val perGameList = listOfNotNull(
                statsRepo.getPerGameStats(userId, "sudoku"),
                statsRepo.getPerGameStats(userId, "math_memory"),
                statsRepo.getPerGameStats(userId, "algebra")
            )
            _perGameStats.value = perGameList
            Log.d("BestStreak", "PER GAME STATS:${_perGameStats.value}")
      //      _gameStats.value = mapToGameStats(perGameList)
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

    fun canWatchAd(userId: String, onResult: (Boolean, Int) -> Unit) {
        viewModelScope.launch {
            val profile = statsRepo.getProfile(userId) ?: return@launch
            val today = getTodayDateMillis()

            val resetNeeded = profile.adLastWatchedDate < today
            val count = if (resetNeeded) 0 else profile.adWatchCount
            val canWatch = count < 5

            onResult(canWatch, count)
        }
    }


//    fun rewardUserForAd(userId: String) {
//        viewModelScope.launch {
//            val profile = statsRepo.getProfile(userId) ?: return@launch
//            statsRepo.updateCoins(userId, profile.coins + 10) // ✅ +10 coins for ad
//            loadProfile(userId)
//        }
//    }

    fun rewardUserForAd(userId: String) {
        viewModelScope.launch {
            val profile = statsRepo.getProfile(userId) ?: return@launch
            val today = getTodayDateMillis()

            // reset if date changed
            val resetNeeded = profile.adLastWatchedDate < today
            val newCount = if (resetNeeded) 1 else profile.adWatchCount + 1

            if (newCount <= 5) { // max 5 per day
                val updatedProfile = profile.copy(
                    adWatchCount = newCount,
                    adLastWatchedDate = today,
                    coins = profile.coins + 10 // ✅ reward coins
                )
                statsRepo.updateProfile(updatedProfile)
                loadProfile(userId)
            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleDailyAdReset() {
        val now = LocalDateTime.now()
        val midnight = now.toLocalDate().plusDays(1).atStartOfDay()
        val delayMillis = Duration.between(now, midnight).toMillis()

        viewModelScope.launch {
            delay(delayMillis)
            resetDailyAds()
            // Schedule again for the next day
            scheduleDailyAdReset()
        }
    }

    private fun resetDailyAds() {
        viewModelScope.launch {
            val userIdValue = _userId.value ?: return@launch
            val profile = statsRepo.getProfile(userIdValue) ?: return@launch
            // reset count and last watched date
            val updatedProfile = profile.copy(
                adWatchCount = 0,
                adLastWatchedDate = getTodayDateMillis()
            )
            statsRepo.updateProfile(updatedProfile)
            loadProfile(userIdValue)
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

        Log.d("AvatarId",avatarId.toString())
        val cost = when (avatarId) {
            R.drawable.avatar_4 -> 400
            R.drawable.avatar_5 -> 1500
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

    private fun getTodayDateMillis(): Long {
        val cal = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }


}
