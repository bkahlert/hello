package com.bkahlert.semanticui.devmode

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import com.bkahlert.kommons.devmode.DevMode
import com.bkahlert.kommons.devmode.DevSession
import com.bkahlert.kommons.js.ConsoleLogging
import org.jetbrains.compose.web.dom.DOMScope
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLDivElement

/**
 * A [DevSession] that renders [content] using an independent [Composition]
 * managing [root].
 */
public open class ComposeDevSession(
    /** The element the active [DevMode] lives in. */
    public val root: HTMLDivElement,
    /** The composable to be rendered. */
    public val content: @Composable DOMScope<HTMLDivElement>.() -> Unit,
) : DevSession {

    private val logger by ConsoleLogging

    init {
        logger.info("Starting composition in", root)
    }

    private val composition: Composition = renderComposable(root = root, content = content)

    public override fun dispose() {
        kotlin.runCatching {
            logger.debug("Disposing composition", composition.toString(), "in", root)
            composition.dispose()
            logger.debug("Disposing composition container element", root)
            root.remove()
            logger.info("Disposed composition in", root)
        }.onFailure {
            logger.error("Failed to dispose composition")
        }
    }

    override fun toString(): String =
        "ComposeDevSession(root=${root.hashCode()}, hasInvalidations=${composition.hasInvalidations}, isDisposed=${composition.isDisposed})"
}
