package com.bkahlert.hello.fritz2.components

import dev.fritz2.core.HtmlTag
import dev.fritz2.core.RenderContext
import dev.fritz2.core.ScopeContext
import org.w3c.dom.HTMLSpanElement

/**
 * Component only visible to screen readers.
 */
public fun RenderContext.screenReaderOnly(
    id: String? = null,
    scope: (ScopeContext.() -> Unit) = {},
    content: HtmlTag<HTMLSpanElement>.() -> Unit,
): HtmlTag<HTMLSpanElement> = span("sr-only", id, scope, content)
