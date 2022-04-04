package com.bkahlert.hello.ui

import androidx.compose.runtime.Composable
import com.bkahlert.hello.SimpleLogger
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.SemanticBuilder
import com.semanticui.compose.collection.Header
import com.semanticui.compose.collection.Message
import com.semanticui.compose.collection.MessageElement
import com.semanticui.compose.collection.MessageElementType.Error
import com.semanticui.compose.module.Accordion
import com.semanticui.compose.module.Dropdown
import org.jetbrains.compose.web.dom.Pre
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement

private val logger = SimpleLogger("error-message-test")


@Composable
fun ErrorMessage(
    throwable: Throwable,
    attrs: SemanticAttrBuilder<MessageElement, HTMLDivElement>? = null,
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
    attrs: SemanticAttrBuilder<MessageElement, HTMLDivElement>? = null,
    content: SemanticBuilder<MessageElement, HTMLDivElement>? = null,
) {
    Message(Error, attrs, content)
}

val Throwable.errorMessage: String get() = message ?: toString()
