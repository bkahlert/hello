package com.bkahlert.semanticui.module

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.attributes.Modifier.State.Active
import com.bkahlert.semanticui.core.attributes.Modifier.State.Disabled
import com.bkahlert.semanticui.core.attributes.Modifier.Variation
import com.bkahlert.semanticui.core.attributes.StatesScope
import com.bkahlert.semanticui.core.attributes.VariationsScope
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticDivElement
import com.bkahlert.semanticui.core.dom.SemanticElement
import com.bkahlert.semanticui.core.jQuery
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import kotlin.js.Json
import kotlin.js.json

public interface DimmerElement : SemanticElement<HTMLDivElement>
public interface DimmerContentElement : SemanticElement<HTMLDivElement>

/** [State.Active](https://semantic-ui.com/modules/dimmer.html#active) */
public fun StatesScope<DimmerElement>.active(): StatesScope<DimmerElement> = +Active

/** [State.Disabled](https://semantic-ui.com/modules/dimmer.html#disabled) */
public fun StatesScope<DimmerElement>.disabled(): StatesScope<DimmerElement> = +Disabled

/**
 * [Variation.Blurring](https://semantic-ui.com/modules/dimmer.html#blurring)
 * that needs to be applied to the [dimmable] content of a [Dimmer].
 * That is, the parent [SemanticElement] of the dimmer.
 */
public fun VariationsScope<SemanticElement<Element>>.blurring(): VariationsScope<SemanticElement<Element>> = +Variation.Blurring

/**
 * [Variation.Dimmable](https://semantic-ui.com/modules/dimmer.html#/usage)
 * that needs to be applied to the enclosing content of a [Dimmer] if [jQuery.dimmer] is invoked on the `.dimmer` and not on the `.dimmable`.
 */
public fun VariationsScope<SemanticElement<Element>>.dimmable(): VariationsScope<SemanticElement<Element>> = +Variation.Dimmable

/** [Variation.VerticallyAligned](https://semantic-ui.com/modules/dimmer.html#vertically-aligned) */
public fun VariationsScope<DimmerElement>.verticallyAligned(value: Variation.VerticallyAligned): VariationsScope<DimmerElement> = +value

// SimpleDimmer, https://semantic-ui.com/modules/dimmer.html#simple-dimmer

/**
 * [Variation.Inverted](https://semantic-ui.com/modules/dimmer.html#inverted)
 * that can be applied to the [Dimmer] itself and its content.
 */
public fun VariationsScope<SemanticElement<Element>>.inverted(): VariationsScope<SemanticElement<Element>> = +Variation.Inverted


private fun jQuery.dimmer(options: Json): jQuery =
    asDynamic().dimmer(options).unsafeCast<jQuery>()

public fun jQuery.dimmer(behavior: String, vararg args: Any?): jQuery =
    asDynamic().dimmer.apply(this, arrayOf(behavior, *args)).unsafeCast<jQuery>()

/**
 * An interface to interact with a [SemanticUI dimmer](https://semantic-ui.com/modules/dimmer.html)
 * using the specified [options].
 */
public fun jQuery.dimmer(vararg options: Pair<String, Any?>): jQuery = dimmer(json(*options))


/**
 * Creates a [SemanticUI dimmer](https://semantic-ui.com/modules/dimmer.html#dimmer) or
 * a [SemanticUI content dimmer](https://semantic-ui.com/modules/dimmer.html#content-dimmer) if [content] is specified.
 */
@Composable
public fun Dimmer(
    attrs: SemanticAttrBuilderContext<DimmerElement>? = null,
    content: SemanticContentBuilder<DimmerContentElement>? = null,
) {
    SemanticDivElement<DimmerElement>({
        classes("ui")
        attrs?.invoke(this)
        classes("dimmer")
    }) {
        if (content != null) {
            SemanticDivElement<DimmerContentElement>({ classes("content") }, content)
        }
    }
}

/**
 * Creates a [SemanticUI page dimmer](https://semantic-ui.com/modules/dimmer.html#page-dimmer).
 */
@Composable
public fun PageDimmer(
    attrs: SemanticAttrBuilderContext<DimmerElement>? = null,
    content: SemanticContentBuilder<DimmerContentElement>? = null,
) {
    Dimmer({
        classes("page")
        attrs?.invoke(this)
    }, content)
}
