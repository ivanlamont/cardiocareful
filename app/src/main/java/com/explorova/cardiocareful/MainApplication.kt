package com.explorova.cardiocareful

import android.app.Application
import android.util.Log
import com.explorova.cardiocareful.background.HeartRateMonitorWorker
import com.explorova.cardiocareful.data.HealthServicesRepository

const val TAG = "Cardio Careful"
const val PERMISSION = android.Manifest.permission.BODY_SENSORS

class MainApplication : Application() {
    val healthServicesRepository by lazy { HealthServicesRepository(this) }

    override fun onCreate() {
        super.onCreate()

        // Schedule background monitoring on app start
        try {
            if (!HeartRateMonitorWorker.isBackgroundMonitoringScheduled(this)) {
                HeartRateMonitorWorker.scheduleBackgroundMonitoring(this)
                Log.d(TAG, "Background monitoring scheduled on app startup")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error scheduling background monitoring", e)
        }
    }
}
