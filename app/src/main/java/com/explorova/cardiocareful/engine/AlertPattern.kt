package com.explorova.cardiocareful.engine

/**
 * Defines a haptic vibration pattern with timing and amplitude values.
 *
 * @param timings Array of durations in milliseconds for each vibration step
 * @param amplitudes Array of intensity values (0-255) for each step. Must match timings length.
 * @param repeatIndex Index from which to repeat the pattern (-1 means no repeat)
 * @throws IllegalArgumentException if timings and amplitudes arrays have different lengths
 * @throws IllegalArgumentException if any amplitude value is outside 0-255 range
 */
class AlertPattern(var timings: LongArray, var amplitudes: IntArray, var repeatIndex: Int) {
    init {
        require(timings.size == amplitudes.size) {
            "Timings (${timings.size}) and amplitudes (${amplitudes.size}) must have the same length"
        }
        require(amplitudes.all { it in 0..255 }) {
            "All amplitude values must be between 0 and 255"
        }
    }
}