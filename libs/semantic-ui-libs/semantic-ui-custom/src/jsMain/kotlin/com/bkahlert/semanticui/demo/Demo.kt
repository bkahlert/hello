package com.bkahlert.semanticui.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.semanticui.collection.Header
import com.bkahlert.semanticui.collection.Item
import com.bkahlert.semanticui.collection.LinkItem
import com.bkahlert.semanticui.collection.Menu
import com.bkahlert.semanticui.collection.disabled
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Attached
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.element.Header
import com.bkahlert.semanticui.element.HeaderDivElement
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.Segment
import com.bkahlert.semanticui.element.SegmentElement
import com.bkahlert.semanticui.element.SegmentGroupElement
import com.bkahlert.semanticui.element.Segments
import com.bkahlert.semanticui.element.attached
import org.jetbrains.compose.web.css.borderWidth
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLParagraphElement
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/** A composable to hold [Demo] composables. */
@Composable
public fun Demos(
    name: String,
    attrs: SemanticAttrBuilderContext<HeaderDivElement>? = null,
    content: SemanticContentBuilder<SegmentGroupElement>? = null,
) {
    Segments {
        DemosHeader(name, attrs)
        Segments({
            style {
                borderWidth(0.px)
                property("box-shadow", "none")
            }
        }) {
            content?.invoke(this)
        }
    }
}

@Composable
private fun DemosHeader(
    name: String,
    attrs: SemanticAttrBuilderContext<HeaderDivElement>? = null,
) {
    Segment {
        Header({
            attrs?.invoke(this)
        }) { Text(name) }
    }
}

/** A composable to demonstrate the specified [content]. */
@Composable
public fun Demo(
    name: String,
    attrs: SemanticAttrBuilderContext<SegmentElement>? = null,
    basic: Boolean = false,
    warning: String? = null,
    content: SemanticContentBuilder<SegmentElement>? = null,
) {
    var resetting by remember { mutableStateOf(false) }
    if (resetting) {
        DemoControls(name, resetting = true, warning = warning) { }
        resetting = false
        Segment { }
    } else {
        DemoControls(name, resetting = false, warning = warning) { resetting = true }
        Segment({
            v.attached(Attached.Bottom)
            if (basic) {
                style {
                    borderWidth(0.px)
                    padding(0.px)
                }
            }
            attrs?.invoke(this)
        }) {
            content?.invoke(this)
        }
    }
}

@Composable
private fun DemoControls(
    name: String,
    resetting: Boolean,
    warning: String? = null,
    onReset: () -> Unit,
) {
    Menu({
        classes("grey", "top", "attached", "mini", "borderless", "inverted")
    }) {
        Header { Text(name) }
        if (warning != null) {
            Item {
                S("ui", "yellow", "label") { Icon("attention"); Text(warning) }
            }
        }
        Menu({ classes("right") }) {
            // TODO change background controls
            LinkItem({
                if (resetting) {
                    s.disabled()
                } else {
                    onClick { onReset() }
                }
            }) {
                Icon("redo", "alternate")
                Text("Reset")
            }
        }
    }
}


// Utils

/**
 * Duration that can be used in demos to simulate the time
 * an offloaded background process takes.
 */
public val DEMO_BASE_DELAY: Duration = 1.5.seconds

public const val LoremIpsum: String = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam"

@Suppress("NOTHING_TO_INLINE")
@Composable
public inline fun LoremIpsumText() {
    Text(LoremIpsum)
}

@Composable
public fun LoremIpsumParagraph(
    attrs: AttrBuilderContext<HTMLParagraphElement>? = null,
) {
    P(attrs) { LoremIpsumText() }
}
