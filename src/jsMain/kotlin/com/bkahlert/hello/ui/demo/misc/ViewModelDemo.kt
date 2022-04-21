package com.bkahlert.hello.ui.demo.misc

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.SimpleLogger.Companion.simpleLogger
import com.bkahlert.hello.plugins.clickup.Pomodoro.Type
import com.bkahlert.hello.plugins.clickup.PomodoroTimer
import com.bkahlert.hello.ui.demo.Demo
import com.bkahlert.hello.ui.demo.Demos
import com.bkahlert.hello.ui.demo.clickup.ClickUpFixtures
import com.bkahlert.hello.ui.demo.clickup.ClickUpFixtures.running
import com.bkahlert.hello.ui.demo.failedResponse
import com.bkahlert.hello.ui.demo.misc.ViewModelDemoStuff.TestViewModel
import com.bkahlert.hello.ui.demo.response
import com.bkahlert.kommons.math.isOdd
import com.bkahlert.kommons.text.randomString
import com.bkahlert.kommons.time.Now
import com.clickup.api.Tag
import com.clickup.api.TaskID
import com.clickup.api.TimeEntry
import com.clickup.api.TimeEntryID
import com.semanticui.compose.element.Header
import com.semanticui.compose.view.Item
import com.semanticui.compose.view.Items
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import kotlin.time.Duration.Companion.seconds

@Composable
fun ViewModelDemo() {
    Demos("View Model") {
        Demo("View Model") {
            val testViewModel = remember { TestViewModel("initial") }
            val testState = testViewModel.testState
            val text = testState.text

            testViewModel.timeEntry?.also {
                PomodoroTimer(
                    timeEntry = it,
                    onStop = { timeEntry, tags ->
                        console.info("stopping $timeEntry with $tags")
                        testViewModel.stop(it, tags)
                    },
                )
            }

            if (false) {

                // Igniting the game loop
                LaunchedEffect(Unit) {
                    while (true) {
                        delay(5.seconds)
                        testViewModel.updateText(Now.toISOString().also { console.log("updating STATE to $it") })
                    }
                }

                Items {
                    Item {
                        Header { Text("Timer") }
                        Div({ classes("content") }) {
                            Text(text.also { console.log("updating ITEM to $it") })
                            Br()
                            Div({ classes("ui", "horizontal", "list") }) {
                                val state by testState.items.collectAsState("")
                                Div({ classes("item") }) {
                                    Text(state)
                                }
                            }
                        }
                    }
                }

                Div {
                    Text(text.also { console.log("updating DIV to $it") })
                }
            }
        }
    }
}

private object ViewModelDemoStuff {
    suspend fun restCall(): Result<String> {
        delay(500)
        return when (Now.getMilliseconds().isOdd) {
            true -> response("rest response at ${Now.toTimeString()}")
            else -> failedResponse()
        }
    }

    class TestViewModel(initialText: String) {
        private val logger = simpleLogger()

        var testState by mutableStateOf(TestState(initialText, emptyFlow()))
            private set

        private fun update(transform: TestState.() -> TestState) {
            testState = testState.transform()
        }

        fun updateText(newText: String) {
            update {
                copy(
                    text = newText,
                    items = flow {
                        restCall().fold(
                            {
                                emit(it)
                                delay(200)
                                emit("$it - #1")
                                delay(200)
                                emit("$it - #2")
                                delay(200)
                                emit("$it - #3")
                                delay(200)
                                emit("$it - #4")
                            },
                            { emit("failure") }
                        )
                    }
                )
            }
        }

        var timeEntry: TimeEntry? by mutableStateOf(ClickUpFixtures.TimeEntry.running())
            private set

        fun start(taskID: TaskID?, type: Type) {
            timeEntry = ClickUpFixtures.TimeEntry.running(start = Now).copy(
                id = TimeEntryID((taskID?.stringValue ?: "unknown") + "-${randomString()}"),
                tags = type.addTag(ClickUpFixtures.TimeEntry.running().tags).also { console.log("$it") },
            )
            logger.info("Time entry $timeEntry started")
        }

        fun stop(timeEntry: TimeEntry, tags: List<Tag>) {
            this.timeEntry = null
            logger.info("Time entry $timeEntry stopped with $tags")
        }
    }

    data class TestState(
        val text: String,
        val items: Flow<String>,
    )
}
