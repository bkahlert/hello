package com.bkahlert.hello.ui.demo.clickup

import androidx.compose.runtime.Composable
import com.bkahlert.hello.plugins.clickup.Pomodoro.Type.Debug
import com.bkahlert.hello.plugins.clickup.Pomodoro.Type.Pro
import com.bkahlert.hello.plugins.clickup.PomodoroStarter
import com.bkahlert.hello.ui.demo.Demo
import com.bkahlert.hello.ui.demo.Demos
import com.clickup.api.Tag
import com.clickup.api.TaskID

@Composable
fun PomodoroStarterDemo() {
    Demos("Pomodoro Starter") {
        Demo("no task") {
            PomodoroStarter(taskID = null, type = Pro, onStart = onStart)
        }
        Demo("task") {
            PomodoroStarter(taskID = taskID, type = Debug, onStart = onStart)
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

private val onStart: (TaskID, List<Tag>, Boolean) -> Unit = { task, tags, billable ->
    console.info("starting billable=$billable pomodoro $tags for $task")
}
