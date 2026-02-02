package com.explorova.cardiocareful.engine

import android.util.Log
import com.explorova.cardiocareful.TAG
import java.time.LocalDateTime

class Notification(
    name: String,
    alertPattern: AlertPattern,
    minRate: Int? = null,
    maxRate: Int? = null,
    repeatIntervalSeconds: Int? = null,
    repeatable: Boolean? = false,
) {
    val pattern: AlertPattern = alertPattern
    private val name = name
    private val minRate = minRate
    private val maxRate = maxRate
    private val repeatIntervalSecs = repeatIntervalSeconds
    private val repeatable = repeatable
    private var becameHot: LocalDateTime? = null

    fun checkConditions(currentHeartRate: Double): Boolean {
        maxRate?.let {
            if (currentHeartRate > it) {
                return cold()
            }
        }
        minRate?.let {
            if (currentHeartRate < it) {
                return cold()
            }
        }

        var result = hot()
        repeatIntervalSecs?.let {
            val repeatNoSoonerThanSeconds = it
            becameHot?.let {
                val now = LocalDateTime.now()
                val whenItsOK = it.plusSeconds(repeatNoSoonerThanSeconds.toLong())
                val isTooSoon: Boolean = now.isBefore(whenItsOK)
                if (isTooSoon) {
                    Log.d(TAG, "TOO SOON: Must wait until $whenItsOK before re-firing notification")
                    result = false
                }
            }
        }

        return result
    }

    private fun cold(): Boolean {
        becameHot = null
        return false
    }

    private fun hot(): Boolean {
        if (becameHot == null) {
            becameHot = LocalDateTime.now()
        }
        return true
    }

    override fun toString(): String {
        return "Notification(Name='$name', MinRate=$minRate, MaxRate=$maxRate, MinDuration=$repeatIntervalSecs, becameHot=$becameHot)"
    }

    companion object DemoData {
        private fun getSingler(): Notification {
            val timings: LongArray = longArrayOf(50, 50, 50, 50, 50, 100, 350, 25, 25, 25, 25, 200)
            val amplitudes: IntArray = intArrayOf(33, 51, 75, 113, 170, 255, 0, 38, 62, 100, 160, 255)
            val repeatIndex = -1 // Do not repeat.
            val pattern = AlertPattern(timings, amplitudes, repeatIndex)
            return Notification("Single", pattern, minRate = 60, maxRate = 70)
        }

        private fun getRepeater(): Notification {
            val timings: LongArray = longArrayOf(50, 50, 100, 50, 50)
            val amplitudes: IntArray = intArrayOf(64, 128, 255, 128, 64)
            val repeatIndex = 1 // Repeat from the second entry, index = 1.
            val pattern = AlertPattern(timings, amplitudes, repeatIndex)
            return Notification("Dual", pattern, minRate = 130)
        }

        fun getSampleData(): MutableList<Notification> {
            return mutableListOf(getRepeater(), getSingler())
        }

        /**
         * Create a notification from user-configured thresholds
         */
        fun createFromUserPreferences(minHr: Int?, maxHr: Int?): Notification {
            val timings: LongArray = longArrayOf(50, 50, 100, 50, 50)
            val amplitudes: IntArray = intArrayOf(64, 128, 255, 128, 64)
            val pattern = AlertPattern(timings, amplitudes, repeatIndex = 1)
            return Notification(
                "User Alert",
                pattern,
                minRate = minHr,
                maxRate = maxHr,
                repeatIntervalSeconds = 30
            )
        }
    }
}
