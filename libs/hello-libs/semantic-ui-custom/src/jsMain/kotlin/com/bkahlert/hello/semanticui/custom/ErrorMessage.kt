package com.bkahlert.hello.ui

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.collection.Header
import com.bkahlert.hello.semanticui.collection.Message
import com.bkahlert.hello.semanticui.collection.MessageElement
import com.bkahlert.hello.semanticui.collection.MessageElementType.Error
import com.bkahlert.hello.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.hello.semanticui.module.Accordion
import com.bkahlert.hello.semanticui.module.Dropdown
import com.bkahlert.kommons.logging.InlineLogging
import org.jetbrains.compose.web.dom.Pre
import org.jetbrains.compose.web.dom.Text

private val logger by InlineLogging


@Composable
public fun ErrorMessage(
    throwable: Throwable,
    attrs: SemanticAttrBuilderContext<MessageElement>? = null,
) {
    ErrorMessage(attrs) {
        logger.error("An error occurred", throwable)

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
    Message(Error, attrs, content)
}

public val Throwable.errorMessage: String get() = message ?: toString()
