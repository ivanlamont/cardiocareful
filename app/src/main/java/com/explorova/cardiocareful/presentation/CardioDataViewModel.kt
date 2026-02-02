package com.explorova.cardiocareful.presentation

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.health.services.client.data.DataTypeAvailability
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.explorova.cardiocareful.TAG
import com.explorova.cardiocareful.data.CardioMessage
import com.explorova.cardiocareful.data.HealthServicesRepository
import com.explorova.cardiocareful.data.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

class CardioDataViewModel(
    private val healthServicesRepository: HealthServicesRepository,
    private val userPreferences: UserPreferences? = null,
) : ViewModel() {
    val enabled: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val hr: MutableState<Double> = mutableStateOf(0.0)
    val availability: MutableState<DataTypeAvailability> =
        mutableStateOf(DataTypeAvailability.UNKNOWN)

    val uiState: MutableState<UiState> = mutableStateOf(UiState.Startup)

    // Settings state
    val showSettings: MutableState<Boolean> = mutableStateOf(false)
    val minHeartRate: MutableState<Int> = mutableStateOf(UserPreferences.DEFAULT_MIN_HR)
    val maxHeartRate: MutableState<Int> = mutableStateOf(UserPreferences.DEFAULT_MAX_HR)
    val alertsEnabled: MutableState<Boolean> = mutableStateOf(true)

    init {
        viewModelScope.launch {
            val supported = healthServicesRepository.hasHeartRateCapability()
            uiState.value =
                if (supported) {
                    UiState.Supported
                } else {
                    UiState.NotSupported
                }
        }

        // Load user preferences if available
        userPreferences?.let {
            viewModelScope.launch {
                try {
                    it.minHeartRateFlow.collectLatest { value ->
                        minHeartRate.value = value
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error loading min heart rate", e)
                }
            }
            viewModelScope.launch {
                try {
                    it.maxHeartRateFlow.collectLatest { value ->
                        maxHeartRate.value = value
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error loading max heart rate", e)
                }
            }
            viewModelScope.launch {
                try {
                    it.alertsEnabledFlow.collectLatest { value ->
                        alertsEnabled.value = value
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error loading alerts enabled", e)
                }
            }
        }

        viewModelScope.launch {
            enabled.collect {
                if (it) {
                    try {
                        healthServicesRepository.heartRateMeasureFlow()
                            .takeWhile { enabled.value }
                            .collect { cardioDataMessage ->
                                when (cardioDataMessage) {
                                    is CardioMessage.CardioData -> {
                                        if (cardioDataMessage.data.isNotEmpty()) {
                                            hr.value = cardioDataMessage.data.last().value
                                        } else {
                                            Log.w(TAG, "Received empty heart rate data")
                                        }
                                    }
                                    is CardioMessage.CardioAvailability -> {
                                        availability.value = cardioDataMessage.availability
                                        Log.d(TAG, "Availability updated: ${cardioDataMessage.availability}")
                                    }
                                }
                            }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error collecting heart rate data", e)
                        enabled.value = false
                    }
                }
            }
        }
    }

    fun toggleEnabled() {
        enabled.value = !enabled.value
        if (!enabled.value) {
            availability.value = DataTypeAvailability.UNKNOWN
        }
    }

    fun toggleShowSettings() {
        showSettings.value = !showSettings.value
    }

    fun saveSettings(minHr: Int, maxHr: Int, alertsEnabledValue: Boolean) {
        userPreferences?.let {
            viewModelScope.launch {
                try {
                    it.setThresholds(minHr, maxHr)
                    it.setAlertsEnabled(alertsEnabledValue)
                    showSettings.value = false
                    Log.d(TAG, "Settings saved successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Error saving settings", e)
                }
            }
        } ?: run {
            // No preferences available, just update the state
            minHeartRate.value = minHr
            maxHeartRate.value = maxHr
            alertsEnabled.value = alertsEnabledValue
            showSettings.value = false
        }
    }
}

class CardioDataViewModelFactory(
    private val healthServicesRepository: HealthServicesRepository,
    private val userPreferences: UserPreferences? = null,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardioDataViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CardioDataViewModel(
                healthServicesRepository = healthServicesRepository,
                userPreferences = userPreferences,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

sealed class UiState {
    object Startup : UiState()

    object NotSupported : UiState()

    object Supported : UiState()
}
