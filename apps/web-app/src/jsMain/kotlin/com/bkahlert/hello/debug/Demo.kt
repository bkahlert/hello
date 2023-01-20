package com.bkahlert.hello.debug

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.clickup.model.ClickUpException
import com.bkahlert.semanticui.collection.Header
import com.bkahlert.semanticui.collection.LinkItem
import com.bkahlert.semanticui.collection.Menu
import com.bkahlert.semanticui.collection.TextMenu
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.attributes.Variation.Attached
import com.bkahlert.semanticui.core.attributes.Variation.Size.Small
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticElement
import com.bkahlert.semanticui.element.Header
import com.bkahlert.semanticui.element.Icon
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.marginBottom
import org.jetbrains.compose.web.css.marginTop
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement

@Composable
fun Demos(
    name: String,
    attrs: SemanticAttrBuilderContext<SemanticElement<HTMLDivElement>>? = null,
    content: SemanticContentBuilder<SemanticElement<HTMLDivElement>>? = null,
) {
    S("ui", "segments", "raised", attrs = attrs) {
        Header({
            +Attached.Top
            +Inverted
            style { property("border-bottom-width", "0") }
        }) { Text(name) }
        content?.invoke(this)
    }
}

@Composable
fun Demo(
    name: String,
    attrs: SemanticAttrBuilderContext<SemanticElement<HTMLDivElement>>? = null,
    content: SemanticContentBuilder<SemanticElement<HTMLDivElement>>? = null,
) {
    var dirty by remember { mutableStateOf(false) }
    S("ui", "segment", attrs = attrs) {
        if (!dirty) {
            TextMenu({
                +Small
                style { marginTop((-1).em); marginBottom(0.em) }
            }) {
                Header { Text(name) }
                Menu({ +Direction.Right + Small }) {
                    LinkItem({
                        onClick { dirty = true }
                    }) {
                        Icon("redo", "alternate")
                        Text("Reset")
                    }
                }
            }
            content?.invoke(this)
        } else {
            Text("Resetting")
            dirty = false
        }
    }
}

val clickupException = ClickUpException(
    "something went wrong", "TEST-1234", RuntimeException("underlying problem")
)

fun <T> response(value: T) = Result.success(value)
fun <T> failedResponse(exception: Throwable = clickupException) = Result.failure<T>(exception)
