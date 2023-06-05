package com.bkahlert.hello.chat

import com.bkahlert.hello.fritz2.ContentBuilder
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.RenderContext
import dev.fritz2.core.classes
import org.w3c.dom.HTMLDivElement

public fun RenderContext.chat(
    history: ContentBuilder<HTMLDivElement>? = null,
    prompt: ContentBuilder<HTMLDivElement>? = null,
): HtmlTag<HTMLDivElement> = div(
    classes(
        "w-full",
        "flex flex-col",
        "rounded-xl overflow-hidden shadow-xl",
        "bg-default/60 text-default dark:bg-invert/60 dark:text-invert",
    )
) {
    history?.also {
        div("p-4 flex-1 overflow-y-auto") { it() }
    }
    prompt?.also {
        div("p-4") { it() }
    }
}
