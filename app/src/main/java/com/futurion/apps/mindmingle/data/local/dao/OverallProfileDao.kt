package com.futurion.apps.mindmingle.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.futurion.apps.mindmingle.data.local.entity.OverallProfileEntity

@Dao
interface OverallProfileDao {

    @Query("SELECT * FROM overall_profile LIMIT 1")
    suspend fun getAnyUser(): OverallProfileEntity?

    @Query("SELECT * FROM overall_profile WHERE userId = :userId LIMIT 1")
    suspend fun getProfile(userId: String): OverallProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: OverallProfileEntity)

    @Update
    suspend fun updateProfile(profile: OverallProfileEntity)
}