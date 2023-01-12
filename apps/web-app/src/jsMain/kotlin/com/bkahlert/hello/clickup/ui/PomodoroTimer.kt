package com.bkahlert.hello.clickup.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.clickup.Pomodoro
import com.bkahlert.hello.clickup.Pomodoro.Companion.format
import com.bkahlert.hello.clickup.Pomodoro.Status.Aborted
import com.bkahlert.hello.clickup.Pomodoro.Status.Completed
import com.bkahlert.hello.clickup.Pomodoro.Status.Prepared
import com.bkahlert.hello.clickup.Pomodoro.Status.Running
import com.bkahlert.hello.clickup.model.Tag
import com.bkahlert.hello.clickup.model.TimeEntry
import com.bkahlert.hello.compose.color
import com.bkahlert.hello.semanticui.element.Icon
import com.bkahlert.hello.semanticui.jQuery
import com.bkahlert.hello.ui.AcousticFeedback
import kotlinx.browser.window
import org.jetbrains.compose.web.css.fontWeight
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Stable
interface PomodoroTimerState {
    val timeEntry: TimeEntry
    val pomodoro: Pomodoro
    val fps: Double
    val progressIndicating: Boolean
    val acousticFeedback: AcousticFeedback
    val onStop: (List<Tag>) -> Unit
}

class PomodoroTimerStateImpl(
    override val timeEntry: TimeEntry,
    override val fps: Double,
    override val progressIndicating: Boolean,
    override val acousticFeedback: AcousticFeedback,
    override val onStop: (List<Tag>) -> Unit,
) : PomodoroTimerState {
    override val pomodoro: Pomodoro = Pomodoro.of(timeEntry)
}

@Composable
fun rememberPomodoroTimerState(
    timeEntry: TimeEntry,
    fps: Double = 15.0,
    progressIndicating: Boolean = false,
    acousticFeedback: AcousticFeedback = AcousticFeedback.NoFeedback,
    onStop: (TimeEntry, List<Tag>) -> Unit = { _, tags ->
        console.log("stopped ${timeEntry.id} with $tags")
    },
): PomodoroTimerState {
    return remember(timeEntry, fps, progressIndicating, acousticFeedback, onStop) {
        PomodoroTimerStateImpl(
            timeEntry = timeEntry,
            fps = fps,
            progressIndicating = progressIndicating,
            acousticFeedback = acousticFeedback,
            onStop = { tags -> onStop(timeEntry, tags) },
        )
    }
}

@Composable
fun PomodoroTimer(
    state: PomodoroTimerState,
    stop: () -> Boolean = { false },
) {
    var tick: Long by remember(state) { mutableStateOf(0L) }
    val pomodoro = state.pomodoro
    val status = pomodoro.status
    val passed = tick.let { state.timeEntry.passed }
    val remaining = pomodoro.duration - passed
    val progress = (passed / pomodoro.duration).coerceAtMost(1.0)

    if (state.progressIndicating) {
        Div({
            classes("ui", "top", "attached", "indicating", "progress")
            attr("data-value", "${passed.inWholeSeconds}")
            attr("data-total", "${pomodoro.duration.inWholeSeconds}")
        }) {
            Div({ classes("bar") })
            DisposableEffect(tick) {
                when (status) {
                    Prepared -> jQuery(scopeElement).progress("remove active")
                    // supposed to work with data-value, but not always working (see Debug Mode F4)
                    Running -> jQuery(scopeElement).progress("set percent", "${progress * 100.0}")
                    Aborted -> jQuery(scopeElement).progress("set error")
                    Completed -> jQuery(scopeElement).progress("set success")
                }
                onDispose { jQuery(scopeElement).progress("remove active") }
            }
        }
    }

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
                +Link
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

    if (remaining < 0.5.seconds) {
        DisposableEffect(state) {
            state.acousticFeedback.completed.play()
            state.onStop(listOf(Completed.tag))
            onDispose { }
        }
    } else {
        DisposableEffect(state) {
            val timeout = 1.seconds / state.fps
            // avoid flickering by initially waiting one second
            val handle: Int = window.setInterval({ tick++ }, timeout = timeout.inWholeMilliseconds.toInt())
            onDispose { window.clearInterval(handle) }
        }
    }
}
