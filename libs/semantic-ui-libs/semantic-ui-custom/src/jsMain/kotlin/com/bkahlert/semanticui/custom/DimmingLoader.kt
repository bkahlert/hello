package com.bkahlert.semanticui.custom

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.element.Loader
import com.bkahlert.semanticui.element.LoaderElement
import com.bkahlert.semanticui.element.TextLoader
import com.bkahlert.semanticui.module.Dimmer
import com.bkahlert.semanticui.module.DimmerElement
import com.bkahlert.semanticui.module.active
import com.bkahlert.semanticui.module.inverted

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
        if (active) s.active()
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
    loaderAttrs: SemanticAttrBuilderContext<LoaderElement>? = null,
    loaderContent: SemanticContentBuilder<LoaderElement>? = null,
) {
    DimmingLoader(
        active = active,
        dimmerAttrs = { v.inverted() },
        loaderAttrs = loaderAttrs,
        loaderContent = loaderContent,
    )
}
