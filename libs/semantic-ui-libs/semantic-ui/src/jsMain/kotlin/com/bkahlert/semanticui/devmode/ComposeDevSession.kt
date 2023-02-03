package com.bkahlert.semanticui.devmode

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import com.bkahlert.kommons.devmode.DevMode
import com.bkahlert.kommons.devmode.DevSession
import com.bkahlert.kommons.js.debug
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

    init {
        console.info("Starting composition in", root)
    }

    private val composition: Composition = renderComposable(root = root, content = content)

    public override fun dispose() {
        kotlin.runCatching {
            console.debug("ComposeDevSession: Disposing composition", composition.toString(), "in", root)
            composition.dispose()
            console.debug("ComposeDevSession: Disposing composition container element", root)
            root.remove()
            console.info("ComposeDevSession: Disposed composition in", root)
        }.onFailure {
            console.error("ComposeDevSession: Failed to dispose composition")
        }
    }

    override fun toString(): String =
        "ComposeDevSession(root=${root.hashCode()}, hasInvalidations=${composition.hasInvalidations}, isDisposed=${composition.isDisposed})"
}
