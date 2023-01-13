package com.bkahlert.hello.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.dom.SemanticContentBuilder
import com.bkahlert.hello.semanticui.dom.SemanticDivElement
import com.bkahlert.hello.semanticui.dom.SemanticElement
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.H4
import org.jetbrains.compose.web.dom.H5
import org.jetbrains.compose.web.dom.H6
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLHeadingElement

interface HeaderElement<TElement : Element> : SemanticElement<TElement>
interface HeaderHeadingElement : SemanticElement<HTMLHeadingElement>
interface HeaderDivElement : SemanticElement<HTMLDivElement>

/**
 * Creates a [SemanticUI page header](https://semantic-ui.com/elements/header.html#page-headers).
 */
@Composable
fun Header1(
    attrs: SemanticAttrBuilderContext<HeaderHeadingElement>? = null,
    content: SemanticContentBuilder<HeaderHeadingElement>? = null,
) {
    SemanticElement({
        classes("ui")
        attrs?.invoke(this)
        classes("header")
    }, content) { a, c -> H1(a, c) }
}

/**
 * Creates a [SemanticUI page header](https://semantic-ui.com/elements/header.html#page-headers).
 */
@Composable
fun Header2(
    attrs: SemanticAttrBuilderContext<HeaderHeadingElement>? = null,
    content: SemanticContentBuilder<HeaderHeadingElement>? = null,
) {
    SemanticElement({
        classes("ui")
        attrs?.invoke(this)
        classes("header")
    }, content) { a, c -> H2(a, c) }
}

/**
 * Creates a [SemanticUI page header](https://semantic-ui.com/elements/header.html#page-headers).
 */
@Composable
fun Header3(
    attrs: SemanticAttrBuilderContext<HeaderHeadingElement>? = null,
    content: SemanticContentBuilder<HeaderHeadingElement>? = null,
) {
    SemanticElement({
        classes("ui")
        attrs?.invoke(this)
        classes("header")
    }, content) { a, c -> H3(a, c) }
}

/**
 * Creates a [SemanticUI page header](https://semantic-ui.com/elements/header.html#page-headers).
 */
@Composable
fun Header4(
    attrs: SemanticAttrBuilderContext<HeaderHeadingElement>? = null,
    content: SemanticContentBuilder<HeaderHeadingElement>? = null,
) {
    SemanticElement({
        classes("ui")
        attrs?.invoke(this)
        classes("header")
    }, content) { a, c -> H4(a, c) }
}

/**
 * Creates a [SemanticUI page header](https://semantic-ui.com/elements/header.html#page-headers).
 */
@Composable
fun Header5(
    attrs: SemanticAttrBuilderContext<HeaderHeadingElement>? = null,
    content: SemanticContentBuilder<HeaderHeadingElement>? = null,
) {
    SemanticElement({
        classes("ui")
        attrs?.invoke(this)
        classes("header")
    }, content) { a, c -> H5(a, c) }
}

/**
 * Creates a [SemanticUI page header](https://semantic-ui.com/elements/header.html#page-headers).
 */
@Composable
fun Header6(
    attrs: SemanticAttrBuilderContext<HeaderHeadingElement>? = null,
    content: SemanticContentBuilder<HeaderHeadingElement>? = null,
) {
    SemanticElement({
        classes("ui")
        attrs?.invoke(this)
        classes("header")
    }, content) { a, c -> H6(a, c) }
}

/**
 * Creates a [SemanticUI content header](https://semantic-ui.com/elements/header.html#content-headers).
 */
@Composable
fun Header(
    attrs: SemanticAttrBuilderContext<HeaderDivElement>? = null,
    content: SemanticContentBuilder<HeaderDivElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        classes("header")
    }, content)
}

/**
 * Creates a [SemanticUI icon header](https://semantic-ui.com/elements/header.html#icon-headers).
 */
@Composable
fun IconHeader(
    vararg icon: String,
    attrs: SemanticAttrBuilderContext<IconElement>? = null,
    content: ContentBuilder<HTMLDivElement>? = null,
) {
    Header({ classes("icon") }) {
        Icon(*icon) { attrs?.invoke(this) }
        Div({ classes("content") }, content)
    }
}


/**
 * Creates a [SemanticUI content sub header](https://semantic-ui.com/elements/header.html#sub-headers).
 */
@Composable
fun SubHeader(
    attrs: SemanticAttrBuilderContext<HeaderDivElement>? = null,
    content: SemanticContentBuilder<HeaderDivElement>? = null,
) {
    Header({
        attrs?.invoke(this)
        classes("sub")
    }, content)
}
