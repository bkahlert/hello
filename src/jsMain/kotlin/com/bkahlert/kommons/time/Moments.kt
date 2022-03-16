package com.bkahlert.kommons.time

import kotlin.js.Date
import kotlin.time.Duration
import kotlin.time.DurationUnit.DAYS
import kotlin.time.DurationUnit.HOURS
import kotlin.time.DurationUnit.MINUTES
import kotlin.time.DurationUnit.SECONDS

fun Duration.toMoment(): String {
    val format: String.() -> String = if (this > Duration.ZERO) {
        { "$this ago" }
    } else {
        { "in $this" }
    }
    val abs = absoluteValue
    return when {
        abs < .5.seconds -> "now"
        abs < 1.minutes -> abs.toString(SECONDS).format()
        abs < 30.minutes -> abs.toString(MINUTES).format()
        abs < 6.hours -> buildString {
            append(abs.toString(HOURS))
            append(" ")
            append((abs - (abs.inWholeHours.hours)).toString(MINUTES))
        }.format()
        abs < 24.hours -> abs.toString(HOURS).format()
        abs < 6.days -> buildString {
            append(abs.toString(DAYS))
            append(" ")
            append((abs - (abs.inWholeDays.days)).toString(HOURS))
        }
        else -> abs.toString(DAYS).format()
    }
}

fun Date.toMoment(): String {
    val now = Now
    val diff = now - this
    return when {
        diff < 30.days -> diff.toMoment()
        isSameDate(now) -> toLocaleTimeString("de-DE")
        else -> toLocaleString("de-DE")
    }
}
