package com.explorova.cardiocareful.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val USER_PREFERENCES_NAME = "cardio_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_PREFERENCES_NAME)

/**
 * Manages user preferences for alert thresholds and settings using DataStore.
 */
class UserPreferences(private val context: Context) {

    companion object {
        private val MIN_HEART_RATE_KEY = intPreferencesKey("min_heart_rate")
        private val MAX_HEART_RATE_KEY = intPreferencesKey("max_heart_rate")
        private val ALERTS_ENABLED_KEY = intPreferencesKey("alerts_enabled") // 0 = false, 1 = true
        private val HAPTIC_PATTERN_KEY = stringPreferencesKey("haptic_pattern")
        private val ALERT_COOLDOWN_KEY = intPreferencesKey("alert_cooldown_seconds")
        private val NOTIFICATIONS_ENABLED_KEY = intPreferencesKey("notifications_enabled") // 0 = false, 1 = true
        private val SHOW_STATUS_NOTIFICATIONS_KEY = intPreferencesKey("show_status_notifications") // 0 = false, 1 = true

        const val DEFAULT_MIN_HR = 60
        const val DEFAULT_MAX_HR = 100
        const val DEFAULT_ALERTS_ENABLED = 1
        const val DEFAULT_HAPTIC_PATTERN = "SHORT"
        const val DEFAULT_ALERT_COOLDOWN = 30
        const val DEFAULT_NOTIFICATIONS_ENABLED = 1
        const val DEFAULT_SHOW_STATUS_NOTIFICATIONS = 0
    }

    /**
     * Flows for user preferences that update when values change
     */
    val minHeartRateFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[MIN_HEART_RATE_KEY] ?: DEFAULT_MIN_HR
    }

    val maxHeartRateFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[MAX_HEART_RATE_KEY] ?: DEFAULT_MAX_HR
    }

    val alertsEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        (prefs[ALERTS_ENABLED_KEY] ?: DEFAULT_ALERTS_ENABLED) == 1
    }

    val hapticPatternFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[HAPTIC_PATTERN_KEY] ?: DEFAULT_HAPTIC_PATTERN
    }

    val alertCooldownFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[ALERT_COOLDOWN_KEY] ?: DEFAULT_ALERT_COOLDOWN
    }

    val notificationsEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        (prefs[NOTIFICATIONS_ENABLED_KEY] ?: DEFAULT_NOTIFICATIONS_ENABLED) == 1
    }

    val showStatusNotificationsFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        (prefs[SHOW_STATUS_NOTIFICATIONS_KEY] ?: DEFAULT_SHOW_STATUS_NOTIFICATIONS) == 1
    }

    /**
     * Save minimum heart rate threshold
     */
    suspend fun setMinHeartRate(value: Int) {
        require(value in 20..220) { "Min heart rate must be between 20 and 220" }
        context.dataStore.edit { prefs ->
            prefs[MIN_HEART_RATE_KEY] = value
        }
    }

    /**
     * Save maximum heart rate threshold
     */
    suspend fun setMaxHeartRate(value: Int) {
        require(value in 20..220) { "Max heart rate must be between 20 and 220" }
        context.dataStore.edit { prefs ->
            prefs[MAX_HEART_RATE_KEY] = value
        }
    }

    /**
     * Enable or disable alerts
     */
    suspend fun setAlertsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[ALERTS_ENABLED_KEY] = if (enabled) 1 else 0
        }
    }

    /**
     * Save thresholds together (ensures min <= max)
     */
    suspend fun setThresholds(minHr: Int, maxHr: Int) {
        require(minHr in 20..220) { "Min heart rate must be between 20 and 220" }
        require(maxHr in 20..220) { "Max heart rate must be between 20 and 220" }
        require(minHr <= maxHr) { "Min heart rate must be less than or equal to max heart rate" }

        context.dataStore.edit { prefs ->
            prefs[MIN_HEART_RATE_KEY] = minHr
            prefs[MAX_HEART_RATE_KEY] = maxHr
        }
    }

    /**
     * Save selected haptic pattern
     */
    suspend fun setHapticPattern(patternName: String) {
        context.dataStore.edit { prefs ->
            prefs[HAPTIC_PATTERN_KEY] = patternName
        }
    }

    /**
     * Save alert cooldown period (in seconds)
     */
    suspend fun setAlertCooldown(seconds: Int) {
        require(seconds in 5..300) { "Alert cooldown must be between 5 and 300 seconds" }
        context.dataStore.edit { prefs ->
            prefs[ALERT_COOLDOWN_KEY] = seconds
        }
    }

    /**
     * Enable or disable system notifications
     */
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[NOTIFICATIONS_ENABLED_KEY] = if (enabled) 1 else 0
        }
    }

    /**
     * Enable or disable status notifications
     */
    suspend fun setShowStatusNotifications(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[SHOW_STATUS_NOTIFICATIONS_KEY] = if (enabled) 1 else 0
        }
    }
}
