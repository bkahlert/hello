package com.bkahlert.hello.ui.demo.clickup

import androidx.compose.runtime.Composable
import com.bkahlert.hello.plugins.clickup.Pomodoro.Type.Debug
import com.bkahlert.hello.plugins.clickup.Pomodoro.Type.Pro
import com.bkahlert.hello.plugins.clickup.PomodoroStarter
import com.bkahlert.hello.plugins.clickup.rememberPomodoroStarterState
import com.bkahlert.hello.ui.demo.Demo
import com.bkahlert.hello.ui.demo.Demos

@Composable
fun PomodoroStarterDemo() {
    Demos("Pomodoro Starter") {
        Demo("no task") {
            PomodoroStarter(rememberPomodoroStarterState(taskID = null, selected = { it == Pro }))
        }
        Demo("task") {
            PomodoroStarter(rememberPomodoroStarterState(taskID = taskID, selected = { it == Debug }))
        }
        Demo("billable") {
            PomodoroStarter(rememberPomodoroStarterState(taskID = taskID, billable = true))
        }
        Demo("non-billable") {
            PomodoroStarter(rememberPomodoroStarterState(taskID = taskID, billable = false))
        }
    }
}

private val taskID = ClickUpFixtures.Tasks.first().id
