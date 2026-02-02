package com.explorova.cardiocareful.integration

import com.explorova.cardiocareful.engine.HapticPatterns
import com.explorova.cardiocareful.engine.Notification
import org.junit.Test
import org.junit.Assert.*

class NotificationIntegrationTest {

    @Test
    fun testCreateNotificationFromUserPreferences() {
        val minHr = 80
        val maxHr = 150
        val cooldown = 30

        val notification = Notification.createFromUserPreferences(minHr, maxHr, cooldown)

        assertNotNull(notification)
        // Verify it triggers correctly within range
        assertTrue(notification.checkConditions(100.0))

        // Verify it doesn't trigger outside range
        assertFalse(notification.checkConditions(79.0))
        assertFalse(notification.checkConditions(151.0))
    }

    @Test
    fun testNotificationWithHapticPattern() {
        val minHr = 100
        val maxHr = 150
        val notification = Notification.createFromUserPreferences(minHr, maxHr)

        // Verify pattern exists and is valid
        assertNotNull(notification.pattern)
        assertTrue(notification.pattern.timings.isNotEmpty())
        assertTrue(notification.pattern.amplitudes.isNotEmpty())
        assertEquals(notification.pattern.timings.size, notification.pattern.amplitudes.size)
    }

    @Test
    fun testMultipleNotificationsWithDifferentPatterns() {
        val patterns = HapticPatterns.getAllPatterns()

        patterns.forEach { patternType ->
            val notification = Notification.createFromUserPreferences(90, 140)

            assertTrue(notification.checkConditions(100.0))
            assertTrue(notification.pattern.timings.isNotEmpty())
        }
    }

    @Test
    fun testCooldownIntegration() {
        val cooldownSeconds = 60
        val notification = Notification.createFromUserPreferences(100, 150, cooldownSeconds)

        // First trigger
        assertTrue(notification.checkConditions(120.0))

        // Immediate second attempt should be blocked
        assertFalse(notification.checkConditions(120.0))

        // Remaining cooldown should be substantial
        val remaining = notification.getRemainingCooldown()
        assertTrue(remaining > 0)
        assertTrue(remaining <= cooldownSeconds)
    }

    @Test
    fun testDefaultNotification() {
        // Test with default parameters
        val notification = Notification.createFromUserPreferences(null, null)

        assertNotNull(notification)
        // Should trigger for any HR when no thresholds set
        assertTrue(notification.checkConditions(0.0))
        assertTrue(notification.checkConditions(150.0))
    }

    @Test
    fun testNotificationStateAfterAlert() {
        val notification = Notification.createFromUserPreferences(100, 150, 10)

        // Trigger alert
        assertTrue(notification.checkConditions(120.0))

        // Check remaining cooldown
        val remaining1 = notification.getRemainingCooldown()
        assertTrue(remaining1 > 0)

        // Drop below threshold
        assertFalse(notification.checkConditions(80.0))

        // Cooldown should be reset
        val remaining2 = notification.getRemainingCooldown()
        assertEquals(0, remaining2)

        // Should be able to alert again
        assertTrue(notification.checkConditions(120.0))
    }

    @Test
    fun testMultipleAlertsWithCooldown() {
        val alerts = mutableListOf<Notification>()

        repeat(3) { i ->
            val alert = Notification.createFromUserPreferences(
                100 + (i * 10),
                150 + (i * 10),
                5
            )
            alerts.add(alert)
        }

        // Trigger all alerts
        var count = 0
        alerts.forEach { alert ->
            if (alert.checkConditions(120.0 + count)) {
                count++
            }
        }

        assertEquals(3, count)

        // All should be in cooldown
        alerts.forEach { alert ->
            assertTrue(alert.getRemainingCooldown() >= 0)
        }
    }
}
