package com.bkahlert.hello.ui.demo.clickup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.bkahlert.hello.plugins.clickup.Pomodoro.Type
import com.bkahlert.hello.plugins.clickup.PomodoroTimer
import com.bkahlert.hello.ui.demo.Demo
import com.bkahlert.hello.ui.demo.Demos
import com.bkahlert.hello.ui.demo.clickup.ClickUpFixtures.aborted
import com.bkahlert.hello.ui.demo.clickup.ClickUpFixtures.completed
import com.bkahlert.hello.ui.demo.clickup.ClickUpFixtures.running
import com.bkahlert.kommons.time.Now
import com.bkahlert.kommons.time.minus
import com.clickup.api.Tag
import com.clickup.api.TimeEntry
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

@Composable
fun PomodoroTimerDemo() {
    Demos("Pomodoro Timer") {
        Demo("aborted") {
            PomodoroTimer(ClickUpFixtures.TimeEntry.aborted(), onStop = onStop)
        }
        Demo("completed") {
            PomodoroTimer(ClickUpFixtures.TimeEntry.completed(), onStop = onStop)
        }
        Demo("completed and exceeded") {
            PomodoroTimer(ClickUpFixtures.TimeEntry.completed(start = Now - 1.days), onStop = onStop)
        }
        Demo("exceeded") {
            PomodoroTimer(ClickUpFixtures.TimeEntry.running(start = Now - 1.days), onStop = onStop)
        }
    }
    Demos("Pomodoro Timer (Running)") {
        enumValues<Type>().forEach { type ->
            Demo(type.name) {
                val scope = rememberCoroutineScope()
                var timeEntry by remember {
                    mutableStateOf(ClickUpFixtures.TimeEntry.running(start = Now,
                        type = type))
                }
                PomodoroTimer(
                    timeEntry = timeEntry,
                    onStop = { entry, tags ->
                        onStop(entry, tags)
                        scope.launch {
                            delay(5.seconds)
                            timeEntry = entry.copy(end = Now, tags = entry.tags + tags)
                        }
                    },
                )
            }
        }
        Demo("unknown type") {
            PomodoroTimer(ClickUpFixtures.TimeEntry.running(type = null), onStop = onStop)
        }
    }
}

private val onStop: (TimeEntry, List<Tag>) -> Unit = { timeEntry, tags ->
    console.info("stopping $timeEntry with $tags")
}
