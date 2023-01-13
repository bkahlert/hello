package com.bkahlert.hello.ui

import androidx.compose.runtime.Composable
import com.bkahlert.hello.SimpleLogger
import com.bkahlert.hello.semanticui.collection.Header
import com.bkahlert.hello.semanticui.collection.Message
import com.bkahlert.hello.semanticui.collection.MessageElement
import com.bkahlert.hello.semanticui.collection.MessageElementType.Error
import com.bkahlert.hello.semanticui.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.dom.SemanticContentBuilder
import com.bkahlert.hello.semanticui.module.Accordion
import com.bkahlert.hello.semanticui.module.Dropdown
import org.jetbrains.compose.web.dom.Pre
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement

private val logger = SimpleLogger("error-message-test")


@Composable
fun ErrorMessage(
    throwable: Throwable,
    attrs: SemanticAttrBuilderContext<MessageElement, HTMLDivElement>? = null,
) {
    ErrorMessage(attrs) {
        logger.error("An error occurred", throwable)

        Header {
            Text(throwable.errorMessage)
        }
        Accordion(throwable) {
            Dropdown("Detailed Error") {
                Pre(it) {
                    Text(throwable.stackTraceToString())
                }
            }
        }
    }
}

@Composable
fun ErrorMessage(
    attrs: SemanticAttrBuilderContext<MessageElement, HTMLDivElement>? = null,
    content: SemanticContentBuilder<MessageElement, HTMLDivElement>? = null,
) {
    Message(Error, attrs, content)
}

val Throwable.errorMessage: String get() = message ?: toString()
