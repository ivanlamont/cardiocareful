package com.explorova.cardiocareful.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a user-defined alert profile with custom thresholds and settings.
 */
@Entity(tableName = "alert_profiles")
data class AlertProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val minHeartRate: Int,
    val maxHeartRate: Int,
    val hapticPattern: String, // Pattern name (e.g., "SHORT", "PULSE")
    val alertCooldownSeconds: Int,
    val notificationsEnabled: Boolean,
    val isActive: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        /**
         * Create a default profile with standard values
         */
        fun createDefault(): AlertProfile {
            return AlertProfile(
                name = "Default",
                minHeartRate = 60,
                maxHeartRate = 100,
                hapticPattern = "SHORT",
                alertCooldownSeconds = 30,
                notificationsEnabled = true,
                isActive = true
            )
        }

        /**
         * Create a cardio workout profile with higher HR ranges
         */
        fun createCardioProfile(): AlertProfile {
            return AlertProfile(
                name = "Cardio Workout",
                minHeartRate = 100,
                maxHeartRate = 180,
                hapticPattern = "PULSE",
                alertCooldownSeconds = 20,
                notificationsEnabled = true,
                isActive = false
            )
        }

        /**
         * Create a recovery profile with lower HR ranges
         */
        fun createRecoveryProfile(): AlertProfile {
            return AlertProfile(
                name = "Recovery",
                minHeartRate = 40,
                maxHeartRate = 80,
                hapticPattern = "DOUBLE_TAP",
                alertCooldownSeconds = 60,
                notificationsEnabled = false,
                isActive = false
            )
        }

        /**
         * Create a warm-up profile with moderate HR ranges
         */
        fun createWarmUpProfile(): AlertProfile {
            return AlertProfile(
                name = "Warm-up",
                minHeartRate = 70,
                maxHeartRate = 120,
                hapticPattern = "LONG",
                alertCooldownSeconds = 30,
                notificationsEnabled = true,
                isActive = false
            )
        }
    }
}
