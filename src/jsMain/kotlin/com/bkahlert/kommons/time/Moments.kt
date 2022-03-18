package com.bkahlert.kommons.time

import kotlin.js.Date
import kotlin.time.Duration
import kotlin.time.DurationUnit.DAYS
import kotlin.time.DurationUnit.HOURS
import kotlin.time.DurationUnit.MINUTES
import kotlin.time.DurationUnit.SECONDS

/**
 * Attempts to describe this duration like a human being would do,
 * e.g. "5m ago" instead of "367.232723s".
 *
 * Set [comparative] to `false` to disable the use of "ago" and "in".
 */
fun Duration.toMoment(comparative: Boolean = true): String {
    val format: String.() -> String = if (comparative) {
        if (this@toMoment > Duration.ZERO) {
            { "$this ago" }
        } else {
            { "in $this" }
        }
    } else {
        { this }
    }
    val abs = absoluteValue
    return when {
        abs < .5.seconds -> "now"
        abs < 1.minutes -> abs.toString(SECONDS).format()
        abs < 1.hours -> (abs + 29.seconds).toString(MINUTES).format()
        abs < 6.hours -> buildString {
            val durationInHours = abs.inWholeHours.hours
            append(durationInHours.toString(HOURS))
            append(" ")
            append((abs - durationInHours).toString(MINUTES))
        }.format()
        abs < 1.days -> (abs + 29.minutes).toString(HOURS).format()
        abs < 6.days -> buildString {
            val durationInDays = abs.inWholeDays.days
            append(durationInDays.toString(DAYS))
            append(" ")
            append((abs - durationInDays).toString(HOURS))
        }
        else -> (abs + 11.hours).toString(DAYS).format()
    }
}

/**
 * Attempts to describe this date like a human being would do,
 * e.g. "28 days ago" instead of "367.232723s".
 *
 * Set [comparative] to `false` to disable the use of "ago" and "in".
 */
fun Date.toMoment(comparative: Boolean = true): String {
    val now = Now
    val diff = now - this
    return when {
        diff.absoluteValue < 30.days -> diff.toMoment(comparative)
        isSameDate(now) -> toLocaleTimeString("de-DE")
        else -> toLocaleDateString("de-DE")
    }
}
