package com.bkahlert.hello.clickup.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.bkahlert.hello.clickup.Pomodoro
import com.bkahlert.hello.clickup.Pomodoro.Companion.format
import com.bkahlert.hello.clickup.Pomodoro.Status
import com.bkahlert.hello.clickup.Pomodoro.Status.Aborted
import com.bkahlert.hello.clickup.Pomodoro.Status.Completed
import com.bkahlert.hello.clickup.model.Tag
import com.bkahlert.hello.clickup.model.TimeEntry
import com.bkahlert.kommons.js.console
import com.bkahlert.kommons.time.toMomentString
import com.bkahlert.semanticui.custom.color
import com.bkahlert.semanticui.custom.rememberReportingCoroutineScope
import com.bkahlert.semanticui.element.Icon
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.fontWeight
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Stable
public interface PomodoroTimerState {
    public val timeEntry: TimeEntry
    public val pomodoro: Pomodoro
    public val acousticFeedback: AcousticFeedback
    public val onStop: (List<Tag>) -> Unit

    public val status: Status
    public val remaining: StateFlow<Duration>
}

public class PomodoroTimerStateImpl(
    override val timeEntry: TimeEntry,
    override val acousticFeedback: AcousticFeedback,
    override val onStop: (List<Tag>) -> Unit,

    externalScope: CoroutineScope,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : PomodoroTimerState {
    override val pomodoro: Pomodoro = Pomodoro.of(timeEntry)

    override val status: Status get() = pomodoro.status

    override val remaining: MutableStateFlow<Duration> = MutableStateFlow(pomodoro.duration - timeEntry.passed)

    // TODO only allow aborting; onStop/Complete needs to be handled outside of timer view
    private val logging = false

    init {
        if (logging) console.debug("PomodoroTimerStateImpl: launching coroutine")
        externalScope.launch(defaultDispatcher) { // TODO use intervalFlow
            while (pomodoro.duration - timeEntry.passed >= 0.5.seconds) {
                if (logging) console.debug(
                    "PomodoroTimerStateImpl: launched coroutine",
                    "pomodoro: ", pomodoro.duration.toMomentString(),
                    "passed: ", timeEntry.passed.toMomentString(),
                    "remaining: ", (pomodoro.duration - timeEntry.passed).toMomentString(),
                )
                remaining.update { pomodoro.duration - timeEntry.passed }
                delay(500.milliseconds)
            }

            acousticFeedback.completed.play()
            onStop(listOf(Completed.tag))
        }
    }
}

@Composable
public fun rememberPomodoroTimerState(
    timeEntry: TimeEntry,
    acousticFeedback: AcousticFeedback = AcousticFeedback.NoFeedback,
    onStop: (TimeEntry, List<Tag>) -> Unit = { _, tags ->
        console.debug("rememberPomodoroTimerState: stopped ${timeEntry.id} with $tags")
    },
    externalScope: CoroutineScope = rememberReportingCoroutineScope(),
    defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
): PomodoroTimerState {
    return remember(timeEntry, acousticFeedback, onStop) {
        PomodoroTimerStateImpl(
            timeEntry = timeEntry,
            acousticFeedback = acousticFeedback,
            onStop = { tags -> onStop(timeEntry, tags) },
            externalScope = externalScope,
            defaultDispatcher = defaultDispatcher,
        )
    }
}


@Composable
public fun PomodoroTimer(
    state: PomodoroTimerState,
    stop: () -> Boolean = { false },
) {

    val status = state.pomodoro.status
    val remaining by state.remaining.collectAsState()

    Div {
        if (state.timeEntry.ended) {
            if (status == Aborted) {
                Icon("red", "times", "circle")
                Span({
                    style {
                        color(status.color)
                        fontWeight(700)
                    }
                }) { Text(remaining.format()) }
            } else {
                Icon("green", "check", "circle")
                Span({
                    style {
                        color(status.color)
                        fontWeight(700)
                    }
                }) { Text(Duration.ZERO.format()) }
            }
        } else {
            Icon("red", "stop", "circle") {
                classes("link")
                if (stop()) {
                    state.onStop(listOf(Aborted.tag))
                }
                onClick {
                    it.preventDefault()
                    state.onStop(listOf(Aborted.tag))
                }
            }
            Span({
                style {
                    fontWeight(700)
                }
            }) { Text(remaining.format()) }
        }
    }
}
