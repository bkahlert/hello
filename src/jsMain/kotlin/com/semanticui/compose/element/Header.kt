package com.semanticui.compose.element

import androidx.compose.runtime.Composable
import com.semanticui.compose.Modifier
import com.semanticui.compose.classNames
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.H4
import org.jetbrains.compose.web.dom.H5
import org.jetbrains.compose.web.dom.H6
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLHeadingElement

/**
 * Creates a [SemanticUI page header](https://semantic-ui.com/elements/header.html#page-headers).
 */
@Composable
fun Header1(
    vararg modifiers: Modifier,
    content: ContentBuilder<HTMLHeadingElement>? = null,
) {
    H1({
        classes("ui", *modifiers.classNames, "header")
    }, content)
}

/**
 * Creates a [SemanticUI page header](https://semantic-ui.com/elements/header.html#page-headers).
 */
@Composable
fun Header2(
    vararg modifiers: Modifier,
    content: ContentBuilder<HTMLHeadingElement>? = null,
) {
    H2({
        classes("ui", *modifiers.classNames, "header")
    }, content)
}

/**
 * Creates a [SemanticUI page header](https://semantic-ui.com/elements/header.html#page-headers).
 */
@Composable
fun Header3(
    vararg modifiers: Modifier,
    content: ContentBuilder<HTMLHeadingElement>? = null,
) {
    H3({
        classes("ui", *modifiers.classNames, "header")
    }, content)
}

/**
 * Creates a [SemanticUI page header](https://semantic-ui.com/elements/header.html#page-headers).
 */
@Composable
fun Header4(
    vararg modifiers: Modifier,
    content: ContentBuilder<HTMLHeadingElement>? = null,
) {
    H4({
        classes("ui", *modifiers.classNames, "header")
    }, content)
}

/**
 * Creates a [SemanticUI page header](https://semantic-ui.com/elements/header.html#page-headers).
 */
@Composable
fun Header5(
    vararg modifiers: Modifier,
    content: ContentBuilder<HTMLHeadingElement>? = null,
) {
    H5({
        classes("ui", *modifiers.classNames, "header")
    }, content)
}

/**
 * Creates a [SemanticUI page header](https://semantic-ui.com/elements/header.html#page-headers).
 */
@Composable
fun Header6(
    vararg modifiers: Modifier,
    content: ContentBuilder<HTMLHeadingElement>? = null,
) {
    H6({
        classes("ui", *modifiers.classNames, "header")
    }, content)
}

/**
 * Creates a [SemanticUI content header](https://semantic-ui.com/elements/header.html#content-headers).
 */
@Composable
fun Header(
    vararg modifiers: Modifier,
    content: ContentBuilder<HTMLDivElement>? = null,
) {
    Div({
        classes("ui", *modifiers.classNames, "header")
    }, content)
}
