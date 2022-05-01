package com.bkahlert.hello.ui.demo.clickup

import androidx.compose.runtime.Composable
import com.bkahlert.hello.plugins.clickup.Pomodoro.Type.Debug
import com.bkahlert.hello.plugins.clickup.Pomodoro.Type.Pro
import com.bkahlert.hello.plugins.clickup.PomodoroStarter
import com.bkahlert.hello.plugins.clickup.rememberPomodoroStarterState
import com.bkahlert.hello.ui.demo.Demo
import com.bkahlert.hello.ui.demo.Demos
import com.clickup.api.Tag
import com.clickup.api.TaskID

@Composable
fun PomodoroStarterDemo() {
    Demos("Pomodoro Starter") {
        Demo("no task") {
            PomodoroStarter(rememberPomodoroStarterState(taskID = null, selected = { it == Pro }), onStart = onStart)
        }
        Demo("task") {
            PomodoroStarter(rememberPomodoroStarterState(taskID = taskID, selected = { it == Debug }), onStart = onStart)
        }
        Demo("billable") {
            PomodoroStarter(rememberPomodoroStarterState(taskID = taskID, billable = true), onStart = onStart)
        }
        Demo("non-billable") {
            PomodoroStarter(rememberPomodoroStarterState(taskID = taskID, billable = false), onStart = onStart)
        }
    }
}

private val taskID = ClickUpFixtures.Tasks.first().id

private val onStart: (TaskID?, List<Tag>, Boolean) -> Unit = { task, tags, billable ->
    console.info("starting billable=$billable pomodoro $tags for $task")
}
