package com.bkahlert.hello.clickup.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.clickup.Pomodoro.Type
import com.bkahlert.hello.clickup.model.fixtures.ClickUpFixtures
import com.bkahlert.hello.clickup.model.fixtures.ClickUpFixtures.aborted
import com.bkahlert.hello.clickup.model.fixtures.ClickUpFixtures.completed
import com.bkahlert.hello.clickup.model.fixtures.ClickUpFixtures.running
import com.bkahlert.hello.clickup.viewmodel.PomodoroTimer
import com.bkahlert.hello.clickup.viewmodel.rememberPomodoroTimerState
import com.bkahlert.kommons.minus
import com.bkahlert.semanticui.custom.rememberReportingCoroutineScope
import com.bkahlert.semanticui.demo.DEMO_BASE_DELAY
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.Demos
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.js.Date
import kotlin.time.Duration.Companion.days

@Composable
public fun PomodoroTimerDemo() {
    Demos("Pomodoro Timer") {
        Demo("aborted") {
            PomodoroTimer(
                rememberPomodoroTimerState(
                    ClickUpFixtures.TimeEntry.aborted(),
                )
            )
        }
        Demo("completed") {
            PomodoroTimer(
                rememberPomodoroTimerState(
                    ClickUpFixtures.TimeEntry.completed(),
                )
            )
        }
        Demo("completed and exceeded") {
            PomodoroTimer(
                rememberPomodoroTimerState(
                    ClickUpFixtures.TimeEntry.completed(start = Date() - 1.days),
                )
            )
        }
        Demo("exceeded") {
            PomodoroTimer(
                rememberPomodoroTimerState(
                    ClickUpFixtures.TimeEntry.running(start = Date() - 1.days),
                )
            )
        }
    }
    Demos("Pomodoro Timer (Running)") {
        enumValues<Type>().forEach { type ->
            Demo(type.name) {
                val scope = rememberReportingCoroutineScope()
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
                        onStop = { entry, tags ->
                            scope.launch {
                                delay(DEMO_BASE_DELAY)
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
