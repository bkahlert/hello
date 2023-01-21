package com.bkahlert.semanticui.collection

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.attributes.Modifier
import com.bkahlert.semanticui.core.attributes.Modifier.Variation
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Attached
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Compact
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Error
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Floating
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Info
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Negative
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Positive
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Success
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Warning
import com.bkahlert.semanticui.core.attributes.VariationsScope
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticDivElement
import com.bkahlert.semanticui.core.dom.SemanticElement
import com.bkahlert.semanticui.core.dom.SemanticElementScope
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLDivElement

public interface MessageElement : SemanticElement<HTMLDivElement>

/** [Variation.Floating](https://semantic-ui.com/collections/message.html#floating) */
public fun VariationsScope<MessageElement>.floating(): VariationsScope<MessageElement> = +Floating

/** [Variation.Compact](https://semantic-ui.com/collections/message.html#compact) */
public fun VariationsScope<MessageElement>.compact(): VariationsScope<MessageElement> = +Compact

/** [Variation.Attached](https://semantic-ui.com/collections/message.html#attached) */
public fun VariationsScope<MessageElement>.attached(): VariationsScope<MessageElement> = +Attached

/** [Variation.Warning](https://semantic-ui.com/collections/message.html#warning) */
public fun VariationsScope<MessageElement>.warning(): VariationsScope<MessageElement> = +Warning

/** [Variation.Info](https://semantic-ui.com/collections/message.html#info) */
public fun VariationsScope<MessageElement>.info(): VariationsScope<MessageElement> = +Info

/** [Variation.Positive](https://semantic-ui.com/collections/message.html#positive--success) */
public fun VariationsScope<MessageElement>.positive(): VariationsScope<MessageElement> = +Positive

/** [Variation.Success](https://semantic-ui.com/collections/message.html#positive--success) */
public fun VariationsScope<MessageElement>.success(): VariationsScope<MessageElement> = +Success

/** [Variation.Negative](https://semantic-ui.com/collections/message.html#negative--error) */
public fun VariationsScope<MessageElement>.negative(): VariationsScope<MessageElement> = +Negative

/** [Variation.Error](https://semantic-ui.com/collections/message.html#negative--error) */
public fun VariationsScope<MessageElement>.error(): VariationsScope<MessageElement> = +Error

/** [Variation.Colored](https://semantic-ui.com/collections/message.html#colored) */
public fun VariationsScope<MessageElement>.colored(value: Variation.Colored): VariationsScope<MessageElement> = +value

/** [Variation.Size](https://semantic-ui.com/collections/message.html#size) */
public fun VariationsScope<MessageElement>.size(value: Variation.Size): VariationsScope<MessageElement> = +value

/**
 * Creates a [SemanticUI message](https://semantic-ui.com/collections/message.html).
 */
@Composable
public fun Message(
    attrs: SemanticAttrBuilderContext<MessageElement>? = null,
    content: SemanticContentBuilder<MessageElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        classes("message")
    }, content)
}

@Suppress("unused", "UnusedReceiverParameter")
@Composable
public fun SemanticElementScope<MessageElement>.Header(
    vararg modifiers: Modifier,
    content: ContentBuilder<HTMLDivElement>? = null,
) {
    Div({
        modifiers.forEach { classes(*it.classNames) }
        classes("header")
    }, content)
}
