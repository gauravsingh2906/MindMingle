package com.futurion.apps.mindmingle.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("theme_unlocks")
data class ThemeUnlockEntity(
    @PrimaryKey val themeName: String,
    val isUnlocked: Boolean,
    val userProgressAds: Int
)

