package com.bkahlert.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticDivElement
import com.bkahlert.semanticui.core.dom.SemanticElement
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLHeadingElement

public interface HeaderElement<out TElement : Element> : SemanticElement<TElement>
public interface HeaderHeadingElement : HeaderElement<HTMLHeadingElement>
public interface HeaderDivElement : HeaderElement<HTMLDivElement>


/**
 * Creates a [SemanticUI content header](https://semantic-ui.com/elements/header.html#content-headers).
 */
@Composable
public fun Header(
    attrs: SemanticAttrBuilderContext<HeaderDivElement>? = null,
    content: SemanticContentBuilder<HeaderDivElement>? = null,
): Unit = SemanticDivElement({
    classes("ui")
    attrs?.invoke(this)
    classes("header")
}, content)

/**
 * Creates a [SemanticUI icon header](https://semantic-ui.com/elements/header.html#icon-headers).
 */
@Composable
public fun IconHeader(
    vararg icon: String,
    attrs: SemanticAttrBuilderContext<HeaderDivElement>? = null,
    content: ContentBuilder<HTMLDivElement>? = null,
): Unit = Header({
    attrs?.invoke(this)
    classes("icon")
}) {
    Icon(*icon)
    Div({ classes("content") }, content)
}

/**
 * Creates a [SemanticUI icon sub header](https://semantic-ui.com/elements/header.html#icon-headers).
 */
@Composable
public fun IconSubHeader(
    vararg icon: String,
    attrs: SemanticAttrBuilderContext<HeaderDivElement>? = null,
    content: ContentBuilder<HTMLDivElement>? = null,
): Unit = Header({
    attrs?.invoke(this)
    classes("icon", "sub")
}) {
    Icon(*icon)
    Div({ classes("content") }, content)
}


/**
 * Creates a [SemanticUI content sub header](https://semantic-ui.com/elements/header.html#sub-headers).
 */
@Composable
public fun SubHeader(
    attrs: SemanticAttrBuilderContext<HeaderDivElement>? = null,
    content: SemanticContentBuilder<HeaderDivElement>? = null,
): Unit = Header({
    attrs?.invoke(this)
    classes("sub")
}, content)
