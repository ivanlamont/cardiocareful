package com.explorova.cardiocareful.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for AlertProfile entities.
 */
@Dao
interface AlertProfileDao {

    /**
     * Insert a new profile
     */
    @Insert
    suspend fun insert(profile: AlertProfile): Long

    /**
     * Update a profile
     */
    @Update
    suspend fun update(profile: AlertProfile)

    /**
     * Delete a profile
     */
    @Delete
    suspend fun delete(profile: AlertProfile)

    /**
     * Get all profiles ordered by creation date
     */
    @Query("SELECT * FROM alert_profiles ORDER BY createdAt DESC")
    fun getAllProfiles(): Flow<List<AlertProfile>>

    /**
     * Get active profile
     */
    @Query("SELECT * FROM alert_profiles WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveProfile(): AlertProfile?

    /**
     * Get profile by name
     */
    @Query("SELECT * FROM alert_profiles WHERE name = :name LIMIT 1")
    suspend fun getProfileByName(name: String): AlertProfile?

    /**
     * Get profile by ID
     */
    @Query("SELECT * FROM alert_profiles WHERE id = :id")
    suspend fun getProfileById(id: Int): AlertProfile?

    /**
     * Set a profile as active and deactivate others
     */
    @Query("UPDATE alert_profiles SET isActive = 0")
    suspend fun deactivateAll()

    /**
     * Activate a specific profile by ID
     */
    @Query("UPDATE alert_profiles SET isActive = 1 WHERE id = :id")
    suspend fun activateProfile(id: Int)

    /**
     * Get count of profiles
     */
    @Query("SELECT COUNT(*) FROM alert_profiles")
    suspend fun getProfileCount(): Int

    /**
     * Delete all profiles
     */
    @Query("DELETE FROM alert_profiles")
    suspend fun deleteAll()
}
