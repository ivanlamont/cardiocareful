package com.explorova.cardiocareful.data

import android.content.Context
import android.util.Log
import com.explorova.cardiocareful.TAG
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing alert profiles.
 */
class ProfileRepository(context: Context) {

    private val dao = CardioDatabase.getInstance(context).alertProfileDao()

    /**
     * Create a new profile
     */
    suspend fun createProfile(profile: AlertProfile): Long {
        return try {
            dao.insert(profile)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating profile", e)
            -1
        }
    }

    /**
     * Update an existing profile
     */
    suspend fun updateProfile(profile: AlertProfile) {
        try {
            dao.update(profile)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating profile", e)
        }
    }

    /**
     * Delete a profile
     */
    suspend fun deleteProfile(profile: AlertProfile) {
        try {
            dao.delete(profile)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting profile", e)
        }
    }

    /**
     * Get all profiles as a Flow
     */
    fun getAllProfilesFlow(): Flow<List<AlertProfile>> {
        return dao.getAllProfiles()
    }

    /**
     * Get the currently active profile
     */
    suspend fun getActiveProfile(): AlertProfile? {
        return try {
            dao.getActiveProfile()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting active profile", e)
            null
        }
    }

    /**
     * Get a profile by name
     */
    suspend fun getProfileByName(name: String): AlertProfile? {
        return try {
            dao.getProfileByName(name)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting profile by name", e)
            null
        }
    }

    /**
     * Activate a profile by ID
     */
    suspend fun activateProfile(profileId: Int) {
        try {
            dao.deactivateAll()
            dao.activateProfile(profileId)
            Log.d(TAG, "Activated profile: $profileId")
        } catch (e: Exception) {
            Log.e(TAG, "Error activating profile", e)
        }
    }

    /**
     * Initialize default profiles if none exist
     */
    suspend fun initializeDefaultProfilesIfNeeded() {
        try {
            val count = dao.getProfileCount()
            if (count == 0) {
                // Create default profiles
                dao.insert(AlertProfile.createDefault())
                dao.insert(AlertProfile.createCardioProfile())
                dao.insert(AlertProfile.createRecoveryProfile())
                dao.insert(AlertProfile.createWarmUpProfile())
                Log.d(TAG, "Default profiles created")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing default profiles", e)
        }
    }

    /**
     * Create a custom profile with given parameters
     */
    suspend fun createCustomProfile(
        name: String,
        minHr: Int,
        maxHr: Int,
        hapticPattern: String,
        cooldownSeconds: Int,
        notificationsEnabled: Boolean
    ): Long {
        return try {
            val profile = AlertProfile(
                name = name,
                minHeartRate = minHr,
                maxHeartRate = maxHr,
                hapticPattern = hapticPattern,
                alertCooldownSeconds = cooldownSeconds,
                notificationsEnabled = notificationsEnabled
            )
            dao.insert(profile)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating custom profile", e)
            -1
        }
    }
}
