package com.bkahlert.hello.debug

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.clickup.model.ClickUpException
import com.bkahlert.hello.semanticui.collection.Header
import com.bkahlert.hello.semanticui.collection.LinkItem
import com.bkahlert.hello.semanticui.collection.Menu
import com.bkahlert.hello.semanticui.collection.TextMenu
import com.bkahlert.hello.semanticui.core.S
import com.bkahlert.hello.semanticui.core.attributes.Variation
import com.bkahlert.hello.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.hello.semanticui.core.dom.SemanticElement
import com.bkahlert.hello.semanticui.element.Header
import com.bkahlert.hello.semanticui.element.Icon
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
            +Variation.Attached.Top
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
                +Variation.Size.Small
                style { marginTop((-1).em); marginBottom(0.em) }
            }) {
                Header { Text(name) }
                Menu({ +Direction.Right + Variation.Size.Small }) {
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
