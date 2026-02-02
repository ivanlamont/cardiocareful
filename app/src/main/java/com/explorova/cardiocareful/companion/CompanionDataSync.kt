package com.explorova.cardiocareful.companion

import android.content.Context
import android.util.Log
import com.explorova.cardiocareful.TAG
import com.explorova.cardiocareful.data.AlertProfile
import com.explorova.cardiocareful.data.ProfileRepository
import kotlinx.coroutines.flow.Flow

/**
 * Handles data synchronization between watch and companion phone app.
 *
 * This class provides methods to share watch app data with a companion phone application.
 * A companion app could use these methods to:
 * - Display live heart rate data
 * - Configure alert profiles on the watch
 * - View historical heart rate data
 * - Manage multiple profiles
 *
 * Communication can be implemented using:
 * - Android Wear Data Layer API (for Wear OS)
 * - Firebase Cloud Messaging
 * - HTTP/REST endpoints
 * - Bluetooth connections
 */
class CompanionDataSync(
    private val context: Context,
    private val profileRepository: ProfileRepository
) {

    /**
     * Get current active profile to send to companion app
     */
    suspend fun getActiveProfileForCompanion(): AlertProfile? {
        return try {
            profileRepository.getActiveProfile()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting profile for companion", e)
            null
        }
    }

    /**
     * Get all profiles for companion app display
     */
    fun getAllProfilesFlow(): Flow<List<AlertProfile>> {
        return profileRepository.getAllProfilesFlow()
    }

    /**
     * Update profile from companion app
     */
    suspend fun updateProfileFromCompanion(profile: AlertProfile) {
        try {
            profileRepository.updateProfile(profile)
            Log.d(TAG, "Profile updated from companion app: ${profile.name}")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating profile from companion", e)
        }
    }

    /**
     * Activate a profile from companion app
     */
    suspend fun activateProfileFromCompanion(profileId: Int) {
        try {
            profileRepository.activateProfile(profileId)
            Log.d(TAG, "Profile activated from companion app: $profileId")
        } catch (e: Exception) {
            Log.e(TAG, "Error activating profile from companion", e)
        }
    }

    companion object {
        /**
         * Recommended communication channels for companion app:
         *
         * 1. Wear Data Layer API (Recommended for Wear OS)
         *    - Built-in Android Wear support
         *    - Automatic sync between watch and phone
         *    - Uses: DataClient, WearableListenerService
         *
         * 2. Firebase Cloud Messaging
         *    - Real-time messaging
         *    - Cloud-based synchronization
         *    - Works across network boundaries
         *
         * 3. SharedPreferences with ContentProvider
         *    - Simple local synchronization
         *    - Works on same device
         *    - Limited to direct Android access
         *
         * 4. REST API
         *    - For cloud-based companion app
         *    - HTTP endpoints on the watch (if available)
         *    - Most flexible but requires more setup
         */

        /**
         * Example implementation guide for companion app:
         *
         * class WearableListenerService : WearableListenerService() {
         *     override fun onDataChanged(events: DataEventBuffer) {
         *         for (event in events) {
         *             if (event.type == DataEvent.TYPE_CHANGED) {
         *                 val dataItem = event.dataItem
         *                 if (dataItem.uri.path == "/alert_profile") {
         *                     // Handle profile update from companion
         *                     val profile = parseProfileFromDataMap(dataItem.data)
         *                     updateWatchProfile(profile)
         *                 }
         *             }
         *         }
         *     }
         * }
         */
    }
}
