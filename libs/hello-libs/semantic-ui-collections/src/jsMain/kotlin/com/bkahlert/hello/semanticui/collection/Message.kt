package com.bkahlert.hello.semanticui.collection

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.core.attributes.Modifier
import com.bkahlert.hello.semanticui.core.attributes.SemanticAttrsScope
import com.bkahlert.hello.semanticui.core.attributes.Variation
import com.bkahlert.hello.semanticui.core.attributes.classNames
import com.bkahlert.hello.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.hello.semanticui.core.dom.SemanticDivElement
import com.bkahlert.hello.semanticui.core.dom.SemanticElement
import com.bkahlert.hello.semanticui.core.dom.SemanticElementScope
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLDivElement

public interface MessageElement : SemanticElement<HTMLDivElement>

/** [Variation.Floating](https://semantic-ui.com/collections/message.html#floating) */
public fun SemanticAttrsScope<MessageElement>.v(value: Variation.Floating): Unit = variation(value)

/** [Variation.Compact](https://semantic-ui.com/collections/message.html#compact) */
public fun SemanticAttrsScope<MessageElement>.v(value: Variation.Compact): Unit = variation(value)

/** [Variation.Attached](https://semantic-ui.com/collections/message.html#attached) */
public fun SemanticAttrsScope<MessageElement>.v(value: Variation.Attached): Unit = variation(value)

/** [Variation.Warning](https://semantic-ui.com/collections/message.html#warning) */
public fun SemanticAttrsScope<MessageElement>.v(value: Variation.Warning): Unit = variation(value)

/** [Variation.Info](https://semantic-ui.com/collections/message.html#info) */
public fun SemanticAttrsScope<MessageElement>.v(value: Variation.Info): Unit = variation(value)

/** [Variation.Positive](https://semantic-ui.com/collections/message.html#positive--success) */
public fun SemanticAttrsScope<MessageElement>.v(value: Variation.Positive): Unit = variation(value)

/** [Variation.Success](https://semantic-ui.com/collections/message.html#positive--success) */
public fun SemanticAttrsScope<MessageElement>.v(value: Variation.Success): Unit = variation(value)

/** [Variation.Negative](https://semantic-ui.com/collections/message.html#negative--error) */
public fun SemanticAttrsScope<MessageElement>.v(value: Variation.Negative): Unit = variation(value)

/** [Variation.Error](https://semantic-ui.com/collections/message.html#negative--error) */
public fun SemanticAttrsScope<MessageElement>.v(value: Variation.Error): Unit = variation(value)

/** [Variation.Colored](https://semantic-ui.com/collections/message.html#colored) */
public fun SemanticAttrsScope<MessageElement>.v(value: Variation.Colored): Unit = variation(value)

/** [Variation.Size](https://semantic-ui.com/collections/message.html#size) */
public fun SemanticAttrsScope<MessageElement>.v(value: Variation.Size): Unit = variation(value)

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
        classes(*modifiers.classNames, "header")
    }, content)
}
