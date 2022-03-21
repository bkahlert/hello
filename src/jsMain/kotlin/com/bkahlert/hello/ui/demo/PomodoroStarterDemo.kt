package com.bkahlert.hello.ui.demo

import androidx.compose.runtime.Composable
import com.bkahlert.hello.plugins.clickup.Pomodoro
import com.bkahlert.hello.plugins.clickup.PomodoroStarter
import com.clickup.api.Tag
import com.clickup.api.Task

@Composable
fun PomodoroStarterDemo() {
    Demos("Pomodoro Starter") {
        Demo("no task") {
            PomodoroStarter(taskID = null, type = Pomodoro.Type.Pro, onStart = onStart)
        }
        Demo("task") {
            PomodoroStarter(taskID = taskID, type = Pomodoro.Type.Debug, onStart = onStart)
        }
        Demo("billable") {
            PomodoroStarter(taskID = taskID, billable = true, onStart = onStart)
        }
        Demo("non-billable") {
            PomodoroStarter(taskID = taskID, billable = false, onStart = onStart)
        }
    }
}

private val taskID = ClickupFixtures.TASKS.first().id

private val onStart: (Task.ID, List<Tag>, Boolean) -> Unit = { task, tags, billable ->
    console.info("starting billable=$billable pomodoro $tags for $task")
}
