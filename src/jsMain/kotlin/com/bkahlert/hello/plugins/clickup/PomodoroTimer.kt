package com.bkahlert.hello.plugins.clickup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.plugins.Pomodoro
import com.bkahlert.hello.plugins.Pomodoro.Companion.format
import com.bkahlert.kommons.Color.RGB
import com.bkahlert.kommons.time.Now
import com.bkahlert.kommons.time.minus
import com.clickup.api.TimeEntry
import com.semanticui.compose.element.Icon
import kotlinx.browser.window
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
fun PomodoroTimer(
    timeEntry: TimeEntry,
    onAbort: (TimeEntry) -> Unit = {},
    onComplete: (TimeEntry) -> Unit = {},
    fps: Double = 15.0,
) {

    var tick by remember { mutableStateOf(0) }
    val pomodoro = Pomodoro.of(timeEntry)
    val passed = tick.let { Now - timeEntry.start }
    val remaining = pomodoro.duration - passed
    val ended = timeEntry.end != null

    if (!ended && remaining < Duration.ZERO) {
        LaunchedEffect(timeEntry) {
            onComplete(timeEntry)
        }
    }

    if (ended) {
        Div {
            Icon("green", "check", "circle")
            Span({
                style { color(RGB(0x1a531b)) }
            }) { Text(Duration.ZERO.format()) }
        }
    } else {
        Div {
            Icon("red", "stop", "circle", {
                +Link
                onClick { onAbort(timeEntry) }
            })
            Text(remaining.format())
        }

        DisposableEffect(timeEntry) {
            val timeout = 1.seconds / fps
            // avoid flickering by initially waiting one second
            val handle: Int = window.setInterval({ tick = Random.nextInt() }, timeout = timeout.inWholeMilliseconds.toInt())
            onDispose { window.clearInterval(handle) }
        }
    }
}
