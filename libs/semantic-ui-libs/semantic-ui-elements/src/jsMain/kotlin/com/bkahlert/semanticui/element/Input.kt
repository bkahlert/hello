package com.bkahlert.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.attributes.Modifier
import com.bkahlert.semanticui.core.attributes.Modifier.Variation
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Fluid
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Inverted
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Labeled
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Transparent
import com.bkahlert.semanticui.core.attributes.StatesScope
import com.bkahlert.semanticui.core.attributes.VariationsScope
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticDivElement
import com.bkahlert.semanticui.core.dom.SemanticElement
import org.w3c.dom.HTMLDivElement

public interface InputElement : SemanticElement<HTMLDivElement>

/** [State.Focus](https://semantic-ui.com/elements/input.html#focus) */
public fun StatesScope<InputElement>.focus(): StatesScope<InputElement> = +Modifier.State.Focus

/** [State.Loading](https://semantic-ui.com/elements/input.html#loading) */
public fun StatesScope<InputElement>.loading(): StatesScope<InputElement> = +Modifier.State.Loading

/** [State.Disabled](https://semantic-ui.com/elements/input.html#disabled) */
public fun StatesScope<InputElement>.disabled(): StatesScope<InputElement> = +Modifier.State.Disabled

/** [State.Error](https://semantic-ui.com/elements/input.html#error) */
public fun StatesScope<InputElement>.error(): StatesScope<InputElement> = +Modifier.State.Error

/** [Variation.Icon](https://semantic-ui.com/elements/input.html#icon) */
public fun VariationsScope<InputElement>.icon(value: Variation.Icon = Variation.Icon): VariationsScope<InputElement> = +value

/** [Variation.Labeled](https://semantic-ui.com/elements/input.html#labeled) */
public fun VariationsScope<InputElement>.labeled(): VariationsScope<InputElement> = +Labeled

/** [Variation.Action](https://semantic-ui.com/elements/input.html#action) */
public fun VariationsScope<InputElement>.action(value: Variation.Action = Variation.Action): VariationsScope<InputElement> = +value

/** [Variation.Transparent](https://semantic-ui.com/elements/input.html#transparent) */
public fun VariationsScope<InputElement>.transparent(): VariationsScope<InputElement> = +Transparent

/** [Variation.Inverted](https://semantic-ui.com/elements/input.html#inverted) */
public fun VariationsScope<InputElement>.inverted(): VariationsScope<InputElement> = +Inverted

/** [Variation.Fluid](https://semantic-ui.com/elements/input.html#fluid) */
public fun VariationsScope<InputElement>.fluid(): VariationsScope<InputElement> = +Fluid

/** [Variation.Size](https://semantic-ui.com/elements/input.html#size) */
public fun VariationsScope<InputElement>.size(value: Variation.Size): VariationsScope<InputElement> = +value


/** Creates a [SemanticUI input](https://semantic-ui.com/elements/input.html). */
@Composable
public fun Input(
    attrs: SemanticAttrBuilderContext<InputElement>? = null,
    content: SemanticContentBuilder<InputElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        classes("input")
    }, content)
}
