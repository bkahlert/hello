package com.semanticui.compose.element

import androidx.compose.runtime.Composable
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.SemanticBuilder
import com.semanticui.compose.SemanticDivElement
import com.semanticui.compose.SemanticElement
import com.semanticui.compose.SemanticElementType
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
