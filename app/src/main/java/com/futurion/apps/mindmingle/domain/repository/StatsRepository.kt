package com.futurion.apps.mindmingle.domain.repository

import com.futurion.apps.mindmingle.data.local.entity.OverallProfileEntity
import com.futurion.apps.mindmingle.data.local.entity.PerGameStatsEntity

interface StatsRepository {

    suspend fun updateMathMemoryHighestLevel(userId: String, newLevel: Int)

    suspend fun updateGameResult(
        userId: String,
        gameName: String,
        levelReached: Int,
        isMatchWon: Boolean,
        coinsEarned:Int,
        currentStreak:Int,
        resultTitle:String,
        resultMessage:String,
        mathMemoryLevel:Int?=null,
        mathMemoryHighestLevel:Int?=null,
        eachGameXp:Int,
        eachGameCoin:Int,
        bestStreak:Int,
        won: Boolean,
        xpGained: Int,
        hintsUsed: Int,
        timeSpentSeconds: Long
    )

    suspend fun initUserIfNeeded(username: String?="Player"): String

    suspend fun updateUsername(userId: String, newUsername: String)

    suspend fun unlockUsername(userId: String, newUsername: String)

    suspend fun updateAvatar(userId: String, newAvatarUri: Int)

    suspend fun unlockAvatar(userId: String, newAvatarUri: Int)
    suspend fun updateCoins(userId: String, newCoins: Int)

    suspend fun getProfile(userId: String): OverallProfileEntity?

    suspend fun updateProfile(profile: OverallProfileEntity)
    suspend fun getPerGameStats(userId: String, gameName: String): PerGameStatsEntity?


    suspend fun updateMathMemoryCurrentLevel(userId: String, level: Int)

    suspend fun getUnlockedThemes(userId: String): Set<String>
    suspend fun saveUnlockedThemes(userId: String, themes: Set<String>)

}
