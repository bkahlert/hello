package com.bkahlert.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.attributes.Modifier
import com.bkahlert.semanticui.core.attributes.Modifier.Variation
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Attached
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Basic
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Circular
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Compact
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Fluid
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.LabeledIcon
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Negative
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Positive
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Toggle
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Vertical
import com.bkahlert.semanticui.core.attributes.StatesScope
import com.bkahlert.semanticui.core.attributes.VariationsScope
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticDivElement
import com.bkahlert.semanticui.core.dom.SemanticElement
import org.jetbrains.compose.web.dom.A
import org.w3c.dom.Element
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLDivElement

public interface ButtonElement<out TElement : Element> : SemanticElement<TElement>
public interface ButtonDivElement : ButtonElement<HTMLDivElement>
public interface ButtonAnchorElement : SemanticElement<HTMLAnchorElement>

// SocialVariation

/** [State.Active](https://semantic-ui.com/elements/button.html#active) */
public fun StatesScope<ButtonElement<Element>>.active(): StatesScope<ButtonElement<Element>> = +Modifier.State.Active

/** [State.Disabled](https://semantic-ui.com/elements/button.html#disabled) */
public fun StatesScope<ButtonElement<Element>>.disabled(): StatesScope<ButtonElement<Element>> = +Modifier.State.Disabled

/** [State.Loading](https://semantic-ui.com/elements/button.html#loading) */
public fun StatesScope<ButtonElement<Element>>.loading(): StatesScope<ButtonElement<Element>> = +Modifier.State.Loading

/** [Variation.Size](https://semantic-ui.com/elements/button.html#size) */
public fun VariationsScope<ButtonElement<Element>>.size(value: Variation.Size): VariationsScope<ButtonElement<Element>> = +value

/** [Variation.Floated](https://semantic-ui.com/elements/button.html#floated) */
public fun VariationsScope<ButtonElement<Element>>.floated(value: Variation.Floated): VariationsScope<ButtonElement<Element>> = +value

/** [Variation.Colored](https://semantic-ui.com/elements/button.html#colored) */
public fun VariationsScope<ButtonElement<Element>>.colored(value: Variation.Colored): VariationsScope<ButtonElement<Element>> = +value

/** [Variation.Compact](https://semantic-ui.com/elements/button.html#compact) */
public fun VariationsScope<ButtonElement<Element>>.compact(): VariationsScope<ButtonElement<Element>> = +Compact

/** [Variation.Toggle](https://semantic-ui.com/elements/button.html#toggle) */
public fun VariationsScope<ButtonElement<Element>>.toggle(): VariationsScope<ButtonElement<Element>> = +Toggle

/** [Variation.Positive](https://semantic-ui.com/elements/button.html#positive) */
public fun VariationsScope<ButtonElement<Element>>.positive(): VariationsScope<ButtonElement<Element>> = +Positive

/** [Variation.Negative](https://semantic-ui.com/elements/button.html#negative) */
public fun VariationsScope<ButtonElement<Element>>.negative(): VariationsScope<ButtonElement<Element>> = +Negative

/** [Variation.Fluid](https://semantic-ui.com/elements/button.html#fluid) */
public fun VariationsScope<ButtonElement<Element>>.fluid(): VariationsScope<ButtonElement<Element>> = +Fluid

/** [Variation.Circular](https://semantic-ui.com/elements/button.html#circular) */
public fun VariationsScope<ButtonElement<Element>>.circular(): VariationsScope<ButtonElement<Element>> = +Circular

/** [Variation.Attached.VerticallyAttached](https://semantic-ui.com/elements/button.html#vertically-attached) */
public fun VariationsScope<ButtonElement<Element>>.verticallyAttached(value: Attached.VerticallyAttached): VariationsScope<ButtonElement<Element>> = +value

/** [Variation.Attached.HorizontallyAttached](https://semantic-ui.com/elements/button.html#horizontally-attached) */
public fun VariationsScope<ButtonElement<Element>>.horizontallyAttached(value: Attached.HorizontallyAttached): VariationsScope<ButtonElement<Element>> = +value


/**
 * Creates a [SemanticUI button](https://semantic-ui.com/elements/button.html).
 */
@Composable
public fun Button(
    attrs: SemanticAttrBuilderContext<ButtonDivElement>? = null,
    content: SemanticContentBuilder<ButtonDivElement>? = null,
): Unit = SemanticDivElement({
    classes("ui")
    attrs?.invoke(this)
    classes("button")
}, content)

/**
 * Creates a [SemanticUI primary button](https://semantic-ui.com/elements/button.html#emphasis).
 */
@Composable
public fun PrimaryButton(
    attrs: SemanticAttrBuilderContext<ButtonDivElement>? = null,
    content: SemanticContentBuilder<ButtonDivElement>? = null,
): Unit = Button({
    classes("primary")
    attrs?.invoke(this)
}, content)

/**
 * Creates a [SemanticUI secondary button](https://semantic-ui.com/elements/button.html#emphasis).
 */
@Composable
public fun SecondaryButton(
    attrs: SemanticAttrBuilderContext<ButtonDivElement>? = null,
    content: SemanticContentBuilder<ButtonDivElement>? = null,
): Unit = Button({
    classes("secondary")
    attrs?.invoke(this)
}, content)

/**
 * Creates a [SemanticUI icon button](https://semantic-ui.com/elements/button.html#icon).
 */
@Composable
public fun IconButton(
    attrs: SemanticAttrBuilderContext<ButtonDivElement>? = null,
    content: SemanticContentBuilder<ButtonDivElement>? = null,
): Unit = Button({
    classes("icon")
    attrs?.invoke(this)
}, content)

/**
 * Creates a [SemanticUI labeled icon button](https://semantic-ui.com/elements/button.html#icon).
 */
@Composable
public fun LabeledIconButton(
    attrs: SemanticAttrBuilderContext<ButtonDivElement>? = null,
    content: SemanticContentBuilder<ButtonDivElement>? = null,
): Unit = Button({
    classes("labeled", "icon")
    attrs?.invoke(this)
}, content)

/**
 * Creates a [SemanticUI basic button](https://semantic-ui.com/elements/button.html#basic).
 */
@Composable
public fun BasicButton(
    attrs: SemanticAttrBuilderContext<ButtonDivElement>? = null,
    content: SemanticContentBuilder<ButtonDivElement>? = null,
): Unit = Button({
    classes("basic")
    attrs?.invoke(this)
}, content)

/** Creates a [SemanticUI inverted button](https://semantic-ui.com/elements/button.html#inverted). */
@Composable
public fun InvertedButton(
    attrs: SemanticAttrBuilderContext<ButtonDivElement>? = null,
    content: SemanticContentBuilder<ButtonDivElement>? = null,
): Unit = Button({
    classes("inverted")
    attrs?.invoke(this)
}, content)


/**
 * Creates a [SemanticUI button](https://semantic-ui.com/elements/button.html)
 * based on an anchor tag.
 */
@Composable
public fun AnkerButton(
    href: String? = null,
    attrs: SemanticAttrBuilderContext<ButtonAnchorElement>? = null,
    content: SemanticContentBuilder<ButtonAnchorElement>? = null,
) {
    SemanticElement({
        classes("ui")
        attrs?.invoke(this)
        classes("button")
    }, content) { a, c -> A(href, a, c) }
}


public interface ButtonGroupElement : SemanticElement<HTMLDivElement>

/** [Variation.Vertical](https://semantic-ui.com/elements/button.html#vertical-buttons) */
public fun VariationsScope<ButtonGroupElement>.vertical(): VariationsScope<ButtonGroupElement> = +Vertical

/** [Variation.Icon](https://semantic-ui.com/elements/button.html#icon-buttons) */
public fun VariationsScope<ButtonGroupElement>.icon(): VariationsScope<ButtonGroupElement> = +Variation.Icon

/** [Variation.LabeledIcon](https://semantic-ui.com/elements/button.html#labeled-icon-buttons) */
public fun VariationsScope<ButtonGroupElement>.labeledIcon(): VariationsScope<ButtonGroupElement> = +LabeledIcon

// Equal
/** [Variation.Colored](https://semantic-ui.com/elements/button.html#colored-buttons) */
public fun VariationsScope<ButtonGroupElement>.colored(value: Variation.Colored): VariationsScope<ButtonGroupElement> = +value

/** [Variation.Basic](https://semantic-ui.com/elements/button.html#basic-buttons) */
public fun VariationsScope<ButtonGroupElement>.basic(): VariationsScope<ButtonGroupElement> = +Basic

/** [Variation.Size](https://semantic-ui.com/elements/button.html#group-sizes) */
public fun VariationsScope<ButtonGroupElement>.size(value: Variation.Size): VariationsScope<ButtonGroupElement> = +value

/**
 * Creates a [SemanticUI button group](https://semantic-ui.com/elements/button.html#buttons).
 */
@Composable
public fun Buttons(
    attrs: SemanticAttrBuilderContext<ButtonGroupElement>? = null,
    content: SemanticContentBuilder<ButtonGroupElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        classes("buttons")
    }, content)
}
