
package com.explorova.cardiocareful.data

import android.content.Context
import android.util.Log
import androidx.concurrent.futures.await
import androidx.health.services.client.HealthServices
import androidx.health.services.client.MeasureCallback
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DataTypeAvailability
import androidx.health.services.client.data.DeltaDataType
import androidx.health.services.client.data.SampleDataPoint
import com.explorova.cardiocareful.TAG
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.runBlocking

/**
 * Entry point for [HealthServicesClient] APIs. This also provides suspend functions around
 * those APIs to enable use in coroutines.
 */
class HealthServicesRepository(context: Context) {
    private val healthServicesClient = HealthServices.getClient(context)
    private val measureClient = healthServicesClient.measureClient

    suspend fun hasHeartRateCapability(): Boolean {
        return try {
            val capabilities = measureClient.getCapabilitiesAsync().await()
            val hasCapability = DataType.HEART_RATE_BPM in capabilities.supportedDataTypesMeasure
            Log.d(TAG, "Heart rate capability check: $hasCapability")
            hasCapability
        } catch (e: Exception) {
            Log.e(TAG, "Error checking heart rate capability", e)
            false
        }
    }

    /**
     * Returns a cold flow. When activated, the flow will register a callback for heart rate data
     * and start to emit messages. When the consuming coroutine is cancelled, the measure callback
     * is unregistered.
     *
     * [callbackFlow] is used to bridge between a callback-based API and Kotlin flows.
     */
    fun heartRateMeasureFlow() =
        callbackFlow {
            val callback =
                object : MeasureCallback {
                    override fun onAvailabilityChanged(
                        dataType: DeltaDataType<*, *>,
                        availability: Availability,
                    ) {
                        // Only send back DataTypeAvailability (not LocationAvailability)
                        if (availability is DataTypeAvailability) {
                            Log.d(TAG, "Heart rate availability changed: $availability")
                            trySendBlocking(CardioMessage.CardioAvailability(availability))
                        }
                    }

                    override fun onDataReceived(data: DataPointContainer) {
                        try {
                            val heartRateBpm = data.getData(DataType.HEART_RATE_BPM)
                            if (heartRateBpm.isNotEmpty()) {
                                trySendBlocking(CardioMessage.CardioData(heartRateBpm))
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error processing heart rate data", e)
                        }
                    }
                }

            try {
                Log.d(TAG, "Registering for heart rate data")
                measureClient.registerMeasureCallback(DataType.HEART_RATE_BPM, callback)
                Log.d(TAG, "Successfully registered for heart rate data")
            } catch (e: Exception) {
                Log.e(TAG, "Error registering measure callback", e)
                close(e)
            }

            awaitClose {
                try {
                    Log.d(TAG, "Unregistering for heart rate data")
                    runBlocking {
                        measureClient.unregisterMeasureCallbackAsync(DataType.HEART_RATE_BPM, callback)
                            .await()
                    }
                    Log.d(TAG, "Successfully unregistered for heart rate data")
                } catch (e: Exception) {
                    Log.e(TAG, "Error unregistering measure callback", e)
                }
            }
        }
}

sealed class CardioMessage {
    class CardioAvailability(val availability: DataTypeAvailability) : CardioMessage()

    class CardioData(val data: List<SampleDataPoint<Double>>) : CardioMessage()
}
