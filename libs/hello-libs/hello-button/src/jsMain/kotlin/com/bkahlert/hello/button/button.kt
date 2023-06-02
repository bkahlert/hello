package com.bkahlert.hello.button

import com.bkahlert.hello.fritz2.small
import com.bkahlert.hello.icon.icon
import com.bkahlert.kommons.uri.Uri
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.RenderContext
import dev.fritz2.core.classes
import dev.fritz2.core.title
import dev.fritz2.core.type
import dev.fritz2.headless.foundation.Aria
import org.w3c.dom.HTMLButtonElement

/** Renders a button with the specified [classes], [icon], [caption], and [description]. */
public fun RenderContext.button(
    classes: String?,
    icon: Uri?,
    caption: String,
    description: String? = null,
    simple: Boolean = false,
    inverted: Boolean = false,
    iconOnly: Boolean = false,
): HtmlTag<HTMLButtonElement> = button(
    classes(
        when {
            inverted && simple -> "btn-invert-simple"
            inverted -> "btn-invert"
            simple -> "btn-simple"
            else -> "btn"
        }, classes
    )
) {
    type("button")
    icon?.also { icon("shrink-0 w-6 h-6", it) }
    if (iconOnly) {
        title(caption)
        description?.also { attr(Aria.description, it) }
    } else {
        +caption
        description?.also { small { domNode.innerHTML = it } }
    }
}

/** Renders a button with the specified [classes], [caption], and [description]. */
public fun RenderContext.button(
    classes: String?,
    caption: String,
    description: String? = null,
    simple: Boolean = false,
    inverted: Boolean = false,
    iconOnly: Boolean = false,
): HtmlTag<HTMLButtonElement> = button(classes, null, caption, description, simple, inverted, iconOnly)

/** Renders a button with the specified [icon], [caption], and [description]. */
public fun RenderContext.button(
    icon: Uri,
    caption: String,
    description: String? = null,
    simple: Boolean = false,
    inverted: Boolean = false,
    iconOnly: Boolean = false,
): HtmlTag<HTMLButtonElement> = button(null, icon, caption, description, simple, inverted, iconOnly)

/** Renders a button with the specified [caption], and [description]. */
public fun RenderContext.button(
    caption: String,
    description: String? = null,
    simple: Boolean = false,
    inverted: Boolean = false,
    iconOnly: Boolean = false,
): HtmlTag<HTMLButtonElement> = button(null, null, caption, description, simple, inverted, iconOnly)
