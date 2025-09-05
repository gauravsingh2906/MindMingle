package com.futurion.apps.mindmingle.data.repository

import android.util.Log
import com.futurion.apps.mindmingle.R
import com.futurion.apps.mindmingle.data.local.dao.OverallProfileDao
import com.futurion.apps.mindmingle.data.local.dao.PerGameStatsDao
import com.futurion.apps.mindmingle.data.local.entity.OverallProfileEntity
import com.futurion.apps.mindmingle.data.local.entity.PerGameStatsEntity
import com.futurion.apps.mindmingle.domain.repository.StatsRepository
import com.futurion.apps.mindmingle.presentation.games.SampleGames.Default
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class StatsRepositoryImpl @Inject constructor(
    private val perGameStatsDao: PerGameStatsDao,
    private val overallProfileDao: OverallProfileDao
) : StatsRepository {



//    override suspend fun updateGameResult(
//        userId: String,
//        gameName: String,
//        levelReached: Int,
//        coinsEarned: Int,
//        currentStreak: Int,
//        resultTitle:String,
//        resultMessage:String,
//        eachGameXp: Int,
//        eachGameCoin: Int,
//        bestStreak: Int,
//        isMatchWon: Boolean,
//        won: Boolean,
//        xpGained: Int,
//        hintsUsed: Int,
//        timeSpentSeconds: Long
//    ) {
//        //  val userId = initUserIfNeeded()
//        val perGame = perGameStatsDao.getStatsForGame(userId, gameName)
//
//        Log.d("MathStats", "Old stats for $gameName: $perGame")
//        val newPerGame = if (perGame == null) {
//            Log.d("MathStats", "First play, creating new stats row.")
//            PerGameStatsEntity(
//                userId = userId,
//                gameName = gameName,
//                gamesPlayed = 1,
//                wins = if (won) 1 else 0,
//                losses = if (!won) 1 else 0,
//                bestStreak = if (gameName=="math_memory") 1 else bestStreak,
//                currentStreak = if (gameName=="math_memory") 1 else currentStreak,
//                xp = xpGained,
//                resultTitle = resultTitle,
//                resultMessage = resultMessage,
//                isMatchWon = won,
//                eachGameXp = eachGameXp,
//                eachGameCoin = eachGameCoin,
//                coinsEarned = coinsEarned,
//                highestLevel = levelReached,
//                totalHintsUsed = hintsUsed,
//                totalTimeSeconds = timeSpentSeconds
//            )
//        } else {
//            perGame.copy(
//                gamesPlayed = perGame.gamesPlayed + 1,
//                wins = perGame.wins + if (won) 1 else 0,
//                losses = perGame.losses + if (!won) 1 else 0,
//                xp = perGame.xp + xpGained,
//                eachGameXp = eachGameXp,
//                eachGameCoin = eachGameCoin,
//                isMatchWon = isMatchWon,
//                coinsEarned = perGame.coinsEarned+coinsEarned,
//                bestStreak = maxOf(perGame.bestStreak, perGame.currentStreak+currentStreak),
//                currentStreak =  if (won) perGame.currentStreak + currentStreak else 0,
//                highestLevel = maxOf(perGame.highestLevel, levelReached),
//                totalHintsUsed = perGame.totalHintsUsed + hintsUsed,
//                totalTimeSeconds = perGame.totalTimeSeconds + timeSpentSeconds
//            )
//        }
//        perGameStatsDao.insertStats(newPerGame)
//
//        val overall = overallProfileDao.getProfile(userId)
//
//
//        val newOverall = if (overall == null) {
//            Log.d("MathStats", "First play, creating new overall profile.")
//            OverallProfileEntity(
//                userId = userId,
//                totalGamesPlayed = 1,
//                totalWins = if (won) 1 else 0,
//                totalLosses = if (!won) 1 else 0,
//                totalXP = xpGained,
//                coins = coinsEarned,
//                finalLevel = 1,
//                overallHighestLevel = levelReached,
//                totalHintsUsed = hintsUsed,
//                unlockedAvatars = listOf(R.drawable.avatar_1),
//                totalTimeSeconds = timeSpentSeconds
//            )
//        } else {
//            val xpForNextLevel = overall.finalLevel * 100
//            var newCurrentLevelXP = (overall.currentLevelXP ?: 0) + xpGained
//            var newLevel = overall.finalLevel
//
//            // Level up loop in case XP overflows multiple levels
//            while (newCurrentLevelXP >= xpForNextLevel) {
//                newCurrentLevelXP -= xpForNextLevel
//                newLevel += 1
//            }
//
//
//            overall.copy(
//                totalGamesPlayed = overall.totalGamesPlayed + 1,
//                totalWins = overall.totalWins + if (won) 1 else 0,
//                totalLosses = overall.totalLosses + if (!won) 1 else 0,
//                totalXP = overall.totalXP + xpGained,
//                currentLevelXP = newCurrentLevelXP,
//                finalLevel = newLevel,
//                coins = overall.coins + coinsEarned,
//                overallHighestLevel = maxOf(overall.overallHighestLevel, levelReached),
//                totalHintsUsed = overall.totalHintsUsed + hintsUsed,
//                totalTimeSeconds = overall.totalTimeSeconds + timeSpentSeconds
//            )
//        }
//        overallProfileDao.insertProfile(newOverall)
//    }

    override suspend fun updateGameResult(
        userId: String,
        gameName: String,
        levelReached: Int,
        isMatchWon: Boolean,
        coinsEarned: Int,
        currentStreak: Int,
        resultTitle: String,
        resultMessage: String,
        mathMemoryLevel:Int?,
        mathMemoryHighestLevel:Int?,
        eachGameXp: Int,
        eachGameCoin: Int,
        bestStreak: Int,
        won: Boolean,
        xpGained: Int,
        hintsUsed: Int,
        timeSpentSeconds: Long
    ) {
        //  val userId = initUserIfNeeded()
        val perGame = perGameStatsDao.getStatsForGame(userId, gameName)

        Log.d("MathStats", "Old stats for $gameName: $perGame")
        val newPerGame = if (perGame == null) {
            Log.d("MathStats", "First play, creating new stats row.")
            PerGameStatsEntity(
                userId = userId,
                gameName = gameName,
                gamesPlayed = 1,
                wins = if (won) 1 else 0,
                losses = if (!won) 1 else 0,
                bestStreak = if (gameName == "math_memory") 1 else bestStreak,
                currentStreak = if (gameName == "math_memory") 1 else currentStreak,
                xp = xpGained,
                resultTitle = resultTitle,
                resultMessage = resultMessage,
                isMatchWon = won,
                eachGameXp = eachGameXp,
                eachGameCoin = eachGameCoin,
                coinsEarned = coinsEarned,
                highestLevel = levelReached,
                totalHintsUsed = hintsUsed,
                totalTimeSeconds = timeSpentSeconds
            )
        } else {
            perGame.copy(
                gamesPlayed = perGame.gamesPlayed + 1,
                wins = perGame.wins + if (won) 1 else 0,
                losses = perGame.losses + if (!won) 1 else 0,
                xp = perGame.xp + xpGained,
                resultTitle = resultTitle,
                resultMessage = resultMessage,
                eachGameXp = eachGameXp,
                eachGameCoin = eachGameCoin,
                isMatchWon = isMatchWon,
                coinsEarned = perGame.coinsEarned+coinsEarned,
                bestStreak = maxOf(perGame.bestStreak, perGame.currentStreak+currentStreak),
                currentStreak =  if (won) perGame.currentStreak + currentStreak else 0,
                highestLevel = maxOf(perGame.highestLevel, levelReached),
                totalHintsUsed = perGame.totalHintsUsed + hintsUsed,
                totalTimeSeconds = perGame.totalTimeSeconds + timeSpentSeconds
            )
        }
        perGameStatsDao.insertStats(newPerGame)

        val overall = overallProfileDao.getProfile(userId)


        val newOverall = if (overall == null) {
            Log.d("MathStats", "First play, creating new overall profile.")
            OverallProfileEntity(
                userId = userId,
                gameName = gameName,
                unlockedAvatars = listOf(R.drawable.avatar_1),
                coins = coinsEarned,
                totalGamesPlayed = 1,
                totalWins = if (won) 1 else 0,
                totalLosses = if (!won) 1 else 0,
                totalXP = xpGained,
                overallHighestLevel = levelReached,
                mathMemoryCurrentLevel = mathMemoryLevel ?: 1,
                mathMemoryHighestLevel = mathMemoryHighestLevel ?:1,
                totalHintsUsed = hintsUsed,
                totalTimeSeconds = timeSpentSeconds,
            )
        } else {
            val xpForNextLevel = overall.finalLevel * 100
            var newCurrentLevelXP = (overall.currentLevelXP ?: 0) + xpGained
            var newLevel = overall.finalLevel

            // Level up loop in case XP overflows multiple levels
            while (newCurrentLevelXP >= xpForNextLevel) {
                newCurrentLevelXP -= xpForNextLevel
                newLevel += 1
            }


            overall.copy(
                userId = userId,
                gameName = gameName,
                coins = overall.coins + coinsEarned,
                totalGamesPlayed = overall.totalGamesPlayed + 1,
                totalWins = overall.totalWins + if (won) 1 else 0,
                totalLosses = overall.totalLosses + if (!won) 1 else 0,
                totalXP = overall.totalXP + xpGained,
                overallHighestLevel = maxOf(overall.overallHighestLevel, levelReached),
                currentLevelXP = newCurrentLevelXP,
                finalLevel = newLevel,
                mathMemoryCurrentLevel = mathMemoryLevel ?:1,
                mathMemoryHighestLevel =  maxOf(overall.mathMemoryHighestLevel,mathMemoryLevel ?:1),
                totalHintsUsed = overall.totalHintsUsed + hintsUsed,
                totalTimeSeconds = overall.totalTimeSeconds + timeSpentSeconds,
            )
        }
        overallProfileDao.insertProfile(newOverall)
    }


    override suspend fun initUserIfNeeded(username: String?): String {
        val existing = overallProfileDao.getAnyUser()

        if (existing != null) return existing.userId


        val newId = UUID.randomUUID().toString()
        Log.d("Id", "First id generated $newId")

        val adjectives = listOf("Cool", "Silent", "Funky", "Smart", "Dark", "Fire")
        val nouns = listOf("Ninja", "Cat", "Wizard", "Dragon", "Knight", "Fox")
        val number = (100..999).random()

        val username = "${adjectives.random()}${nouns.random()}_$number"

        var defaultAvatarId = listOf<Int>(
            R.drawable.avatar_1,
            R.drawable.avatar_4,
            R.drawable.avatar_2,
            R.drawable.avatar_5,
            R.drawable.avatar_6,
            R.drawable.avatar_7
        )
        val defaultUnlockedAvatars = defaultAvatarId.random()
        val defaultAvatar = R.drawable.avatar_1


        val us = OverallProfileEntity(
            userId = newId,
            username = username,
            avatarUri = defaultUnlockedAvatars,
            unlockedAvatars = listOf(defaultUnlockedAvatars),
        )
        overallProfileDao.insertProfile(us)
        // create default total stats row
        overallProfileDao.updateProfile(us)
        // overallProfileDao.insertTotalStats(TotalStatsEntity(userId = newId))
        return newId
    }

    override suspend fun updateUsername(userId: String, newUsername: String) {
        val profile = overallProfileDao.getProfile(userId) ?: return
        overallProfileDao.updateProfile(profile.copy(username = newUsername,))
    }

    override suspend fun unlockUsername(userId: String, newUsername: String) {
        val profile = overallProfileDao.getProfile(userId) ?: return
        if (!profile.unlockedUsernames.contains(newUsername)) {
            val updatedNames = profile.unlockedUsernames + newUsername
            overallProfileDao.updateProfile(profile.copy(unlockedUsernames = updatedNames,))
        }
    }


    // to unlock avatars / deduct coins or check level
    override suspend fun unlockAvatar(userId: String, newAvatarUri: Int) {
        val profile = overallProfileDao.getProfile(userId) ?: return

        if (!profile.unlockedAvatars.contains(newAvatarUri)) {
            val updatedAvatars = profile.unlockedAvatars + newAvatarUri
            overallProfileDao.updateProfile(profile.copy( unlockedAvatars = updatedAvatars,))
        }
    }

    // update the current avatar to new one
    override suspend fun updateAvatar(userId: String, newAvatarUri: Int) {
        val profile = overallProfileDao.getProfile(userId) ?: return
        if (profile.unlockedAvatars.contains(newAvatarUri)) {
            overallProfileDao.updateProfile(profile.copy( avatarUri = newAvatarUri,))
        }
    }


    override suspend fun updateCoins(userId: String, newCoins: Int) {
        val profile = overallProfileDao.getProfile(userId) ?: return
        overallProfileDao.updateProfile(profile.copy(coins = newCoins,))
    }

    override suspend fun getProfile(userId: String) = overallProfileDao.getProfile(userId)


    override suspend fun updateProfile(profile: OverallProfileEntity) {
        overallProfileDao.updateProfile(profile)
    }

    override suspend fun getPerGameStats(userId: String, gameName: String) =
        perGameStatsDao.getStatsForGame(userId, gameName)

    override suspend fun updateMathMemoryCurrentLevel(userId: String, level: Int) {
        val profile = overallProfileDao.getProfile(userId) ?: return
        overallProfileDao.updateProfile(profile.copy(mathMemoryCurrentLevel = level,))
    }

    override suspend fun updateMathMemoryHighestLevel(userId: String, newLevel: Int) {
        val profile = overallProfileDao.getProfile(userId) ?: return
        if (newLevel > (profile.mathMemoryHighestLevel ?: 0)) {
            val updated = profile.copy(mathMemoryHighestLevel = newLevel,)
            overallProfileDao.updateProfile(updated)
        }
    }

    override suspend fun getUnlockedThemes(userId: String): Set<String> {
        val profile = overallProfileDao.getProfile(userId)
        return profile?.unlockedThemes ?: setOf(Default[0].name)
    }

    override suspend fun saveUnlockedThemes(userId: String, themes: Set<String>) {
        val profile = overallProfileDao.getProfile(userId) ?: return
        overallProfileDao.updateProfile(profile.copy(unlockedThemes = themes))
    }


}