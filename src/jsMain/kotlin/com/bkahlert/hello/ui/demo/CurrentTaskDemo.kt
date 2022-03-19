package com.bkahlert.hello.ui.demo

import androidx.compose.runtime.Composable
import com.bkahlert.hello.plugins.CurrentTask

@Composable
fun CurrentTaskDemo() {
    Demos("Current Task") {
        Demo("no response") {
            CurrentTask(response(null)) {
                console.info("stopping $it")
            }
        }
        Demo("failed response") {
            CurrentTask(failedResponse()) {
                console.info("stopping $it")
            }
        }
        Demo("succeeded response") {
            CurrentTask(response(TimeEntryFixtures.running())) {
                console.info("stopping $it")
            }
        }
    }
}
