package com.bkahlert.hello.semanticui.module

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.hello.semanticui.core.dom.SemanticDivElement
import com.bkahlert.hello.semanticui.core.dom.SemanticElement
import com.bkahlert.hello.semanticui.core.dom.SemanticElementType
import org.w3c.dom.HTMLDivElement

public interface CheckboxElement : SemanticElement<HTMLDivElement>
public enum class CheckboxElementType(override vararg val classNames: String) : SemanticElementType<CheckboxElement> {
    Radio("radio"),
    Slider("slider"),
    Toggle("toggle"),
}

/**
 * Creates a [SemanticUI Checkbox](https://semantic-ui.com/modules/Checkbox.html)
 * with the specified [type].
 */
@Composable
public fun Checkbox(
    type: CheckboxElementType?,
    attrs: SemanticAttrBuilderContext<CheckboxElement>? = null,
    content: SemanticContentBuilder<CheckboxElement>? = null,
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
public fun Checkbox(
    attrs: SemanticAttrBuilderContext<CheckboxElement>? = null,
    content: SemanticContentBuilder<CheckboxElement>? = null,
) {
    Checkbox(null, attrs, content)
}
