package com.bkahlert.hello.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.SemanticAttrBuilder
import com.bkahlert.hello.semanticui.SemanticBuilder
import com.bkahlert.hello.semanticui.SemanticDivElement
import com.bkahlert.hello.semanticui.SemanticElement
import com.bkahlert.hello.semanticui.SemanticElementType
import org.jetbrains.compose.web.dom.A
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLDivElement

interface ButtonElement : SemanticElement
enum class ButtonElementType(override vararg val classNames: String) : SemanticElementType<ButtonElement> {
    Primary("primary"),
    Secondary("secondary"),
    Animated("animated"),
    Labeled("labeled"),
    LabeledIcon("labeled", "icon"),
    Basic("basic"),
    Inverted("inverted"),
}

/**
 * Creates a [SemanticUI button](https://semantic-ui.com/elements/button.html)
 * with the specified [type].
 */
@Composable
fun Button(
    type: ButtonElementType?,
    attrs: SemanticAttrBuilder<ButtonElement, HTMLDivElement>? = null,
    content: SemanticBuilder<ButtonElement, HTMLDivElement>? = null,
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
    attrs: SemanticAttrBuilder<ButtonElement, HTMLDivElement>? = null,
    content: SemanticBuilder<ButtonElement, HTMLDivElement>? = null,
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
    attrs: SemanticAttrBuilder<ButtonElement, HTMLAnchorElement>? = null,
    content: SemanticBuilder<ButtonElement, HTMLAnchorElement>? = null,
) {
    SemanticElement({
        classes("ui")
        attrs?.invoke(this)
        classes("button")
    }, content) { a, c -> A(href, a, c) }
}

interface ButtonGroupElement : SemanticElement
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
    attrs: SemanticAttrBuilder<ButtonGroupElement, HTMLDivElement>? = null,
    content: SemanticBuilder<ButtonGroupElement, HTMLDivElement>? = null,
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
    attrs: SemanticAttrBuilder<ButtonGroupElement, HTMLDivElement>? = null,
    content: SemanticBuilder<ButtonGroupElement, HTMLDivElement>? = null,
) {
    Buttons(null, attrs, content)
}
