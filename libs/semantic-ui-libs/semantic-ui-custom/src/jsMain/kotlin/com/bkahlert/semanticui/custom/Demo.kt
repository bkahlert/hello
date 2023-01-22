package com.bkahlert.semanticui.custom

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.semanticui.collection.Header
import com.bkahlert.semanticui.collection.LinkItem
import com.bkahlert.semanticui.collection.Menu
import com.bkahlert.semanticui.collection.TextMenu
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.attributes.Modifier
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Attached
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Small
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticElement
import com.bkahlert.semanticui.element.Header
import com.bkahlert.semanticui.element.Icon
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.marginBottom
import org.jetbrains.compose.web.css.marginTop
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement

/** A composable to hold [Demo] composables. */
@Composable
public fun Demos(
    name: String,
    attrs: SemanticAttrBuilderContext<SemanticElement<HTMLDivElement>>? = null,
    content: SemanticContentBuilder<SemanticElement<HTMLDivElement>>? = null,
) {
    S("ui", "segments", "raised", attrs = attrs) {
        Header({
            raw(Attached.Top, Modifier.Variation.Inverted)
            style { property("border-bottom-width", "0") }
        }) { Text(name) }
        content?.invoke(this)
    }
}

/** A composable to demonstrate the specified [content]. */
@Composable
public fun Demo(
    name: String?,
    attrs: SemanticAttrBuilderContext<SemanticElement<HTMLDivElement>>? = null,
    content: SemanticContentBuilder<SemanticElement<HTMLDivElement>>? = null,
) {
    var dirty by remember { mutableStateOf(false) }
    S("ui", "segment", attrs = attrs) {
        Div { }
        if (!dirty) {
            TextMenu({
                raw(Small)
                style { marginTop((-1).em); marginBottom(0.em) }
            }) {
                name?.also { Header { Text(it) } }
                Menu({ classes("right", "small") }) {
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

/** A composable to demonstrate the specified [content]. */
@Composable
public fun Demo(
    attrs: SemanticAttrBuilderContext<SemanticElement<HTMLDivElement>>? = null,
    content: SemanticContentBuilder<SemanticElement<HTMLDivElement>>? = null,
): Unit = Demo(null, attrs, content)
