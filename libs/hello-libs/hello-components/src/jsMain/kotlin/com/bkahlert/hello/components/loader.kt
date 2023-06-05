package com.bkahlert.hello.components

import com.bkahlert.hello.fritz2.srOnly
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Tag
import dev.fritz2.core.classes
import dev.fritz2.headless.foundation.Aria
import dev.fritz2.headless.foundation.AriaReferenceHook
import dev.fritz2.headless.foundation.hook
import org.w3c.dom.HTMLDivElement

public fun RenderContext.loader(
    text: String = "Loading...",
    classes: String? = "w-4 h-4",
) {
    val ariaLabelId = AriaReferenceHook<Tag<HTMLDivElement>>(Aria.labelledby)
    div(
        classes(
            "relative",
            classes,
        )
    ) {
        attr("role", Aria.Role.status)
        div("absolute inset-0 border-glass-light-100 dark:border-glass-dark-100 border-2 rounded-full") { }
        div("absolute inset-0 border-current border-opacity-50 border-t-2 animate-spin rounded-full") {
        }
        hook(ariaLabelId)
        srOnly(ariaLabelId("title")) { +text }
    }
}
