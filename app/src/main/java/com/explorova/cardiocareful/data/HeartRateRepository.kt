package com.explorova.cardiocareful.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

/**
 * Repository for managing heart rate data access.
 */
class HeartRateRepository(context: Context) {

    private val dao = CardioDatabase.getInstance(context).heartRateReadingDao()

    /**
     * Log a new heart rate reading
     */
    suspend fun logReading(
        heartRate: Double,
        alertTriggered: Boolean = false,
        alertType: String? = null
    ) {
        val reading = HeartRateReading.create(heartRate, alertTriggered, alertType)
        dao.insert(reading)
    }

    /**
     * Get all heart rate readings as a Flow
     */
    fun getAllReadingsFlow(): Flow<List<HeartRateReading>> {
        return dao.getAllReadings()
    }

    /**
     * Get readings from the last N hours
     */
    fun getReadingsFromLastHoursFlow(hours: Int): Flow<List<HeartRateReading>> {
        return dao.getReadingsFromLastHours(hours)
    }

    /**
     * Get alert readings as a Flow
     */
    fun getAlertReadingsFlow(): Flow<List<HeartRateReading>> {
        return dao.getAlertReadings()
    }

    /**
     * Get the latest reading
     */
    suspend fun getLatestReading(): HeartRateReading? {
        return dao.getLatestReading()
    }

    /**
     * Get statistics for the last N hours
     */
    suspend fun getStatsForLastHours(hours: Int): HeartRateStats? {
        val row = dao.getStatsFromLastHours(hours)
        return row?.let {
            HeartRateStats(
                minHeartRate = it.minHeartRate,
                maxHeartRate = it.maxHeartRate,
                averageHeartRate = it.averageHeartRate,
                readingCount = it.readingCount,
                alertCount = it.alertCount,
                timeRange = "Last $hours hours"
            )
        }
    }

    /**
     * Get statistics for the last hour
     */
    suspend fun getStatsForLastHour(): HeartRateStats? {
        return getStatsForLastHours(1)
    }

    /**
     * Get statistics for the last 24 hours
     */
    suspend fun getStatsForLast24Hours(): HeartRateStats? {
        return getStatsForLastHours(24)
    }

    /**
     * Delete readings older than N hours (data retention)
     */
    suspend fun deleteReadingsOlderThan(hours: Int): Int {
        return dao.deleteOlderThan(hours)
    }

    /**
     * Clear all readings
     */
    suspend fun deleteAllReadings() {
        dao.deleteAll()
    }

    /**
     * Clean up old data (keep last 7 days)
     */
    suspend fun cleanupOldData() {
        deleteReadingsOlderThan(24 * 7) // Delete older than 7 days
    }
}
