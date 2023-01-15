package com.bkahlert.hello.debug.clickup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.bkahlert.hello.clickup.Pomodoro.Type
import com.bkahlert.hello.clickup.model.fixtures.ClickUpFixtures
import com.bkahlert.hello.clickup.model.fixtures.ClickUpFixtures.aborted
import com.bkahlert.hello.clickup.model.fixtures.ClickUpFixtures.completed
import com.bkahlert.hello.clickup.model.fixtures.ClickUpFixtures.running
import com.bkahlert.hello.clickup.viewmodel.PomodoroTimer
import com.bkahlert.hello.clickup.viewmodel.rememberPomodoroTimerState
import com.bkahlert.hello.debug.Demo
import com.bkahlert.hello.debug.Demos
import com.bkahlert.kommons.minus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.js.Date
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

@Composable
fun PomodoroTimerDemo() {
    Demos("Pomodoro Timer") {
        Demo("aborted") {
            PomodoroTimer(
                rememberPomodoroTimerState(
                    ClickUpFixtures.TimeEntry.aborted(),
                    progressIndicating = true,
                )
            )
        }
        Demo("completed") {
            PomodoroTimer(
                rememberPomodoroTimerState(
                    ClickUpFixtures.TimeEntry.completed(),
                    progressIndicating = true,
                )
            )
        }
        Demo("completed and exceeded") {
            PomodoroTimer(
                rememberPomodoroTimerState(
                    ClickUpFixtures.TimeEntry.completed(start = Date() - 1.days),
                    progressIndicating = true,
                )
            )
        }
        Demo("exceeded") {
            PomodoroTimer(
                rememberPomodoroTimerState(
                    ClickUpFixtures.TimeEntry.running(start = Date() - 1.days),
                    progressIndicating = true,
                )
            )
        }
    }
    Demos("Pomodoro Timer (Running)") {
        enumValues<Type>().forEach { type ->
            Demo(type.name) {
                val scope = rememberCoroutineScope()
                var timeEntry by remember {
                    mutableStateOf(
                        ClickUpFixtures.TimeEntry.running(
                            start = Date(),
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
                                timeEntry = entry.copy(end = Date(), tags = entry.tags + tags)
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
