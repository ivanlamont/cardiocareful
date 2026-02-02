package com.explorova.cardiocareful.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Data Access Object for HeartRateReading entities.
 */
@Dao
interface HeartRateReadingDao {

    /**
     * Insert a new heart rate reading
     */
    @Insert
    suspend fun insert(reading: HeartRateReading)

    /**
     * Insert multiple readings
     */
    @Insert
    suspend fun insertAll(readings: List<HeartRateReading>)

    /**
     * Delete a reading
     */
    @Delete
    suspend fun delete(reading: HeartRateReading)

    /**
     * Get all readings ordered by timestamp (newest first)
     */
    @Query("SELECT * FROM heart_rate_readings ORDER BY timestamp DESC")
    fun getAllReadings(): Flow<List<HeartRateReading>>

    /**
     * Get readings from the last N hours
     */
    @Query(
        """
        SELECT * FROM heart_rate_readings
        WHERE timestamp > datetime('now', '-' || :hours || ' hours')
        ORDER BY timestamp DESC
        """
    )
    fun getReadingsFromLastHours(hours: Int): Flow<List<HeartRateReading>>

    /**
     * Get readings for a specific day
     */
    @Query(
        """
        SELECT * FROM heart_rate_readings
        WHERE DATE(timestamp) = :date
        ORDER BY timestamp DESC
        """
    )
    fun getReadingsForDate(date: String): Flow<List<HeartRateReading>>

    /**
     * Get alert readings only
     */
    @Query(
        """
        SELECT * FROM heart_rate_readings
        WHERE alertTriggered = 1
        ORDER BY timestamp DESC
        """
    )
    fun getAlertReadings(): Flow<List<HeartRateReading>>

    /**
     * Get the latest reading
     */
    @Query("SELECT * FROM heart_rate_readings ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestReading(): HeartRateReading?

    /**
     * Get average heart rate
     */
    @Query("SELECT AVG(heartRate) FROM heart_rate_readings")
    suspend fun getAverageHeartRate(): Double

    /**
     * Get min heart rate
     */
    @Query("SELECT MIN(heartRate) FROM heart_rate_readings")
    suspend fun getMinHeartRate(): Double?

    /**
     * Get max heart rate
     */
    @Query("SELECT MAX(heartRate) FROM heart_rate_readings")
    suspend fun getMaxHeartRate(): Double?

    /**
     * Get reading count
     */
    @Query("SELECT COUNT(*) FROM heart_rate_readings")
    suspend fun getReadingCount(): Int

    /**
     * Get alert count
     */
    @Query("SELECT COUNT(*) FROM heart_rate_readings WHERE alertTriggered = 1")
    suspend fun getAlertCount(): Int

    /**
     * Get statistics for readings from last N hours
     */
    @Query(
        """
        SELECT
            COALESCE(MIN(heartRate), 0) as minHeartRate,
            COALESCE(MAX(heartRate), 0) as maxHeartRate,
            COALESCE(AVG(heartRate), 0) as averageHeartRate,
            COUNT(*) as readingCount,
            SUM(CASE WHEN alertTriggered = 1 THEN 1 ELSE 0 END) as alertCount
        FROM heart_rate_readings
        WHERE timestamp > datetime('now', '-' || :hours || ' hours')
        """
    )
    suspend fun getStatsFromLastHours(hours: Int): HeartRateStatsRow?

    /**
     * Delete readings older than N hours
     */
    @Query(
        """
        DELETE FROM heart_rate_readings
        WHERE timestamp < datetime('now', '-' || :hours || ' hours')
        """
    )
    suspend fun deleteOlderThan(hours: Int): Int

    /**
     * Clear all readings
     */
    @Query("DELETE FROM heart_rate_readings")
    suspend fun deleteAll()
}

/**
 * Row data for statistics query (needed because Room doesn't support data classes with custom constructor in queries)
 */
data class HeartRateStatsRow(
    val minHeartRate: Double,
    val maxHeartRate: Double,
    val averageHeartRate: Double,
    val readingCount: Int,
    val alertCount: Int
)
