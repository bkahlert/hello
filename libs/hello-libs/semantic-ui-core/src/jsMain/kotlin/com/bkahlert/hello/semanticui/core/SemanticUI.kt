package com.bkahlert.hello.semanticui.core

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLDivElement

/**
 * A semantic UI element of the form `<div class="ui $classes">$content</div>` that
 * can be used as a fallback for not yet implemented Semantic UI features. */
@Composable
public fun SemanticUI(
    vararg classes: String,
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: ContentBuilder<HTMLDivElement>? = null,
) {
    Semantic("ui", *classes, attrs = attrs, content = content)
}

/**
 * A semantic UI element of the form `<div class="$classes">$content</div>` that
 * can be used as a fallback for not yet implemented Semantic UI features. */
@Composable
public fun Semantic(
    vararg classes: String,
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: ContentBuilder<HTMLDivElement>? = null,
) {
    Div({
        attrs?.invoke(this)
        classes(*classes.flatMap { it.split(' ') }.toTypedArray())
    }, content)
}
