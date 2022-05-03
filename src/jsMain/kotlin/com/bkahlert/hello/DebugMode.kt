package com.bkahlert.hello

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import com.bkahlert.hello.DebugModeState.Active
import com.bkahlert.hello.DebugModeState.Inactive
import com.bkahlert.hello.SimpleLogger.Companion.simpleLogger
import com.bkahlert.kommons.dom.body
import kotlinx.browser.document
import kotlinx.dom.addClass
import org.jetbrains.compose.web.dom.DOMScope
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import org.w3c.dom.events.KeyboardEvent

/**
 * Debug mode that renders the [content] [Composable] on activation.
 *
 * Debug mode is activated when a `keydown` event with the specified [key]
 * on the specified [eventTarget].
 */
class DebugMode(
    private val eventTarget: EventTarget = document.body(),
    private val key: String = "F4",
    disableOnEscape: Boolean = true,
    active: Boolean = false,
    private val onStateChange: (DebugModeState) -> Unit = {},
    private val content: @Composable DOMScope<HTMLDivElement>.() -> Unit,
) {

    var state: DebugModeState = Inactive
        private set(value) {
            if (field != value) {
                field = value
                onStateChange(value)
            }
        }

    var active: Boolean
        get() = state is Active
        set(value) {
            state = when (val current = state) {
                is Active -> {
                    if (value) {
                        current
                    } else {
                        Logger.debug("Debug mode disabled")
                        current.composition.dispose()
                        checkNotNull(current.root.parentElement) { "missing root container" }.removeChild(current.root)
                        Inactive
                    }
                }
                is Inactive -> {
                    if (value) {
                        Logger.debug("Debug mode enabled")
                        val body = checkNotNull(document.body) { "missing body tag" }
                        val root = (document.createElement("div") as HTMLDivElement).apply {
                            addClass("debug-mode")
                        }
                        val composition = renderComposable(root = root, content = content)
                        body.append(root)
                        Active(root, composition)
                    } else {
                        current
                    }
                }
            }
        }

    init {
        Logger.debug("Setting up debug mode")
        val callback: (Event) -> Unit = when (disableOnEscape) {
            true -> {
                { event ->
                    if (event.target == eventTarget) {
                        val pressed = (event as KeyboardEvent).key
                        if (pressed == key) this.active = !this.active
                        else if (pressed.equals("Escape", ignoreCase = true)) this.active = false
                    }
                }
            }
            else -> {
                { event ->
                    if (event.target == eventTarget) {
                        if ((event as KeyboardEvent).key == key) this.active = !active
                    }
                }
            }
        }
        eventTarget.addEventListener("keydown", callback)
        this.active = active
    }

    companion object {
        private val Logger = simpleLogger()
    }
}

sealed class DebugModeState {
    object Inactive : DebugModeState()
    data class Active(
        val root: HTMLDivElement,
        val composition: Composition,
    ) : DebugModeState()
}
