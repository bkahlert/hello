package com.bkahlert.hello.debug.clickup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.bkahlert.hello.clickup.ui.Pomodoro.Type
import com.bkahlert.hello.clickup.ui.PomodoroTimer
import com.bkahlert.hello.clickup.ui.rememberPomodoroTimerState
import com.bkahlert.hello.debug.Demo
import com.bkahlert.hello.debug.Demos
import com.bkahlert.hello.debug.clickup.ClickUpFixtures.aborted
import com.bkahlert.hello.debug.clickup.ClickUpFixtures.completed
import com.bkahlert.hello.debug.clickup.ClickUpFixtures.running
import com.bkahlert.kommons.time.Now
import com.bkahlert.kommons.time.minus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

@Composable
fun PomodoroTimerDemo() {
    Demos("Pomodoro Timer") {
        Demo("aborted") {
            PomodoroTimer(rememberPomodoroTimerState(ClickUpFixtures.TimeEntry.aborted()))
        }
        Demo("completed") {
            PomodoroTimer(rememberPomodoroTimerState(ClickUpFixtures.TimeEntry.completed()))
        }
        Demo("completed and exceeded") {
            PomodoroTimer(rememberPomodoroTimerState(ClickUpFixtures.TimeEntry.completed(start = Now - 1.days)))
        }
        Demo("exceeded") {
            PomodoroTimer(rememberPomodoroTimerState(ClickUpFixtures.TimeEntry.running(start = Now - 1.days)))
        }
    }
    Demos("Pomodoro Timer (Running)") {
        enumValues<Type>().forEach { type ->
            Demo(type.name) {
                val scope = rememberCoroutineScope()
                var timeEntry by remember {
                    mutableStateOf(
                        ClickUpFixtures.TimeEntry.running(
                            start = Now,
                            type = type
                        )
                    )
                }
                PomodoroTimer(
                    rememberPomodoroTimerState(
                        timeEntry = timeEntry,
                        progressIndicating = true,
                        onStop = { entry, tags ->
                            scope.launch {
                                delay(5.seconds)
                                timeEntry = entry.copy(end = Now, tags = entry.tags + tags)
                            }
                        },
                    )
                )
            }
        }
        Demo("unknown type") {
            PomodoroTimer(rememberPomodoroTimerState(ClickUpFixtures.TimeEntry.running(type = null)))
        }
    }
}
