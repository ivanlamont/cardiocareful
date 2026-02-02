package com.explorova.cardiocareful.engine

import org.junit.Test
import org.junit.Assert.*

class AlertPatternTest {

    @Test
    fun testValidAlertPattern() {
        val timings = longArrayOf(50, 100, 50)
        val amplitudes = intArrayOf(100, 200, 150)
        val pattern = AlertPattern(timings, amplitudes, repeatIndex = -1)

        assertEquals(3, pattern.timings.size)
        assertEquals(3, pattern.amplitudes.size)
        assertEquals(-1, pattern.repeatIndex)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testMismatchedTimingsAndAmplitudes() {
        val timings = longArrayOf(50, 100, 50)
        val amplitudes = intArrayOf(100, 200) // Different size
        AlertPattern(timings, amplitudes, repeatIndex = -1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInvalidAmplitudeValueTooHigh() {
        val timings = longArrayOf(50, 100)
        val amplitudes = intArrayOf(100, 256) // Invalid: > 255
        AlertPattern(timings, amplitudes, repeatIndex = -1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInvalidAmplitudeValueNegative() {
        val timings = longArrayOf(50, 100)
        val amplitudes = intArrayOf(-1, 100) // Invalid: < 0
        AlertPattern(timings, amplitudes, repeatIndex = -1)
    }

    @Test
    fun testValidAmplitudeRange() {
        val timings = longArrayOf(50, 100, 200)
        val amplitudes = intArrayOf(0, 128, 255) // Valid range
        val pattern = AlertPattern(timings, amplitudes, repeatIndex = 1)

        assertTrue(pattern.amplitudes.all { it in 0..255 })
    }

    @Test
    fun testRepeatIndexCanBeNegative() {
        val timings = longArrayOf(50)
        val amplitudes = intArrayOf(100)
        val pattern = AlertPattern(timings, amplitudes, repeatIndex = -1)

        assertEquals(-1, pattern.repeatIndex)
    }

    @Test
    fun testRepeatIndexCanBePositive() {
        val timings = longArrayOf(50, 100, 150)
        val amplitudes = intArrayOf(100, 150, 200)
        val pattern = AlertPattern(timings, amplitudes, repeatIndex = 1)

        assertEquals(1, pattern.repeatIndex)
    }
}
