package com.explorova.cardiocareful.engine

import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDateTime

class NotificationTest {

    private val testPattern = AlertPattern(
        timings = longArrayOf(50, 50),
        amplitudes = intArrayOf(100, 0),
        repeatIndex = -1
    )

    @Test
    fun testNotificationWithoutThresholds() {
        val notification = Notification(
            name = "Test",
            alertPattern = testPattern,
            minRate = null,
            maxRate = null
        )

        // Should trigger for any heart rate
        assertTrue(notification.checkConditions(60.0))
        assertTrue(notification.checkConditions(0.0))
        assertTrue(notification.checkConditions(220.0))
    }

    @Test
    fun testNotificationWithMinThreshold() {
        val notification = Notification(
            name = "Test",
            alertPattern = testPattern,
            minRate = 100
        )

        assertTrue(notification.checkConditions(100.0))
        assertTrue(notification.checkConditions(150.0))
        assertFalse(notification.checkConditions(99.0))
    }

    @Test
    fun testNotificationWithMaxThreshold() {
        val notification = Notification(
            name = "Test",
            alertPattern = testPattern,
            maxRate = 120
        )

        assertTrue(notification.checkConditions(120.0))
        assertTrue(notification.checkConditions(80.0))
        assertFalse(notification.checkConditions(121.0))
    }

    @Test
    fun testNotificationWithBothThresholds() {
        val notification = Notification(
            name = "Test",
            alertPattern = testPattern,
            minRate = 100,
            maxRate = 150
        )

        assertTrue(notification.checkConditions(100.0))
        assertTrue(notification.checkConditions(125.0))
        assertTrue(notification.checkConditions(150.0))
        assertFalse(notification.checkConditions(99.0))
        assertFalse(notification.checkConditions(151.0))
    }

    @Test
    fun testCooldownPreventsRepetitiveAlerts() {
        val notification = Notification(
            name = "Test",
            alertPattern = testPattern,
            minRate = 100,
            repeatIntervalSeconds = 5
        )

        // First trigger should succeed
        assertTrue(notification.checkConditions(110.0))

        // Immediate second trigger should be blocked by cooldown
        assertFalse(notification.checkConditions(110.0))
    }

    @Test
    fun testCooldownResetOnBelowThreshold() {
        val notification = Notification(
            name = "Test",
            alertPattern = testPattern,
            minRate = 100,
            repeatIntervalSeconds = 5
        )

        // Trigger alert
        assertTrue(notification.checkConditions(110.0))

        // Drop below threshold (resets cooldown)
        assertFalse(notification.checkConditions(90.0))

        // Should be able to trigger again after dropping below threshold
        assertTrue(notification.checkConditions(110.0))
    }

    @Test
    fun testGetRemainingCooldown() {
        val notification = Notification(
            name = "Test",
            alertPattern = testPattern,
            minRate = 100,
            repeatIntervalSeconds = 10
        )

        // No cooldown initially
        assertEquals(0, notification.getRemainingCooldown())

        // Trigger alert to start cooldown
        notification.checkConditions(110.0)

        // Should have remaining cooldown
        val remaining = notification.getRemainingCooldown()
        assertTrue(remaining > 0)
        assertTrue(remaining <= 10)
    }

    @Test
    fun testNoCooldownWhenNotSpecified() {
        val notification = Notification(
            name = "Test",
            alertPattern = testPattern,
            minRate = 100,
            repeatIntervalSeconds = null
        )

        // First trigger
        assertTrue(notification.checkConditions(110.0))

        // Should trigger again immediately without cooldown
        assertTrue(notification.checkConditions(110.0))
    }

    @Test
    fun testToStringMethod() {
        val notification = Notification(
            name = "TestAlert",
            alertPattern = testPattern,
            minRate = 100,
            maxRate = 150,
            repeatIntervalSeconds = 30
        )

        val str = notification.toString()
        assertTrue(str.contains("TestAlert"))
        assertTrue(str.contains("100"))
        assertTrue(str.contains("150"))
        assertTrue(str.contains("30"))
    }
}
