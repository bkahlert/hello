package com.bkahlert.semanticui.core.dom

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.attributes.SemanticAttrsScopeBuilder
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement

/**
 * A builder for an [Element] to a [SemanticElement] is based on.
 */
public typealias ElementBuilder<T> = (attrs: AttrBuilderContext<T>?, content: ContentBuilder<T>?) -> Unit

/**
 * Creates a [TSemantic] representing element with
 * the specified [semanticAttrs] and
 * the specified [semanticContent]
 * based on the [TElement]
 * built using the specified [elementBuilder].
 */
@Composable
public fun <TSemantic : SemanticElement<Element>> SemanticElement(
    semanticAttrs: SemanticAttrBuilderContext<TSemantic>? = null,
    semanticContent: SemanticContentBuilder<TSemantic>? = null,
    elementBuilder: @Composable ElementBuilder<Element>,
): Unit = elementBuilder(
    { semanticAttrs?.invoke(SemanticAttrsScopeBuilder(this)) },
    { semanticContent?.invoke(SemanticElementScopeBase(this)) },
)

/**
 * Creates a [TSemantic] representing element with
 * the specified [semanticAttrs] and
 * the specified [semanticContent]
 * based on a [HTMLDivElement].
 */
@Composable
public fun <TSemantic : SemanticElement<HTMLDivElement>> SemanticDivElement(
    semanticAttrs: SemanticAttrBuilderContext<TSemantic>? = null,
    semanticContent: SemanticContentBuilder<TSemantic>? = null,
): Unit = SemanticElement(semanticAttrs, semanticContent) { attrs, content -> Div(attrs, content) }
