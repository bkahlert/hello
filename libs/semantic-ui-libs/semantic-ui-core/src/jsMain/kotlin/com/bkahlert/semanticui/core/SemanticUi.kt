package com.bkahlert.semanticui.core

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticDivElement
import com.bkahlert.semanticui.core.dom.SemanticElement
import org.w3c.dom.HTMLDivElement

/**
 * A semantic UI element of the form `<div class="$classes">$content</div>` that
 * can be used as a fallback for not yet implemented Semantic UI features. */
@Composable
public fun S(
    vararg classes: String?,
    attrs: SemanticAttrBuilderContext<SemanticElement<HTMLDivElement>>? = null,
    content: SemanticContentBuilder<SemanticElement<HTMLDivElement>>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes(*classes.filterNotNull().flatMap { it.split(' ') }.toTypedArray())
    }, content)
}
