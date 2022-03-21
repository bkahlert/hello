package com.semanticui.compose.module

import androidx.compose.runtime.Composable
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.SemanticBuilder
import com.semanticui.compose.SemanticDivElement
import com.semanticui.compose.SemanticElement
import com.semanticui.compose.SemanticElementType
import org.w3c.dom.HTMLDivElement

interface CheckboxElement : SemanticElement
enum class CheckboxElementType(override vararg val classNames: String) : SemanticElementType<CheckboxElement> {
    Radio("radio"),
    Slider("slider"),
    Toggle("toggle"),
}

/**
 * Creates a [SemanticUI Checkbox](https://semantic-ui.com/modules/Checkbox.html)
 * with the specified [type].
 */
@Composable
fun Checkbox(
    type: CheckboxElementType?,
    attrs: SemanticAttrBuilder<CheckboxElement, HTMLDivElement>? = null,
    content: SemanticBuilder<CheckboxElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        type?.invoke(this)
        classes("checkbox")
    }, content)
}

/**
 * Creates a [SemanticUI Checkbox](https://semantic-ui.com/modules/Checkbox.html).
 */
@Composable
fun Checkbox(
    attrs: SemanticAttrBuilder<CheckboxElement, HTMLDivElement>? = null,
    content: SemanticBuilder<CheckboxElement, HTMLDivElement>? = null,
) {
    Checkbox(null, attrs, content)
}
