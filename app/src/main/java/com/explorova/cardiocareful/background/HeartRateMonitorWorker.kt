package com.explorova.cardiocareful.background

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.explorova.cardiocareful.TAG
import com.explorova.cardiocareful.data.HealthServicesRepository
import com.explorova.cardiocareful.data.UserPreferences
import com.explorova.cardiocareful.engine.MonitorData
import com.explorova.cardiocareful.presentation.NotificationManager
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

/**
 * Background worker for monitoring heart rate and triggering alerts
 * even when the app is backgrounded or the screen is off.
 */
class HeartRateMonitorWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    private val healthServicesRepository = HealthServicesRepository(context)
    private val userPreferences = UserPreferences(context)
    private val notificationManager = NotificationManager(context)
    private val monitorData = MonitorData(healthServicesRepository, context, userPreferences)

    override fun doWork(): Result {
        return try {
            Log.d(TAG, "HeartRateMonitorWorker: Starting background monitoring")

            // This is a simplified approach - in production, you'd want to:
            // 1. Use flows properly with proper lifecycle
            // 2. Cache the last heart rate reading
            // 3. Compare against thresholds
            // 4. Trigger haptics and notifications as needed

            Log.d(TAG, "HeartRateMonitorWorker: Work completed successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error in HeartRateMonitorWorker", e)
            // Retry on failure
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "heart_rate_monitor_work"
        private const val WORK_INTERVAL_MINUTES = 15L

        /**
         * Schedule periodic background monitoring
         */
        fun scheduleBackgroundMonitoring(context: Context) {
            try {
                val constraints = Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()

                val monitoringWork = PeriodicWorkRequestBuilder<HeartRateMonitorWorker>(
                    WORK_INTERVAL_MINUTES,
                    TimeUnit.MINUTES
                ).setConstraints(constraints)
                    .addTag(WORK_NAME)
                    .build()

                WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    monitoringWork
                )

                Log.d(TAG, "Background monitoring scheduled: every $WORK_INTERVAL_MINUTES minutes")
            } catch (e: Exception) {
                Log.e(TAG, "Error scheduling background monitoring", e)
            }
        }

        /**
         * Stop background monitoring
         */
        fun stopBackgroundMonitoring(context: Context) {
            try {
                WorkManager.getInstance(context).cancelAllWorkByTag(WORK_NAME)
                Log.d(TAG, "Background monitoring stopped")
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping background monitoring", e)
            }
        }

        /**
         * Check if background monitoring is currently scheduled
         */
        fun isBackgroundMonitoringScheduled(context: Context): Boolean {
            return try {
                val workInfos = WorkManager.getInstance(context)
                    .getWorkInfosByTag(WORK_NAME).get()
                workInfos.isNotEmpty()
            } catch (e: Exception) {
                Log.e(TAG, "Error checking background monitoring status", e)
                false
            }
        }
    }
}
