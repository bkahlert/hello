@file:Suppress("RedundantVisibilityModifier")

package com.bkahlert.hello.fritz2.components.showcase

import dev.fritz2.core.HtmlTag
import dev.fritz2.core.RenderContext
import dev.fritz2.core.classes

public const val LoremIpsum: String = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam"

@Suppress("NOTHING_TO_INLINE")

public inline fun HtmlTag<*>.loremIpsumText(words: Int? = null) {
    if (words != null) +(LoremIpsum.split(" ").take(words).joinToString(" "))
    else +LoremIpsum
}

public fun RenderContext.loremIpsumHeader(
    classes: String? = null,
) {
    h3(classes) { loremIpsumText(3) }
}

public fun RenderContext.loremIpsumParagraph(
    classes: String? = null,
) {
    p(classes) { loremIpsumText() }
}

public fun RenderContext.placeholder(baseClass: String? = null) {
    div(classes("bg-hero-diagonal-stripes-swatch-magenta w-full h-24 lg:32", baseClass)) {}
}

public fun RenderContext.placeholderImageAndLines(baseClass: String? = null) {
    div("animate-pulse flex space-x-4") {
        image(baseClass)
        paragraph(baseClass)
    }
}


public fun RenderContext.placeholderParagraph(baseClass: String? = null, paragraphs: Int = 1) {
    div("animate-pulse flex space-x-4") {
        repeat(paragraphs) {
            paragraph(baseClass)
        }
    }
}

private fun RenderContext.image(baseClass: String? = null) {
    div(classes("rounded-lg bg-slate-700 h-10 w-10", baseClass)) {}
}

private fun RenderContext.paragraph(baseClass: String? = null) {
    div("flex-1 space-y-6 py-1") {
        straightLine(baseClass)
        div("space-y-3") {
            interruptedLine(baseClass)
            straightLine(baseClass)
        }
    }
}

private fun RenderContext.straightLine(baseClass: String? = null) {
    div(classes("h-2 bg-slate-700 rounded", baseClass)) {}
}

private fun RenderContext.interruptedLine(baseClass: String? = null) {
    div("grid grid-cols-3 gap-4") {
        div(classes("h-2 bg-slate-700 rounded col-span-2", baseClass)) {}
        div(classes("h-2 bg-slate-700 rounded col-span-1", baseClass)) {}
    }
}
