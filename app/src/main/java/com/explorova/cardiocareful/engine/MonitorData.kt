package com.explorova.cardiocareful.engine

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.explorova.cardiocareful.TAG
import com.explorova.cardiocareful.data.CardioMessage
import com.explorova.cardiocareful.data.HealthServicesRepository
import com.explorova.cardiocareful.data.UserPreferences
import com.explorova.cardiocareful.presentation.Haptics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

class MonitorData(
    healthServicesRepository: HealthServicesRepository,
    context: android.content.Context,
    private val userPreferences: UserPreferences? = null,
) {
    private val notifications: MutableList<Notification> = loadNotifications()
    private val heartrateBpm: MutableState<Double> = mutableStateOf(0.0)
    private val haptics: Haptics = Haptics(context)

    init {
        val enabled: MutableStateFlow<Boolean> = MutableStateFlow(true)
        val scope = kotlinx.coroutines.MainScope()

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
            } else {
                Log.d(TAG, "condition not fired")
            }
        }
    }
}
