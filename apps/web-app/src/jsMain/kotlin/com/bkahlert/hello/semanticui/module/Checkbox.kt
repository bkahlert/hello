package com.bkahlert.hello.semanticui.module

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.dom.SemanticContentBuilder
import com.bkahlert.hello.semanticui.dom.SemanticDivElement
import com.bkahlert.hello.semanticui.dom.SemanticElement
import com.bkahlert.hello.semanticui.dom.SemanticElementType
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
    attrs: SemanticAttrBuilderContext<CheckboxElement, HTMLDivElement>? = null,
    content: SemanticContentBuilder<CheckboxElement, HTMLDivElement>? = null,
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
    attrs: SemanticAttrBuilderContext<CheckboxElement, HTMLDivElement>? = null,
    content: SemanticContentBuilder<CheckboxElement, HTMLDivElement>? = null,
) {
    Checkbox(null, attrs, content)
}
