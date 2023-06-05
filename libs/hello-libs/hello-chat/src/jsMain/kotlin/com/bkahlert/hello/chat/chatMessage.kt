package com.bkahlert.hello.chat

import com.bkahlert.hello.fritz2.ContentBuilder
import com.bkahlert.kommons.time.toMomentString
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.RenderContext
import dev.fritz2.core.classes
import kotlinx.datetime.Instant
import org.w3c.dom.HTMLDivElement

public fun RenderContext.chatMessage(
    name: String? = null,
    image: ContentBuilder<HTMLDivElement>? = null,
    timestamp: Instant? = null,
    status: String? = null,
    end: Boolean = false,
    content: ContentBuilder<HTMLDivElement>? = null,
): HtmlTag<HTMLDivElement> = div(
    classes(
        "chat",
        if (end) "chat-end" else "chat-start",
    )
) {
    ChatCss
    if (image != null) div("chat-image") { image() }
    if (name != null || timestamp != null) {
        div("chat-header") {
            if (name != null) +name
            if (timestamp != null) time("text-xs opacity-50") { +timestamp.toMomentString() }
        }
    }
    if (content != null) div("chat-bubble") { content() }
    if (status != null) div("chat-footer opacity-50") { +status }
}
