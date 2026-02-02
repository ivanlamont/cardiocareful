package com.explorova.cardiocareful.engine

import org.junit.Test
import org.junit.Assert.*

class HapticPatternsTest {

    @Test
    fun testAllPatternsAvailable() {
        val patterns = HapticPatterns.getAllPatterns()

        assertTrue(patterns.isNotEmpty())
        assertTrue(patterns.size >= 3) // At least 3 patterns as per requirements
    }

    @Test
    fun testPatternNames() {
        val patterns = HapticPatterns.getAllPatterns()
        val names = patterns.map { it.name }

        assertTrue(names.contains("SHORT"))
        assertTrue(names.contains("LONG"))
        assertTrue(names.contains("PULSE"))
    }

    @Test
    fun testPatternDisplayNames() {
        val patterns = HapticPatterns.getAllPatterns()

        patterns.forEach { pattern ->
            assertTrue(pattern.displayName.isNotEmpty())
            assertNotEquals(pattern.name, pattern.displayName) // Should have readable display name
        }
    }

    @Test
    fun testGetPatternByName() {
        val shortPattern = HapticPatterns.getPatternByName("SHORT")
        assertNotNull(shortPattern)

        val longPattern = HapticPatterns.getPatternByName("LONG")
        assertNotNull(longPattern)
    }

    @Test
    fun testGetPatternByNameInvalid() {
        // Should return default (SHORT) for invalid name
        val pattern = HapticPatterns.getPatternByName("INVALID_PATTERN")
        assertNotNull(pattern)

        // Should match SHORT pattern
        val shortPattern = HapticPatterns.getPatternByName("SHORT")
        assertEquals(shortPattern.timings.toList(), pattern.timings.toList())
    }

    @Test
    fun testGetPatternByNameNull() {
        // Should return default (SHORT) for null
        val pattern = HapticPatterns.getPatternByName(null)
        assertNotNull(pattern)

        val shortPattern = HapticPatterns.getPatternByName("SHORT")
        assertEquals(shortPattern.timings.toList(), pattern.timings.toList())
    }

    @Test
    fun testPatternValidation() {
        val patterns = HapticPatterns.getAllPatterns()

        patterns.forEach { pattern ->
            // All patterns should have timings and amplitudes
            assertTrue(pattern.pattern.timings.isNotEmpty())
            assertTrue(pattern.pattern.amplitudes.isNotEmpty())

            // Sizes should match
            assertEquals(
                pattern.pattern.timings.size,
                pattern.pattern.amplitudes.size
            )

            // All amplitudes should be valid
            assertTrue(pattern.pattern.amplitudes.all { it in 0..255 })
        }
    }

    @Test
    fun testShortPatternHasValidTimings() {
        val shortPattern = HapticPatterns.getPatternByName("SHORT")

        assertTrue(shortPattern.timings.isNotEmpty())
        assertTrue(shortPattern.timings.all { it > 0 })
    }

    @Test
    fun testPulsePatternAlternatesOnOff() {
        val pulsePattern = HapticPatterns.getPatternByName("PULSE")

        // Pulse pattern should alternate between on (>0) and off (0)
        assertTrue(pulsePattern.amplitudes.size >= 2)

        // Check alternation
        for (i in 0 until pulsePattern.amplitudes.size - 1) {
            val current = pulsePattern.amplitudes[i] > 0
            val next = pulsePattern.amplitudes[i + 1] > 0
            // Should alternate (not always the same)
            if (i > 0) {
                assertNotEquals(current, next)
            }
        }
    }
}
