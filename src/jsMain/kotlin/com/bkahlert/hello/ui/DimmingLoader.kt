package com.bkahlert.hello.ui

import androidx.compose.runtime.Composable
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.SemanticBuilder
import com.semanticui.compose.element.Loader
import com.semanticui.compose.element.LoaderElement
import com.semanticui.compose.element.TextLoader
import com.semanticui.compose.module.Dimmer
import com.semanticui.compose.module.DimmerElement
import org.w3c.dom.HTMLDivElement

/**
 * A [Loader] that-if [active]-uses a [Dimmer] to dim the content
 * of its parent element. If [loaderContent] is specified a [TextLoader]
 * is created instead.
 */
@Composable
fun DimmingLoader(
    active: Boolean,
    dimmerAttrs: SemanticAttrBuilder<DimmerElement, HTMLDivElement>? = { +Inverted },
    loaderAttrs: SemanticAttrBuilder<LoaderElement, HTMLDivElement>? = { +Size.Mini },
    loaderContent: SemanticBuilder<LoaderElement, HTMLDivElement>? = null,
) {
    Dimmer({
        dimmerAttrs?.invoke(this)
        if (active) +Active
    }) {
        if (loaderContent != null) TextLoader(loaderAttrs, loaderContent)
        else Loader(loaderAttrs)
    }
}

/**
 * A [Loader] that-if [active]-uses a [Dimmer] to dim the content
 * of its parent element. If [content] is specified a [TextLoader]
 * is created instead.
 */
@Composable
fun DimmingLoader(
    active: Boolean,
    attrs: SemanticAttrBuilder<LoaderElement, HTMLDivElement>? = { +Size.Mini },
    content: SemanticBuilder<LoaderElement, HTMLDivElement>? = null,
) {
    DimmingLoader(
        active = active,
        loaderAttrs = attrs,
        loaderContent = content,
    )
}
