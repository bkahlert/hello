package com.bkahlert.hello.semanticui.collection

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.attributes.Modifier
import com.bkahlert.hello.semanticui.attributes.classNames
import com.bkahlert.hello.semanticui.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.dom.SemanticContentBuilder
import com.bkahlert.hello.semanticui.dom.SemanticDivElement
import com.bkahlert.hello.semanticui.dom.SemanticElement
import com.bkahlert.hello.semanticui.dom.SemanticElementScope
import com.bkahlert.hello.semanticui.dom.SemanticElementType
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLDivElement

interface MessageElement : SemanticElement<HTMLDivElement>
enum class MessageElementType(override vararg val classNames: String) : SemanticElementType<MessageElement> {
    Warning("warning"),
    Info("info"),
    Positive("positive"),
    Success("success"),
    Negative("negative"),
    Error("error"),
}

/**
 * Creates a [SemanticUI message](https://semantic-ui.com/collections/message.html).
 */
@Composable
fun Message(
    type: MessageElementType?,
    attrs: SemanticAttrBuilderContext<MessageElement>? = null,
    content: SemanticContentBuilder<MessageElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        type?.invoke(this)
        classes("message")
    }, content)
}

/**
 * Creates a [SemanticUI message](https://semantic-ui.com/collections/message.html).
 */
@Composable
fun Message(
    attrs: SemanticAttrBuilderContext<MessageElement>? = null,
    content: SemanticContentBuilder<MessageElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        classes("message")
    }, content)
}

@Suppress("unused")
@Composable
fun SemanticElementScope<MessageElement>.Header(
    vararg modifiers: Modifier,
    content: ContentBuilder<HTMLDivElement>? = null,
) {
    Div({
        classes(*modifiers.classNames, "header")
    }, content)
}
