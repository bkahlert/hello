package com.bkahlert.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.attributes.Modifier.State.Active
import com.bkahlert.semanticui.core.attributes.Modifier.State.Disabled
import com.bkahlert.semanticui.core.attributes.Modifier.State.Indeterminate
import com.bkahlert.semanticui.core.attributes.Modifier.Variation
import com.bkahlert.semanticui.core.attributes.StatesScope
import com.bkahlert.semanticui.core.attributes.VariationsScope
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticDivElement
import com.bkahlert.semanticui.core.dom.SemanticElement
import org.w3c.dom.HTMLDivElement

public interface LoaderElement : SemanticElement<HTMLDivElement>

/** [State.Indeterminate](https://semantic-ui.com/elements/loader.html#indeterminate) */
public fun StatesScope<LoaderElement>.indeterminate(): StatesScope<LoaderElement> = +Indeterminate

/** [State.Active](https://semantic-ui.com/elements/loader.html#active) */
public fun StatesScope<LoaderElement>.active(): StatesScope<LoaderElement> = +Active

/** [State.Disabled](https://semantic-ui.com/elements/loader.html#disabled) */
public fun StatesScope<LoaderElement>.disabled(): StatesScope<LoaderElement> = +Disabled

/** [Variation.Inline](https://semantic-ui.com/elements/loader.html#inline) */
public fun VariationsScope<LoaderElement>.inline(): VariationsScope<LoaderElement> = +Variation.Inline

/** [Variation.InlineCenter](https://semantic-ui.com/elements/loader.html#inline-center) */
public fun VariationsScope<LoaderElement>.inlineCenter(): VariationsScope<LoaderElement> = +Variation.InlineCenter

/** [Variation.Size](https://semantic-ui.com/elements/loader.html#size) */
public fun VariationsScope<LoaderElement>.size(value: Variation.Size): VariationsScope<LoaderElement> = +value

/** [Variation.Inverted](https://semantic-ui.com/elements/loader.html#inverted) */
public fun VariationsScope<LoaderElement>.inverted(): VariationsScope<LoaderElement> = +Variation.Inverted

/**
 * Creates a [SemanticUI loader](https://semantic-ui.com/elements/loader.html).
 */
@Composable
public fun Loader(
    attrs: SemanticAttrBuilderContext<LoaderElement>? = null,
    content: SemanticContentBuilder<LoaderElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        classes("loader")
    }, content)
}


/**
 * Creates a [SemanticUI text loader](https://semantic-ui.com/elements/loader.html).
 */
@Composable
public fun TextLoader(
    attrs: SemanticAttrBuilderContext<LoaderElement>? = null,
    content: SemanticContentBuilder<LoaderElement>? = null,
) {
    Loader({
        attrs?.invoke(this)
        classes("text")
    }, content)
}
