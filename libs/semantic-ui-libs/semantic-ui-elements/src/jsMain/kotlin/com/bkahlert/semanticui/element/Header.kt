package com.bkahlert.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticDivElement
import com.bkahlert.semanticui.core.dom.SemanticElement
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

public fun interface HeaderElement<TElement : Element> : SemanticElement<TElement>
public fun interface HeaderHeadingElement : SemanticElement<HTMLHeadingElement>
public fun interface HeaderDivElement : SemanticElement<HTMLDivElement>

/**
 * Creates a [SemanticUI page header](https://semantic-ui.com/elements/header.html#page-headers).
 */
@Composable
public fun Header1(
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
public fun Header2(
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
public fun Header3(
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
public fun Header4(
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
public fun Header5(
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
public fun Header6(
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
public fun Header(
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
public fun IconHeader(
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
 * Creates a [SemanticUI icon sub header](https://semantic-ui.com/elements/header.html#icon-headers).
 */
@Composable
public fun IconSubHeader(
    vararg icon: String,
    attrs: SemanticAttrBuilderContext<IconElement>? = null,
    content: ContentBuilder<HTMLDivElement>? = null,
) {
    Header({ classes("icon", "sub") }) {
        Icon(*icon) { attrs?.invoke(this) }
        Div({ classes("content") }, content)
    }
}


/**
 * Creates a [SemanticUI content sub header](https://semantic-ui.com/elements/header.html#sub-headers).
 */
@Composable
public fun SubHeader(
    attrs: SemanticAttrBuilderContext<HeaderDivElement>? = null,
    content: SemanticContentBuilder<HeaderDivElement>? = null,
) {
    Header({
        attrs?.invoke(this)
        classes("sub")
    }, content)
}
