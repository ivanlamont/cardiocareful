package com.explorova.cardiocareful.presentation

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.health.services.client.data.DataTypeAvailability
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.explorova.cardiocareful.TAG
import com.explorova.cardiocareful.data.HealthServicesRepository
import com.explorova.cardiocareful.data.CardioMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

class CardioDataViewModel(
    private val healthServicesRepository: HealthServicesRepository
) : ViewModel() {
    val enabled: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val hr: MutableState<Double> = mutableStateOf(0.0)
    val availability: MutableState<DataTypeAvailability> =
        mutableStateOf(DataTypeAvailability.UNKNOWN)

    val uiState: MutableState<UiState> = mutableStateOf(UiState.Startup)

    init {
        viewModelScope.launch {
            val supported = healthServicesRepository.hasHeartRateCapability()
            uiState.value = if (supported) {
                UiState.Supported
            } else {
                UiState.NotSupported
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
                                        Log.d(TAG, "Availability updated: ${cardioDataMessage.availability.availability}")
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
}

class CardioDataViewModelFactory(
    private val healthServicesRepository: HealthServicesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardioDataViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CardioDataViewModel(
                healthServicesRepository = healthServicesRepository
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
