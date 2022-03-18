package com.bkahlert.kommons.time

import kotlin.js.Date

/**
 * Computer whether the [other] date
 * is on the same date.
 */
fun Date.isSameDate(other: Date): Boolean =
    other.getDate() == getDate() && other.getMonth() == getMonth() && other.getFullYear() == getFullYear()

/**
 * Adds the [other] date to this date.
 */
public inline operator fun Date.compareTo(other: Date): Int {
    val diff = getTime() - other.getTime()
    return when {
        diff < 0.0 -> -1
        diff > 0.0 -> +1
        else -> return 0
    }
}
