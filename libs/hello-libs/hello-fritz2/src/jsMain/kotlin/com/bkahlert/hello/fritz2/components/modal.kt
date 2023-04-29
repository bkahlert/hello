package com.bkahlert.hello.fritz2.components

import com.bkahlert.hello.fritz2.ContentBuilder1
import com.bkahlert.kommons.randomString
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.RenderContext
import dev.fritz2.core.animationDone
import dev.fritz2.core.classes
import dev.fritz2.core.transition
import dev.fritz2.headless.foundation.Aria
import kotlinx.dom.removeClass
import org.w3c.dom.HTMLDialogElement

/**
 * Renders a modal dialog with the specified [classes] and [content].
 */
public fun RenderContext.modal(
    classes: String? = null,
    show: Boolean = true,
    content: ContentBuilder1<HTMLDialogElement, String>? = null
): HtmlTag<HTMLDialogElement> = dialog(
    classes(
        "[&::backdrop]:bg-glass",
        "w-[95%] sm:max-w-2xl px-2 py-6 sm:p-14",
        "shadow-lg dark:shadow-xl bg-default/60 text-default dark:bg-invert/60 dark:text-invert",
        "shadow-2xl transition-all",
        "opacity-0 sm:scale-95",
        classes,
    ),
) {
    transition(
        "ease-out duration-300",
        "translate-y-4 sm:translate-y-0",
        "opacity-100 translate-y-0 sm:scale-100",
        "ease-in duration-200",
        "opacity-100 translate-y-0 sm:scale-100",
        "opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
    )

    val labelledbyId = randomString()
    attr("role", Aria.Role.dialog)
    attr("aria-labelledby", labelledbyId)
    attr("aria-modal", true)
    content?.invoke(this, labelledbyId)
}.apply {
    animationDone(domNode).then { domNode.removeClass("opacity-0", "sm:scale-95") }
    if (show) domNode.showModal()
}
