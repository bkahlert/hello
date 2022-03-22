package com.bkahlert.hello

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import com.bkahlert.hello.SimpleLogger.Companion.simpleLogger
import kotlinx.browser.document
import org.jetbrains.compose.web.dom.DOMScope
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.EventTarget
import org.w3c.dom.events.KeyboardEvent

/**
 * Debug mode that can render a composable on activation.
 */
object DebugMode {
    private val logger = simpleLogger()

    private var content: @Composable DOMScope<HTMLDivElement>.() -> Unit = {}
    private var rootAndComposition: Pair<HTMLDivElement, Composition>? = null

    private fun toggle() {
        rootAndComposition = when (val current = rootAndComposition) {
            null -> {
                logger.debug("Debug mode enabled")
                val body = checkNotNull(document.body) { "missing body tag" }
                val root = document.createElement("div") as HTMLDivElement
                val composition = renderComposable(root = root, content = content)
                body.append(root)
                root to composition
            }
            else -> {
                logger.debug("Debug mode disabled")
                current.second.dispose()
                checkNotNull(current.first.parentElement) { "missing root container" }.removeChild(current.first)
                null
            }
        }
    }

    /**
     * Sets up the debug mode to listen to `keydown` events with the specified [key]
     * on the specified [eventTarget] rendering a [Composable] which uses the specified [content].
     */
    fun enable(
        eventTarget: EventTarget = document,
        key: String = "F4",
        content: @Composable DOMScope<HTMLDivElement>.() -> Unit,
    ) {
        this.content = content
        eventTarget.addEventListener("keydown", {
            if ((it as KeyboardEvent).key == key) toggle()
        })
    }
}
