package com.bkahlert.hello.fritz2

import dev.fritz2.core.HtmlTag
import dev.fritz2.core.RenderContext
import dev.fritz2.core.ScopeContext
import dev.fritz2.core.Tag
import dev.fritz2.headless.foundation.TagFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLSpanElement

/** Returns a [TagFactory] that handles [E] typed elements instead of the generic [Element] as vanilla [RenderContext.custom] does. */
public fun <T : Tag<*>> custom(localName: String): TagFactory<T> = { renderContext, baseClass, customId, customScope, content ->
    renderContext.custom(localName, baseClass, customId, customScope, content)
}

/** Typed variant of [RenderContext.custom]. */
public fun <T : Tag<*>> RenderContext.custom(
    localName: String,
    baseClass: String? = null,
    id: String? = null,
    scope: (ScopeContext.() -> Unit) = {},
    content: T.() -> Unit,
): T = custom(localName, baseClass, id, scope) { content.invoke(unsafeCast<T>()) }.unsafeCast<T>()

public fun RenderContext.small(
    baseClass: String? = null,
    id: String? = null,
    scope: (ScopeContext.() -> Unit) = {},
    content: HtmlTag<HTMLElement>.() -> Unit
): HtmlTag<HTMLElement> = custom("small", baseClass, id, scope, content)

/**
 * Component only visible to screen readers.
 */
public fun RenderContext.srOnly(
    id: String? = null,
    scope: (ScopeContext.() -> Unit) = {},
    content: HtmlTag<HTMLSpanElement>.() -> Unit,
): HtmlTag<HTMLSpanElement> = span("sr-only", id, scope, content)

public operator fun Flow<Boolean>.not(): Flow<Boolean> = map { !it }
