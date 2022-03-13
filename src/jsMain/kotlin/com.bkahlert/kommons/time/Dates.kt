package com.bkahlert.kommons.time

import kotlin.js.Date

fun Date.isSameDate(other: Date): Boolean =
    other.getDate() == getDate() && other.getMonth() == getMonth() && other.getFullYear() == getFullYear()
