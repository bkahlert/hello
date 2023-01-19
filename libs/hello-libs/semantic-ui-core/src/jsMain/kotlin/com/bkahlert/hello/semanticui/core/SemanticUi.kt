package com.bkahlert.hello.semanticui.core

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.hello.semanticui.core.dom.SemanticDivElement
import com.bkahlert.hello.semanticui.core.dom.SemanticElement
import org.w3c.dom.HTMLDivElement

/**
 * A semantic UI element of the form `<div class="$classes">$content</div>` that
 * can be used as a fallback for not yet implemented Semantic UI features. */
@Composable
public fun S(
    vararg classes: String,
    attrs: SemanticAttrBuilderContext<SemanticElement<HTMLDivElement>>? = null,
    content: SemanticContentBuilder<SemanticElement<HTMLDivElement>>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes(*classes.flatMap { it.split(' ') }.toTypedArray())
    }, content)
}
