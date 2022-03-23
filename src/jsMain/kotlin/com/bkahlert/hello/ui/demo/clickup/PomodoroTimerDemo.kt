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
        Demos("running") {
            enumValues<Type>().forEach { type ->
                Demo(type.name) {
                    val scope = rememberCoroutineScope()
                    var timeEntry by remember {
                        mutableStateOf(TimeEntryFixtures.running(
                            start = Now,
                            type = type,
                        ))
                    }
                    PomodoroTimer(
                        timeEntry = timeEntry,
                        onAbort = { entry, tags ->
                            onAbort(entry, tags)
                            scope.launch {
                                delay(5.seconds)
                                timeEntry = entry.copy(end = Now, tags = entry.tags + tags)
                            }
                        },
                        onComplete = { entry, tags ->
                            onComplete(entry, tags)
                            scope.launch {
                                delay(5.seconds)
                                timeEntry = entry.copy(end = Now, tags = entry.tags + tags)
                            }
                        }
                    )
                }
            }
            Demo("unknown type") {
                PomodoroTimer(TimeEntryFixtures.running(type = null), onAbort, onComplete)
            }
        }
        Demo("aborted") {
            PomodoroTimer(TimeEntryFixtures.aborted(), onAbort, onComplete)
        }
        Demo("completed") {
            PomodoroTimer(TimeEntryFixtures.completed(), onAbort, onComplete)
        }
        Demo("completed and exceeded") {
            PomodoroTimer(TimeEntryFixtures.completed(start = Now - 1.days), onAbort, onComplete)
        }
        Demo("exceeded") {
            PomodoroTimer(TimeEntryFixtures.running(start = Now - 1.days), onAbort, onComplete)
        }
    }
}

private val onAbort: (TimeEntry, List<Tag>) -> Unit = { timeEntry, tags ->
    console.info("aborting $timeEntry with $tags")
}

private val onComplete: (TimeEntry, List<Tag>) -> Unit = { timeEntry, tags ->
    console.info("completing $timeEntry with $tags")
}
