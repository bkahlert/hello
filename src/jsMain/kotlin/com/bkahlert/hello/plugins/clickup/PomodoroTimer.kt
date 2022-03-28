package com.bkahlert.hello.plugins.clickup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.plugins.clickup.Pomodoro.Companion.format
import com.bkahlert.hello.plugins.clickup.Pomodoro.Status.Aborted
import com.bkahlert.hello.plugins.clickup.Pomodoro.Status.Completed
import com.bkahlert.hello.plugins.clickup.Pomodoro.Status.Prepared
import com.bkahlert.hello.plugins.clickup.Pomodoro.Status.Running
import com.bkahlert.hello.ui.AcousticFeedback
import com.bkahlert.hello.ui.DimmingLoader
import com.clickup.api.Tag
import com.clickup.api.TimeEntry
import com.semanticui.compose.element.Icon
import com.semanticui.compose.jQuery
import kotlinx.browser.window
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.fontWeight
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
fun PomodoroTimer(
    timeEntry: TimeEntry,
    onStop: (TimeEntry, List<Tag>) -> Unit = { _, _ -> },
    fps: Double = 15.0,
    progressIndicating: Boolean = true,
    acousticFeedback: AcousticFeedback = AcousticFeedback.NoFeedback,
) {
    var tick: Long by remember(timeEntry) { mutableStateOf(0L) }
    val pomodoro = Pomodoro.of(timeEntry)
    val status = pomodoro.status
    val passed = tick.let { timeEntry.passed }
    val remaining = pomodoro.duration - passed
    val progress = (passed / pomodoro.duration).coerceAtMost(1.0)

    if (progressIndicating) {
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

    var stopping by remember(timeEntry) { mutableStateOf(false) }
    DimmingLoader({ stopping })

    if (timeEntry.ended) {
        Div {
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
        }
    } else {
        Div {
            Icon("red", "stop", "circle", {
                if (!stopping) {
                    +Link
                    onClick {
                        stopping = true
                        onStop(timeEntry, listOf(Aborted.tag))
                    }
                }
            })
            Span({
                style {
                    fontWeight(700)
                }
            }) { Text(remaining.format()) }
        }

        if (remaining < 0.5.seconds) {
            LaunchedEffect(timeEntry) { // TODO likely start with a remembered coroutine
                stopping = true
                acousticFeedback.completed.play()
                onStop(timeEntry, listOf(Completed.tag))
            }
        }

        if (!stopping) {
            DisposableEffect(timeEntry) {
                val timeout = 1.seconds / fps
                // avoid flickering by initially waiting one second
                val handle: Int = window.setInterval({ tick++ }, timeout = timeout.inWholeMilliseconds.toInt())
                onDispose { window.clearInterval(handle) }
            }
        }
    }
}
