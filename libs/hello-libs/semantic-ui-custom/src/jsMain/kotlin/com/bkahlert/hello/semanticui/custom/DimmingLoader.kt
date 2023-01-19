package com.bkahlert.hello.semanticui.custom

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.core.attributes.Variation
import com.bkahlert.hello.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.hello.semanticui.element.Loader
import com.bkahlert.hello.semanticui.element.LoaderElement
import com.bkahlert.hello.semanticui.element.TextLoader
import com.bkahlert.hello.semanticui.module.Dimmer
import com.bkahlert.hello.semanticui.module.DimmerElement

/**
 * A [Loader] that-if [active]-uses a [Dimmer] to dim the content
 * of its parent element. If [loaderContent] is specified a [TextLoader]
 * is created instead.
 */
@Composable
public fun DimmingLoader(
    active: Boolean,
    dimmerAttrs: SemanticAttrBuilderContext<DimmerElement>?,
    loaderAttrs: SemanticAttrBuilderContext<LoaderElement>?,
    loaderContent: SemanticContentBuilder<LoaderElement>? = null,
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
 * of its parent element. If [loaderContent] is specified a [TextLoader]
 * is created instead.
 */
@Composable
public fun DimmingLoader(
    active: Boolean,
    loaderAttrs: SemanticAttrBuilderContext<LoaderElement>? = { +Variation.Size.Mini },
    loaderContent: SemanticContentBuilder<LoaderElement>? = null,
) {
    DimmingLoader(
        active = active,
        dimmerAttrs = { +Inverted },
        loaderAttrs = loaderAttrs,
        loaderContent = loaderContent,
    )
}
