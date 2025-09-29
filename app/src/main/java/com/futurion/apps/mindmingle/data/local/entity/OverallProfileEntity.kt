package com.futurion.apps.mindmingle.data.local.entity

import android.graphics.drawable.Drawable
import com.futurion.apps.mindmingle.R
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.futurion.apps.mindmingle.data.converters.IntListConverter
import com.futurion.apps.mindmingle.data.converters.StringListConverter
import com.futurion.apps.mindmingle.presentation.games.SampleGames.Default

@Entity(tableName = "overall_profile")
data class OverallProfileEntity(
    @PrimaryKey var userId: String,
    val gameName: String = "math_memory",
    val username: String = "Player",
    val avatarUri: Int? = null,          // URI string/path to avatar image
    @TypeConverters(IntListConverter::class)
    val unlockedAvatars: List<Int> = listOf(R.drawable.avatar_1),
    @TypeConverters(StringListConverter::class)
    val unlockedUsernames: List<String> = emptyList(),
    val coins: Int = 0,                     // coins to spend on avatar/name customizations
    val totalGamesPlayed: Int = 0,
    val totalWins: Int = 0,
    val totalLosses: Int = 0,
    val totalDraws: Int = 0,
    val totalXP: Int = 0,
    val overallHighestLevel: Int = 1,
    val currentLevelXP: Int = 0,
    val finalLevel: Int = 1,
    // 👇 Separate fields
    val mathMemoryCurrentLevel: Int = 1,   // where to continue
    val mathMemoryHighestLevel: Int = 1,   // highest ever reached
    val lastLoginDate: Long = 0L,  // Timestamp
    val streakCount: Int = 0,
    val totalHintsUsed: Int = 0,
    val totalTimeSeconds: Long = 0L,
    val selectedThemeName: String = Default[0].name,
    val unlockedThemes: Set<String> = emptySet()
)