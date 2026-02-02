package com.explorova.cardiocareful.presentation

import android.app.NotificationChannel
import android.app.NotificationManager as AndroidNotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.explorova.cardiocareful.R
import com.explorova.cardiocareful.TAG

/**
 * Manages system notifications for heart rate alerts.
 */
class NotificationManager(private val context: Context) {

    companion object {
        private const val ALERT_CHANNEL_ID = "cardio_alerts"
        private const val ALERT_NOTIFICATION_ID = 1001
        private const val ALERT_CHANNEL_NAME = "Heart Rate Alerts"
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as AndroidNotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val importance = AndroidNotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(
            ALERT_CHANNEL_ID,
            ALERT_CHANNEL_NAME,
            importance
        ).apply {
            description = "Notifications for heart rate alerts"
        }
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * Show a heart rate alert notification.
     * @param heartRate The current heart rate in BPM
     * @param alertType The type of alert (e.g., "High", "Low", "Target Zone")
     */
    fun showHeartRateAlert(heartRate: Double, alertType: String = "Alert") {
        try {
            val notification = NotificationCompat.Builder(context, ALERT_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText("$alertType: $heartRate bpm")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .build()

            notificationManager.notify(ALERT_NOTIFICATION_ID, notification)
            Log.d(TAG, "Notification shown: $alertType - $heartRate bpm")
        } catch (e: Exception) {
            Log.e(TAG, "Error showing notification", e)
        }
    }

    /**
     * Show a status notification with current heart rate.
     * @param heartRate The current heart rate in BPM
     * @param status The status message
     */
    fun showStatusNotification(heartRate: Double, status: String) {
        try {
            val notification = NotificationCompat.Builder(context, ALERT_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Heart Rate Monitor")
                .setContentText("$heartRate bpm - $status")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .build()

            notificationManager.notify(ALERT_NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            Log.e(TAG, "Error showing status notification", e)
        }
    }

    /**
     * Dismiss any active notification.
     */
    fun dismissNotification() {
        try {
            notificationManager.cancel(ALERT_NOTIFICATION_ID)
            Log.d(TAG, "Notification dismissed")
        } catch (e: Exception) {
            Log.e(TAG, "Error dismissing notification", e)
        }
    }

    /**
     * Show a cooldown notification when alert is in cooldown period.
     * @param remainingSeconds Time remaining in cooldown period
     */
    fun showCooldownNotification(remainingSeconds: Int) {
        try {
            val notification = NotificationCompat.Builder(context, ALERT_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Alert Cooling Down")
                .setContentText("Next alert in $remainingSeconds seconds")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setProgress(100, (remainingSeconds % 100), false)
                .build()

            notificationManager.notify(ALERT_NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            Log.e(TAG, "Error showing cooldown notification", e)
        }
    }
}
