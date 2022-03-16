package com.semanticui.compose.element

import androidx.compose.runtime.Composable
import com.semanticui.compose.Modifier
import com.semanticui.compose.classNames
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLDivElement

/**
 * Creates a [SemanticUI divider](https://semantic-ui.com/elements/divider.html#divider).
 */
@Composable
fun Divider(
    vararg modifiers: Modifier,
    content: ContentBuilder<HTMLDivElement>? = null,
) {
    Div({
        classes("ui", *modifiers.classNames, "divider")
    }, content)
}
