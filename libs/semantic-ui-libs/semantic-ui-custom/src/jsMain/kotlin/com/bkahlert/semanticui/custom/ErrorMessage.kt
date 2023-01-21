package com.bkahlert.semanticui.custom

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.collection.Header
import com.bkahlert.semanticui.collection.Message
import com.bkahlert.semanticui.collection.MessageElement
import com.bkahlert.semanticui.collection.error
import com.bkahlert.semanticui.collection.floating
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.module.Accordion
import com.bkahlert.semanticui.module.Dropdown
import org.jetbrains.compose.web.dom.Pre
import org.jetbrains.compose.web.dom.Text


@Composable
public fun ErrorMessage(
    throwable: Throwable,
    attrs: SemanticAttrBuilderContext<MessageElement>? = null,
) {
    ErrorMessage(attrs) {
        console.error("An error occurred", throwable)

        Header {
            Text(throwable.errorMessage)
        }
        Accordion(throwable) {
            Dropdown("Detailed Error") {
                Pre {
                    Text(throwable.stackTraceToString())
                }
            }
        }
    }
}

@Composable
public fun ErrorMessage(
    attrs: SemanticAttrBuilderContext<MessageElement>? = null,
    content: SemanticContentBuilder<MessageElement>? = null,
) {
    Message({
        v.floating()
        v.error()
        attrs?.invoke(this)
    }, content)
}

public val Throwable.errorMessage: String get() = message ?: toString()
