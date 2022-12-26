package com.bkahlert.hello.semanticui.collection

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.Modifier
import com.bkahlert.hello.semanticui.SemanticAttrBuilder
import com.bkahlert.hello.semanticui.SemanticBuilder
import com.bkahlert.hello.semanticui.SemanticDivElement
import com.bkahlert.hello.semanticui.SemanticElement
import com.bkahlert.hello.semanticui.SemanticElementScope
import com.bkahlert.hello.semanticui.SemanticElementType
import com.bkahlert.hello.semanticui.classNames
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLDivElement

interface MessageElement : SemanticElement
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
    attrs: SemanticAttrBuilder<MessageElement, HTMLDivElement>? = null,
    content: SemanticBuilder<MessageElement, HTMLDivElement>? = null,
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
    attrs: SemanticAttrBuilder<MessageElement, HTMLDivElement>? = null,
    content: SemanticBuilder<MessageElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        classes("message")
    }, content)
}

@Suppress("unused")
@Composable
fun SemanticElementScope<MessageElement, *>.Header(
    vararg modifiers: Modifier,
    content: ContentBuilder<HTMLDivElement>? = null,
) {
    Div({
        classes(*modifiers.classNames, "header")
    }, content)
}
