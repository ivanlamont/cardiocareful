package com.explorova.cardiocareful.engine

/**
 * Defines predefined haptic vibration patterns for alert notifications.
 */
object HapticPatterns {

    enum class PatternType(val displayName: String, val pattern: AlertPattern) {
        SHORT(
            "Short Tap",
            AlertPattern(
                timings = longArrayOf(50, 50),
                amplitudes = intArrayOf(200, 0),
                repeatIndex = -1
            )
        ),
        LONG(
            "Long Vibration",
            AlertPattern(
                timings = longArrayOf(200, 100),
                amplitudes = intArrayOf(255, 0),
                repeatIndex = -1
            )
        ),
        PULSE(
            "Pulse",
            AlertPattern(
                timings = longArrayOf(50, 50, 50, 50),
                amplitudes = intArrayOf(255, 0, 255, 0),
                repeatIndex = -1
            )
        ),
        DOUBLE_TAP(
            "Double Tap",
            AlertPattern(
                timings = longArrayOf(50, 100, 50, 100),
                amplitudes = intArrayOf(200, 0, 200, 0),
                repeatIndex = -1
            )
        ),
        WAVE(
            "Wave",
            AlertPattern(
                timings = longArrayOf(50, 50, 50, 50, 50, 100, 350, 25, 25, 25, 25, 200),
                amplitudes = intArrayOf(33, 51, 75, 113, 170, 255, 0, 38, 62, 100, 160, 255),
                repeatIndex = -1
            )
        ),
        EMERGENCY(
            "Emergency",
            AlertPattern(
                timings = longArrayOf(50, 50, 100, 50, 50),
                amplitudes = intArrayOf(64, 128, 255, 128, 64),
                repeatIndex = 1
            )
        ),
    }

    fun getPatternByName(name: String?): AlertPattern {
        return PatternType.values().find { it.name == name }?.pattern ?: PatternType.SHORT.pattern
    }

    fun getAllPatterns(): List<PatternType> = PatternType.values().toList()
}
