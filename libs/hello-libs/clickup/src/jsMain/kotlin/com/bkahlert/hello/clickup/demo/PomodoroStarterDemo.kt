package com.bkahlert.hello.clickup.demo

import androidx.compose.runtime.Composable
import com.bkahlert.hello.clickup.Pomodoro.Type.Debug
import com.bkahlert.hello.clickup.Pomodoro.Type.Pro
import com.bkahlert.hello.clickup.model.fixtures.ClickUpFixtures
import com.bkahlert.hello.clickup.viewmodel.PomodoroStarter
import com.bkahlert.hello.clickup.viewmodel.rememberPomodoroStarterState
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.Demos

@Composable
public fun PomodoroStarterDemo() {
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
