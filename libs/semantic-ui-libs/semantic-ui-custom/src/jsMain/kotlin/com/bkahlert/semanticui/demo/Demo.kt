package com.bkahlert.semanticui.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.kommons.dom.appendDivElement
import com.bkahlert.kommons.dom.data
import com.bkahlert.kommons.js.debug
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
import com.bkahlert.semanticui.element.ImageHeader
import com.bkahlert.semanticui.element.Line
import com.bkahlert.semanticui.element.Paragraph
import com.bkahlert.semanticui.element.Placeholder
import com.bkahlert.semanticui.element.Segment
import com.bkahlert.semanticui.element.SegmentElement
import com.bkahlert.semanticui.element.SegmentGroupElement
import com.bkahlert.semanticui.element.Segments
import com.bkahlert.semanticui.element.attached
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.css.borderWidth
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.DOMScope
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLDivElement
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
    Segments({ classes("demos") }) {
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
    Segment({ classes("demos-header") }) {
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
    var reset by remember { mutableStateOf(false) }
    if (reset) {
        DemoControls(name, warning = warning, onReset = null)
        LaunchedEffect(Unit) {
            delay(0.25.seconds)
            reset = false
        }
    } else {
        DemoControls(name, warning = warning, onReset = {
            console.debug("Resetting demo", name)
            reset = true
        })
    }
    Segment({
        classes("demo")
        v.attached(Attached.Bottom)
        if (basic) {
            style {
                borderWidth(0.px)
                padding(0.px)
            }
        }
        if (!reset) {
            attrs?.invoke(this)
        }
    }) {
        if (reset) {
            Text("Resetting ...")
        } else {
            content?.invoke(this)
        }
    }
}


/** A composable to demonstrate the specified [content]. */
@Composable
public fun DemoSandbox(
    name: String,
    basic: Boolean = false,
    warning: String? = null,
    content: @Composable DOMScope<HTMLDivElement>.() -> Unit,
) {
    var reset by remember { mutableStateOf(false) }
    if (reset) {
        DemoControls(name, warning = warning, onReset = null)
        LaunchedEffect(Unit) {
            delay(0.25.seconds)
            reset = false
        }
    } else {
        DemoControls(name, warning = warning, onReset = {
            console.debug("Resetting demo", name)
            reset = true
        })
    }

    Segment({
        classes("demo")
        v.attached(Attached.Bottom)
        if (basic) {
            style {
                borderWidth(0.px)
                padding(0.px)
            }
        }
    }) {
        if (reset) {
            Text("Resetting ...")
        } else {
            DisposableEffect(Unit) {
                val root: HTMLDivElement = scopeElement.appendDivElement()
                val composition: Composition = renderComposable(root = root, content = content)
                onDispose {
                    composition.dispose()
                }
            }
        }
    }
}


@Composable
private fun DemoControls(
    name: String,
    warning: String? = null,
    onReset: (() -> Unit)?,
) {
    Menu({
        classes("demo-controls")
        classes("grey", "top", "attached", "mini", "borderless", "inverted")
    }) {
        Header { Text(name) }
        if (warning != null) {
            Item({
                prop({ e: HTMLDivElement, v: String -> e.data("tooltip", v) }, warning)
            }) {
                S("ui", "yellow", "empty", "circular", "label")
            }
        }
        Menu({ classes("right") }) {
            // TODO change background controls
            LinkItem({
                if (onReset != null) {
                    onClick { onReset() }
                } else {
                    s.disabled()
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


@Composable
public fun PlaceholderImageAndLines(lines: Int = 2) {
    Placeholder {
        ImageHeader {
            repeat(lines) {
                Line()
            }
        }
    }
}

@Composable
public fun PlaceholderParagraph(lines: Int = 4) {
    Placeholder {
        Paragraph {
            repeat(lines) {
                Line()
            }
        }
    }
}
