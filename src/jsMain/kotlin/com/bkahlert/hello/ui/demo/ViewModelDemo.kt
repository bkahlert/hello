package com.bkahlert.hello.ui.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.Failure
import com.bkahlert.hello.SimpleLogger.Companion.simpleLogger
import com.bkahlert.hello.Success
import com.bkahlert.hello.plugins.CurrentTask
import com.bkahlert.hello.plugins.Pomodoro.Type
import com.bkahlert.hello.plugins.clickup.PomodoroTimer
import com.bkahlert.kommons.Either
import com.bkahlert.kommons.Either.Left
import com.bkahlert.kommons.Either.Right
import com.bkahlert.kommons.fix.value
import com.bkahlert.kommons.math.isOdd
import com.bkahlert.kommons.text.randomString
import com.bkahlert.kommons.time.Now
import com.clickup.api.Task.ID
import com.clickup.api.TimeEntry
import com.clickup.api.rest.ClickUpException
import com.clickup.api.rest.ErrorInfo
import com.semanticui.compose.element.Header
import com.semanticui.compose.view.Item
import com.semanticui.compose.view.Items
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Hr
import org.jetbrains.compose.web.dom.Text
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
fun ViewModelDemo() {
    Demos("View Model") {
        Demo("View Model") {
            val testState = remember { TestViewModel("initial") }
            val text = testState.testUiState.text

            testState.timeEntry?.also {
                PomodoroTimer(
                    timeEntry = it,
                    onAbort = {
                        console.log("aborting", it)
                        testState.abort(it)
                    },
                    onComplete = {
                        console.log("completing", it)
                        testState.complete(it)
                    },
                )
            }

            if (false) {

                // Igniting the game loop
                LaunchedEffect(Unit) {
                    while (true) {
                        delay(5.seconds)
                        testState.updateText(Now.toISOString().also { console.log("updating STATE to $it") })
                    }
                }

                Items {
                    Item {
                        Header { Text("Timer") }
                        Div({ classes("content") }) {
                            Text(text.also { console.log("updating ITEM to $it") })
                            Br()
                            Div({ classes("ui", "horizontal", "list") }) {
                                val state by testState.testUiState.items.collectAsState("")
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

                Div {
                    console.warn("updating OTHER")
                    CurrentTask(response(null)) {
                        console.info("stopping $it")
                    }
                }
                Hr()
                Div {
                    CurrentTask(failedResponse()) {
                        console.info("stopping $it")
                    }
                }
                Hr()
                Div {
                    CurrentTask(response(TimeEntryFixtures.running())) {
                        console.info("stopping $it")
                    }
                }
            }
        }
    }
}

fun <T> response(value: T) = Left<T, Throwable>(value)
fun <T> failedResponse() = Right<T, Throwable>(ClickUpException(
    ErrorInfo("something went wrong", "TEST-1234"), RuntimeException("underlying problem")
))

suspend fun restCall(): Either<String, Throwable> {
    delay(500)
    return when (Now.getMilliseconds().isOdd) {
        true -> response("rest response at ${Now.toTimeString()}")
        else -> failedResponse()
    }
}

class TestViewModel(initialText: String) {
    private val logger = simpleLogger()

    var testUiState by mutableStateOf(TestUiState(initialText, emptyFlow()))
        private set

    private fun update(transform: TestUiState.() -> TestUiState) {
        testUiState = testUiState.transform()
    }

    fun updateText(newText: String) {
        update {
            copy(
                text = newText,
                items = flow {
                    val response = restCall()
                    when (response) {
                        is Success -> {
                            emit(response.value)
                            delay(200)
                            emit("${response.value} - #1")
                            delay(200)
                            emit("${response.value} - #2")
                            delay(200)
                            emit("${response.value} - #3")
                            delay(200)
                            emit("${response.value} - #4")
                        }
                        is Failure -> {
                            emit("failure")
                        }
                    }
                }
            )
        }
    }

    var timeEntry: TimeEntry? by mutableStateOf(TimeEntryFixtures.running())
        private set

    fun start(taskID: ID?, type: Type) {
        timeEntry = TimeEntryFixtures.running().copy(
            id = TimeEntry.ID((taskID?.stringValue ?: "unknown") + "-${randomString()}"),
            start = Now,
            end = null,
            duration = Duration.ZERO,
            tags = type.addTag(TimeEntryFixtures.running().tags).also { console.log("$it") },
            source = "String?",
            at = Now,
        )
        logger.info("Time entry $timeEntry started")
    }

    fun abort(timeEntry: TimeEntry) {
        this.timeEntry = null
        logger.info("Time entry $timeEntry aborted")
    }

    fun complete(timeEntry: TimeEntry) {
//        this.timeEntry = timeEntry.copy(end = Now)
        logger.info("Time entry $timeEntry completed")
    }
}

data class TestUiState(
    val text: String,
    val items: Flow<String>,
)
