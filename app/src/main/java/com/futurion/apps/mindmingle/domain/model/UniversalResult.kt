package com.futurion.apps.mindmingle.domain.model


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UniversalResult(
    val title: String,
    val resultTitle: String,
    val resultMessage: String,
    val score: Int? = null,
    val won: Boolean? = null,
    val isMatchWon: Boolean? = null,
    val overallLevel: Int? = null,
    val gameLevel: Int? = null,
    val bestTimeSec: Int? = null,
    val difficulty: String? = null,
    val hintsUsed: Int? = null,
    val unlockAvatarInfo: UnlockAvatarInfo? = null, // info for unlocking avatars
    val unlockThemeInfo: UnlockAvatarInfo? = null, // info for unlocking themes
    val unlockBackgroundIngo: UnlockAvatarInfo? = null,
    val unlockMessage: String? = null, // Reach x to unlock y
    val canReplay: Boolean = false, // can replay the math game
    val canNextLevel: Boolean = false, // can go to next level for math game
    val canHome: Boolean = true,
    val xpEarned: Int,
    val eachGameXp: Int,
    val eachGameCoin: Int,
    val coinsEarned: Int,
    val currentStreak: Int,
    val bestStreak: Int,
    val highestScore: Int? = null,
    val mathMemoryLevel: Int?=null,
): Parcelable

@Parcelize
data class UnlockAvatarInfo(
    val name:String,
    val unlockAt: String,
    val unlocked: Boolean, // true if user just unlocked
    val avatarId: Int,
    val backgroundId: Int,
): Parcelable

