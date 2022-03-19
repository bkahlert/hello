package com.bkahlert.hello.ui.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.deserialize
import com.bkahlert.hello.plugins.Pomodoro
import com.bkahlert.hello.plugins.clickup.PomodoroTimer
import com.bkahlert.kommons.serialization.Named
import com.bkahlert.kommons.time.Now
import com.bkahlert.kommons.time.minus
import com.bkahlert.kommons.time.minutes
import com.clickup.api.TimeEntry
import kotlin.js.Date
import kotlin.time.Duration.Companion.days

@Composable
fun PomodoroTimerDemo() {
    Demos("Pomodoro Timer") {
        Demos("running") {
            enumValues<Pomodoro.Type>().forEach { type ->
                Demo(type.name) {
                    var timeEntry by remember { mutableStateOf(TimeEntryFixtures.running(type = type)) }
                    PomodoroTimer(timeEntry, onComplete = {
                        console.warn("completed pomodoro of type $type: $it")
                        timeEntry = timeEntry.copy(end = Now)
                    })
                }
            }
            Demo("unknown type") {
                PomodoroTimer(TimeEntryFixtures.running(type = null))
            }
        }
        Demo("completed") {
            PomodoroTimer(TimeEntryFixtures.completed())
        }
        Demo("completed and exceeded") {
            PomodoroTimer(TimeEntryFixtures.completed(start = Now - 1.days))
        }
        Demo("exceeded") {
            PomodoroTimer(TimeEntryFixtures.running(start = Now - 1.days))
        }
    }
}

object TimeEntryFixtures {
    private val running =
        // language=JSON
        """
        {
          "data": {
            "id": "3873003127832353210",
            "task": {
              "id": "30jg1er",
              "name": "get things done",
              "status": {
                "status": "in progress",
                "color": "#a875ff",
                "type": "custom",
                "orderindex": 1
              },
              "custom_type": null
            },
            "wid": "3576831",
            "user": {
              "id": 3687596,
              "username": "John Doe",
              "email": "john.doe@example.com",
              "color": "#4169E1",
              "initials": "JD",
              "profilePicture": "$SPACER"
            },
            "billable": false,
            "start": "1647040470454",
            "duration": -13523,
            "description": "",
            "tags": [],
            "source": "clickup",
            "at": "1647040470454",
            "task_location": {
              "list_id": "25510969",
              "folder_id": "11087491",
              "space_id": "4564985"
            },
            "task_url": "https://app.clickup.com/t/20jg1er"
          }
        }
        """.trimIndent().deserialize<Named<TimeEntry>>().value

    fun running(
        start: Date = Now - 3.5.minutes,
        type: Pomodoro.Type? = null,
    ) = running.let {
        it.copy(
            start = start,
            tags = type?.addTag(it.tags) ?: it.tags
        )
    }

    fun completed(
        start: Date = Now - Pomodoro.Type.Default.duration,
        end: Date = Now,
        type: Pomodoro.Type? = Pomodoro.Type.Default,
    ) = running.let {
        it.copy(
            start = start,
            end = end,
            tags = type?.addTag(it.tags) ?: it.tags
        )
    }
}
