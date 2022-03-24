package com.bkahlert.hello

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import com.bkahlert.Brand
import com.bkahlert.hello.SimpleLogger.Companion.simpleLogger
import com.bkahlert.hello.plugins.clickup.Pomodoro
import com.bkahlert.kommons.dom.InMemoryStorage
import com.bkahlert.kommons.dom.Storage
import com.bkahlert.kommons.dom.default
import com.bkahlert.kommons.dom.provideDelegate
import com.clickup.api.Tag
import kotlinx.browser.document
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
    private val eventTarget: EventTarget = document,
    private val key: String = "F4",
    disableOnEscape: Boolean = true,
    storage: Storage = InMemoryStorage(),
    private val content: @Composable (DOMScope<HTMLDivElement>.() -> Unit),
) {
    private val logger = simpleLogger()
    private var active: Boolean by storage default false
    private var rootAndComposition: Pair<HTMLDivElement, Composition>? = null

    // TODO remove
    var xyz: String by storage default "init"
    var xyz123: String? by storage
    var test: Tag? by storage
    var nullablePomodoroType: Pomodoro.Type? by storage
    var pomodoroType: Pomodoro.Type by storage default Pomodoro.Type.Default

    init {
        xyz = "sss"
        xyz123 = "023 09230ß"
        test = Tag("name", Brand.colors.blue)
        nullablePomodoroType = Pomodoro.Type.Debug
        pomodoroType = Pomodoro.Type.Pro
        val callback: (Event) -> Unit = when (disableOnEscape) {
            true -> {
                {
                    val pressed = (it as KeyboardEvent).key
                    if (pressed == key) toggle(false)
                    else if (pressed.equals("Escape", ignoreCase = true)) toggle(true)
                }
            }
            else -> {
                { if ((it as KeyboardEvent).key == key) toggle(false) }
            }
        }
        eventTarget.addEventListener("keydown", callback)
        if (active) toggle(false)
    }

    private fun toggle(escapePressed: Boolean) {
        rootAndComposition = when (val current = rootAndComposition) {
            null -> {
                if (escapePressed) return
                logger.debug("Debug mode enabled")
                active = true
                val body = checkNotNull(document.body) { "missing body tag" }
                val root = document.createElement("div") as HTMLDivElement
                val composition = renderComposable(root = root, content = content)
                body.append(root)
                root to composition
            }
            else -> {
                logger.debug("Debug mode disabled")
                active = false
                current.second.dispose()
                checkNotNull(current.first.parentElement) { "missing root container" }.removeChild(current.first)
                null
            }
        }
    }
}
