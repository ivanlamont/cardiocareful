package com.explorova.cardiocareful.engine

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.explorova.cardiocareful.TAG
import com.explorova.cardiocareful.data.CardioMessage
import com.explorova.cardiocareful.data.HealthServicesRepository
import com.explorova.cardiocareful.data.UserPreferences
import com.explorova.cardiocareful.presentation.Haptics
import com.explorova.cardiocareful.presentation.NotificationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

class MonitorData(
    healthServicesRepository: HealthServicesRepository,
    context: android.content.Context,
    private val userPreferences: UserPreferences? = null,
) {
    private var notifications: MutableList<Notification> = loadNotifications()
    private val heartrateBpm: MutableState<Double> = mutableStateOf(0.0)
    private val haptics: Haptics = Haptics(context)
    private val notificationManager: NotificationManager = NotificationManager(context)
    private val scope = kotlinx.coroutines.MainScope()
    private var notificationsEnabled = true
    private var showStatusNotifications = false

    init {
        val enabled: MutableStateFlow<Boolean> = MutableStateFlow(true)

        // Watch for user preference changes and update notifications
        userPreferences?.let {
            scope.launch {
                try {
                    combine(
                        it.minHeartRateFlow,
                        it.maxHeartRateFlow,
                        it.alertCooldownFlow
                    ) { minHr, maxHr, cooldown ->
                        Triple(minHr, maxHr, cooldown)
                    }.collect { (minHr, maxHr, cooldown) ->
                        Log.d(TAG, "Preferences changed: minHr=$minHr, maxHr=$maxHr, cooldown=$cooldown")
                        // Update notifications with new preferences
                        notifications = mutableListOf(
                            Notification.createFromUserPreferences(minHr, maxHr, cooldown)
                        )
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error watching preferences", e)
                }
            }
            scope.launch {
                try {
                    combine(
                        it.notificationsEnabledFlow,
                        it.showStatusNotificationsFlow
                    ) { notifEnabled, statusNotif ->
                        Pair(notifEnabled, statusNotif)
                    }.collect { (notifEnabled, statusNotif) ->
                        notificationsEnabled = notifEnabled
                        showStatusNotifications = statusNotif
                        Log.d(TAG, "Notification prefs changed: enabled=$notifEnabled, status=$statusNotif")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error watching notification preferences", e)
                }
            }
        }

        scope.launch {
            try {
                enabled.collect {
                    if (it) {
                        try {
                            healthServicesRepository.heartRateMeasureFlow()
                                .takeWhile { enabled.value }
                                .collect { cardioDataMessage ->
                                    when (cardioDataMessage) {
                                        is CardioMessage.CardioData -> {
                                            if (cardioDataMessage.data.isNotEmpty()) {
                                                heartrateBpm.value = cardioDataMessage.data.last().value
                                                checkData(notifications, heartrateBpm.value)
                                            }
                                        }
                                        is CardioMessage.CardioAvailability -> {
                                            Log.d(TAG, "Heart rate availability: ${cardioDataMessage.availability}")
                                        }
                                    }
                                }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error in heart rate measurement flow", e)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in monitoring initialization", e)
            }
        }
    }

    fun loadNotifications(): MutableList<Notification> {
        // If user preferences are available, they should be used with flows for real-time updates
        // For now, return sample data as fallback
        return Notification.getSampleData()
    }

    fun checkData(
        notifications: MutableList<Notification>,
        currentHeartRate: Double,
    ) {
        Log.d(TAG, "heart rate $currentHeartRate")
        for (item in notifications) {
            Log.d(TAG, "checking condition $item")
            if (item.checkConditions(currentHeartRate)) {
                haptics.sendVibration(item.pattern)

                // Send notification if enabled
                if (notificationsEnabled) {
                    val alertType = when {
                        item.pattern.amplitudes.maxOrNull() ?: 0 > 200 -> "High Alert"
                        item.pattern.amplitudes.maxOrNull() ?: 0 > 100 -> "Medium Alert"
                        else -> "Alert"
                    }
                    notificationManager.showHeartRateAlert(currentHeartRate, alertType)
                }
            } else {
                Log.d(TAG, "condition not fired")
            }
        }

        // Show status notification if enabled
        if (showStatusNotifications) {
            val status = when {
                currentHeartRate < 60 -> "Low"
                currentHeartRate < 100 -> "Normal"
                currentHeartRate < 140 -> "Elevated"
                else -> "High"
            }
            notificationManager.showStatusNotification(currentHeartRate, status)
        }
    }
}
