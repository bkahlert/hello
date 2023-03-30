package com.bkahlert.semanticui.collection

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticDivElement
import com.bkahlert.semanticui.core.dom.SemanticElement
import com.bkahlert.semanticui.element.Icon
import org.w3c.dom.HTMLDivElement

public interface MessageElement : SemanticElement<HTMLDivElement>

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

/**
 * Creates a [SemanticUI icon message](https://semantic-ui.com/collections/message.html#icon-message).
 */
@Composable
public fun IconMessage(
    icon: String,
    attrs: SemanticAttrBuilderContext<MessageElement>? = null,
    content: SemanticContentBuilder<MessageElement>? = null,
) {
    Message({
        attrs?.invoke(this)
        classes("icon", "message")
    }) {
        Icon(icon)
        if (content != null) {
            S("content") {
                content()
            }
        }
    }
}
