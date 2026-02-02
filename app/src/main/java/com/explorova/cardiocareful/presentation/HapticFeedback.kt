package com.explorova.cardiocareful.presentation

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.explorova.cardiocareful.TAG
import kotlinx.coroutines.flow.collectLatest

/**
 * Provides haptic feedback utilities for user interactions.
 */
class HapticFeedbackProvider(private val context: Context) {

    private val vibrator = context.getSystemService(Vibrator::class.java)

    /**
     * Light click feedback (short, subtle vibration)
     */
    fun lightClick() {
        vibrate(duration = 10, amplitude = 50)
    }

    /**
     * Standard click feedback
     */
    fun click() {
        vibrate(duration = 20, amplitude = 100)
    }

    /**
     * Strong feedback for important actions
     */
    fun strongFeedback() {
        vibrate(duration = 50, amplitude = 200)
    }

    /**
     * Double tap feedback
     */
    fun doubleTap() {
        vibrate(duration = 30, amplitude = 150)
        java.lang.Thread.sleep(50)
        vibrate(duration = 30, amplitude = 150)
    }

    /**
     * Success feedback (ascending pattern)
     */
    fun success() {
        val timings = longArrayOf(0, 50, 100, 50)
        val amplitudes = intArrayOf(0, 150, 200, 255)
        vibratePattern(timings, amplitudes, -1)
    }

    /**
     * Error feedback (descending pattern)
     */
    fun error() {
        val timings = longArrayOf(0, 50, 100, 50)
        val amplitudes = intArrayOf(0, 255, 200, 100)
        vibratePattern(timings, amplitudes, -1)
    }

    /**
     * Generic vibration with duration and amplitude
     */
    private fun vibrate(duration: Long, amplitude: Int) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createOneShot(
                    duration,
                    if (amplitude in 1..255) amplitude else VibrationEffect.DEFAULT_AMPLITUDE
                )
                vibrator?.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(duration)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error providing haptic feedback", e)
        }
    }

    /**
     * Vibration pattern
     */
    private fun vibratePattern(timings: LongArray, amplitudes: IntArray, repeat: Int) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(timings, amplitudes, repeat)
                vibrator?.vibrate(effect)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error providing haptic pattern", e)
        }
    }
}

/**
 * Modifier to add haptic feedback on button press.
 */
fun Modifier.hapticClickable(context: Context): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val haptics = remember { HapticFeedbackProvider(context) }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collectLatest { interaction ->
            when (interaction) {
                is PressInteraction.Press -> haptics.click()
            }
        }
    }

    this
}

/**
 * Create a remembered HapticFeedbackProvider.
 */
@Composable
fun rememberHapticFeedback(context: Context): HapticFeedbackProvider {
    return remember { HapticFeedbackProvider(context) }
}
