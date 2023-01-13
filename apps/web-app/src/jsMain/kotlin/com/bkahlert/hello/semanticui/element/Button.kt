package com.bkahlert.hello.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.attributes.SemanticAttrsScope
import com.bkahlert.hello.semanticui.attributes.Variation
import com.bkahlert.hello.semanticui.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.dom.SemanticContentBuilder
import com.bkahlert.hello.semanticui.dom.SemanticDivElement
import com.bkahlert.hello.semanticui.dom.SemanticElement
import com.bkahlert.hello.semanticui.dom.SemanticElementType
import org.jetbrains.compose.web.dom.A
import org.w3c.dom.HTMLDivElement

interface ButtonElement : SemanticElement<HTMLDivElement>
enum class ButtonElementType(override vararg val classNames: String) : SemanticElementType<ButtonElement> {
    Primary("primary"),
    Secondary("secondary"),
    Animated("animated"),
    Labeled("labeled"),
    LabeledIcon("labeled", "icon"),
    Basic("basic"),
    Inverted("inverted"),
}

/** [Positive](https://semantic-ui.com/elements/button.html#positive) variation of a [button](https://semantic-ui.com/elements/button.html). */
@Suppress("unused")
val <TSemantic : ButtonElement> SemanticAttrsScope<TSemantic>.positive: Variation get() = Variation.Positive

/** [Negative](https://semantic-ui.com/elements/button.html#negative) variation of a [button](https://semantic-ui.com/elements/button.html). */
@Suppress("unused")
val <TSemantic : ButtonElement> SemanticAttrsScope<TSemantic>.negative: Variation get() = Variation.Negative

/**
 * Creates a [SemanticUI button](https://semantic-ui.com/elements/button.html)
 * with the specified [type].
 */
@Composable
fun Button(
    type: ButtonElementType?,
    attrs: SemanticAttrBuilderContext<ButtonElement>? = null,
    content: SemanticContentBuilder<ButtonElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        type?.invoke(this)
        classes("button")
    }, content)
}

/**
 * Creates a [SemanticUI button](https://semantic-ui.com/elements/button.html).
 */
@Composable
fun Button(
    attrs: SemanticAttrBuilderContext<ButtonElement>? = null,
    content: SemanticContentBuilder<ButtonElement>? = null,
) {
    Button(null, attrs, content)
}

/**
 * Creates a [SemanticUI button](https://semantic-ui.com/elements/button.html)
 * based on an anchor tag.
 */
@Composable
fun AnkerButton(
    href: String? = null,
    attrs: SemanticAttrBuilderContext<ButtonElement>? = null,
    content: SemanticContentBuilder<ButtonElement>? = null,
) {
    SemanticElement({
        classes("ui")
        attrs?.invoke(this)
        classes("button")
    }, content) { a, c -> A(href, a, c) }
}

interface ButtonGroupElement : SemanticElement<HTMLDivElement>
enum class ButtonGroupElementType(override vararg val classNames: String) : SemanticElementType<ButtonGroupElement> {
    Icon("icon"),
}

/**
 * Creates a [SemanticUI button group](https://semantic-ui.com/elements/button.html#buttons)
 * with the specified [type].
 */
@Composable
fun Buttons(
    type: ButtonGroupElementType?,
    attrs: SemanticAttrBuilderContext<ButtonGroupElement>? = null,
    content: SemanticContentBuilder<ButtonGroupElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        type?.invoke(this)
        classes("buttons")
    }, content)
}

/**
 * Creates a [SemanticUI button group](https://semantic-ui.com/elements/button.html#buttons).
 */
@Composable
fun Buttons(
    attrs: SemanticAttrBuilderContext<ButtonGroupElement>? = null,
    content: SemanticContentBuilder<ButtonGroupElement>? = null,
) {
    Buttons(null, attrs, content)
}
