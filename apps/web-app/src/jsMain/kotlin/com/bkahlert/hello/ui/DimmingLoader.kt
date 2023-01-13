package com.bkahlert.hello.ui

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.dom.SemanticContentBuilder
import com.bkahlert.hello.semanticui.element.Loader
import com.bkahlert.hello.semanticui.element.LoaderElement
import com.bkahlert.hello.semanticui.element.TextLoader
import com.bkahlert.hello.semanticui.module.Dimmer
import com.bkahlert.hello.semanticui.module.DimmerElement
import org.w3c.dom.HTMLDivElement

/**
 * A [Loader] that-if [active]-uses a [Dimmer] to dim the content
 * of its parent element. If [loaderContent] is specified a [TextLoader]
 * is created instead.
 */
@Composable
fun DimmingLoader(
    active: Boolean,
    dimmerAttrs: SemanticAttrBuilderContext<DimmerElement, HTMLDivElement>? = { +Inverted },
    loaderAttrs: SemanticAttrBuilderContext<LoaderElement, HTMLDivElement>? = { +Size.Mini },
    loaderContent: SemanticContentBuilder<LoaderElement, HTMLDivElement>? = null,
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
    attrs: SemanticAttrBuilderContext<LoaderElement, HTMLDivElement>? = { +Size.Mini },
    content: SemanticContentBuilder<LoaderElement, HTMLDivElement>? = null,
) {
    DimmingLoader(
        active = active,
        loaderAttrs = attrs,
        loaderContent = content,
    )
}
