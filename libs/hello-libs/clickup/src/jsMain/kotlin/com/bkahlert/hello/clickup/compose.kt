package com.bkahlert.hello.clickup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import com.bkahlert.kommons.js.ConsoleLogger
import com.bkahlert.kommons.js.grouping
import dev.fritz2.core.Tag
import org.jetbrains.compose.web.dom.DOMScope
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.Element

private val logger = ConsoleLogger("Compose for Web in Fritz2")

/**
 * Renders a [Composition] in this [Tag]
 * and disposes it when the [Tag] is unmounted.
 */
public fun <E : Element> Tag<E>.compose(
    /** The composable to be rendered. */
    content: @Composable DOMScope<E>.() -> Unit,
) {
    val composition: Composition = logger.grouping("composition start", this) {
        renderComposable(root = domNode, content = content)
    }
    job.invokeOnCompletion {
        logger.grouping("composition disposition", composition) {
            composition.dispose()
        }
    }
}
