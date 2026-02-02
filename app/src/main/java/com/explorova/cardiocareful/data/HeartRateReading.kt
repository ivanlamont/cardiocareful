package com.explorova.cardiocareful.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Entity representing a heart rate reading with timestamp.
 */
@Entity(tableName = "heart_rate_readings")
data class HeartRateReading(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val heartRate: Double,
    val timestamp: LocalDateTime,
    val alertTriggered: Boolean = false,
    val alertType: String? = null
) {
    companion object {
        /**
         * Create a new reading with current timestamp
         */
        fun create(heartRate: Double, alertTriggered: Boolean = false, alertType: String? = null): HeartRateReading {
            return HeartRateReading(
                heartRate = heartRate,
                timestamp = LocalDateTime.now(),
                alertTriggered = alertTriggered,
                alertType = alertType
            )
        }
    }
}

/**
 * Statistics for heart rate data
 */
data class HeartRateStats(
    val minHeartRate: Double,
    val maxHeartRate: Double,
    val averageHeartRate: Double,
    val readingCount: Int,
    val alertCount: Int,
    val timeRange: String // e.g., "Last Hour", "Last 24 Hours"
)
