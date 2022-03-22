package com.semanticui.compose.collection

import androidx.compose.runtime.Composable
import com.semanticui.compose.Modifier
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.SemanticBuilder
import com.semanticui.compose.SemanticDivElement
import com.semanticui.compose.SemanticElement
import com.semanticui.compose.SemanticElementScope
import com.semanticui.compose.SemanticElementType
import com.semanticui.compose.classNames
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