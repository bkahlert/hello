package com.bkahlert.hello.fritz2.components

import com.bkahlert.hello.fritz2.HtmlContentBuilder
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.RenderContext
import dev.fritz2.core.classes
import org.w3c.dom.HTMLDivElement

/**
 * Simple container that provides default styling for
 * content from content management systems, Markdown, etc.
 *
 * @see <a href="https://tailwindcss.com/docs/typography-plugin">@tailwindcss/typography</a>
 */
public fun RenderContext.proseBox(
    classes: String? = null,
    content: HtmlContentBuilder<HTMLDivElement>? = null,
): HtmlTag<HTMLDivElement> =
    div(classes("box-prose", classes)) { content?.invoke(this) }
