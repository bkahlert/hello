package com.bkahlert.semanticui.custom

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.collection.Message
import com.bkahlert.semanticui.collection.MessageElement
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.element.Header
import com.bkahlert.semanticui.element.SubHeader
import com.bkahlert.semanticui.module.Accordion
import com.bkahlert.semanticui.module.Dropdown
import org.jetbrains.compose.web.css.textAlign
import org.jetbrains.compose.web.dom.Pre
import org.jetbrains.compose.web.dom.Text

@Composable
public fun ErrorMessage(
    message: String?,
    throwable: Throwable? = null,
    attrs: SemanticAttrBuilderContext<MessageElement>? = null,
    content: SemanticContentBuilder<MessageElement>? = null,
) {
    ErrorMessage(attrs) {
        when (message) {
            null -> throwable?.message?.also { Header { Text(it) } }
            else -> {
                Header { Text(message) }
                throwable?.message?.also { SubHeader { Text(it) } }
            }
        }
        content?.invoke(this)
        when (throwable) {
            null -> {}
            else -> Accordion {
                Dropdown("Stacktrace") {
                    Pre({
                        style {
                            textAlign("left")
                            textOverflow(whiteSpace = null)
                        }
                    }) {
                        Text(throwable.stackTraceToString())
                    }
                }
            }
        }
    }
}

@Composable
@Suppress("NOTHING_TO_INLINE") // = avoidance of unnecessary recomposition scope
public inline fun ErrorMessage(
    throwable: Throwable?,
    noinline attrs: SemanticAttrBuilderContext<MessageElement>? = null,
    noinline content: SemanticContentBuilder<MessageElement>? = null,
) {
    ErrorMessage(
        message = null,
        throwable = throwable,
        attrs = attrs,
        content = content,
    )
}

@Composable
public fun ErrorMessage(
    attrs: SemanticAttrBuilderContext<MessageElement>? = null,
    content: SemanticContentBuilder<MessageElement>? = null,
) {
    Message({
        +"floating"
        +"error"
        attrs?.invoke(this)
    }, content)
}
