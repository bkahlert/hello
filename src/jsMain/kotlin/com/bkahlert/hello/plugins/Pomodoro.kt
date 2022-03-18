package com.bkahlert.hello.plugins

import com.clickup.api.TimeEntry
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

data class Pomodoro(
    val timeEntryID: TimeEntry.ID,
    val duration: Duration,
) {
    enum class Type(val duration: Duration) {
        Classic(25.minutes), Pro(50.minutes)
    }
}
