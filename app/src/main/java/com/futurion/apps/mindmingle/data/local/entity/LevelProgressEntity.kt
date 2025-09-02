package com.futurion.apps.mindmingle.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "level_progress")
data class LevelProgressEntity(
    @PrimaryKey val gameId: String, // example: "algebra"
    val maxUnlockedLevel: Int
)
