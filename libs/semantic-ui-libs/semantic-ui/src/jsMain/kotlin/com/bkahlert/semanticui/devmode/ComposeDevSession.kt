package com.bkahlert.semanticui.devmode

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import com.bkahlert.kommons.binding.DevMode
import com.bkahlert.kommons.binding.DevSession
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
    private val composition: Composition = renderComposable(root = root, content = content)

    public override fun dispose() {
        composition.dispose()
        val parentElement = checkNotNull(root.parentElement) { "missing root container" }
        parentElement.removeChild(root)
    }
}
