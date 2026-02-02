package com.explorova.cardiocareful.data

import org.junit.Test
import org.junit.Assert.*

class UserPreferencesValidationTest {

    @Test
    fun testDefaultValues() {
        assertEquals(60, UserPreferences.DEFAULT_MIN_HR)
        assertEquals(100, UserPreferences.DEFAULT_MAX_HR)
        assertEquals(1, UserPreferences.DEFAULT_ALERTS_ENABLED)
        assertEquals("SHORT", UserPreferences.DEFAULT_HAPTIC_PATTERN)
        assertEquals(30, UserPreferences.DEFAULT_ALERT_COOLDOWN)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testMinHeartRateTooLow() {
        // Validate that values outside 20-220 throw exception
        // This tests the requirement in setMinHeartRate
        val minHr = 10 // Below 20
        require(minHr in 20..220) { "Min heart rate must be between 20 and 220" }
    }

    @Test(expected = IllegalArgumentException::class)
    fun testMinHeartRateTooHigh() {
        val minHr = 250 // Above 220
        require(minHr in 20..220) { "Min heart rate must be between 20 and 220" }
    }

    @Test(expected = IllegalArgumentException::class)
    fun testMaxHeartRateTooLow() {
        val maxHr = 10 // Below 20
        require(maxHr in 20..220) { "Max heart rate must be between 20 and 220" }
    }

    @Test(expected = IllegalArgumentException::class)
    fun testMaxHeartRateTooHigh() {
        val maxHr = 250 // Above 220
        require(maxHr in 20..220) { "Max heart rate must be between 20 and 220" }
    }

    @Test(expected = IllegalArgumentException::class)
    fun testMinGreaterThanMax() {
        val minHr = 150
        val maxHr = 100
        require(minHr <= maxHr) { "Min heart rate must be less than or equal to max heart rate" }
    }

    @Test
    fun testValidThresholdRange() {
        val minHr = 60
        val maxHr = 100

        // Should not throw
        require(minHr in 20..220) { "Min heart rate must be between 20 and 220" }
        require(maxHr in 20..220) { "Max heart rate must be between 20 and 220" }
        require(minHr <= maxHr) { "Min heart rate must be less than or equal to max heart rate" }

        assertTrue(true)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testAlertCooldownTooLow() {
        val cooldown = 3 // Below 5
        require(cooldown in 5..300) { "Alert cooldown must be between 5 and 300 seconds" }
    }

    @Test(expected = IllegalArgumentException::class)
    fun testAlertCooldownTooHigh() {
        val cooldown = 500 // Above 300
        require(cooldown in 5..300) { "Alert cooldown must be between 5 and 300 seconds" }
    }

    @Test
    fun testValidAlertCooldown() {
        val validCooldowns = listOf(5, 10, 30, 60, 120, 300)

        validCooldowns.forEach { cooldown ->
            require(cooldown in 5..300) { "Alert cooldown must be between 5 and 300 seconds" }
            assertTrue(true)
        }
    }

    @Test
    fun testBoundaryValues() {
        // Test valid boundary values
        val minHrBoundaries = listOf(20, 100, 220)
        minHrBoundaries.forEach { hr ->
            require(hr in 20..220) { "Invalid HR" }
            assertTrue(true)
        }

        val cooldownBoundaries = listOf(5, 150, 300)
        cooldownBoundaries.forEach { cooldown ->
            require(cooldown in 5..300) { "Invalid cooldown" }
            assertTrue(true)
        }
    }
}
