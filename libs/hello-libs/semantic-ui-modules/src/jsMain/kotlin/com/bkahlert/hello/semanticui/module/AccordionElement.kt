package com.bkahlert.hello.semanticui.module

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.bkahlert.hello.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.hello.semanticui.core.dom.SemanticDivElement
import com.bkahlert.hello.semanticui.core.dom.SemanticElement
import com.bkahlert.hello.semanticui.core.dom.SemanticElementScope
import com.bkahlert.hello.semanticui.core.jQuery
import com.bkahlert.hello.semanticui.element.Icon
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

public interface AccordionElement : SemanticElement<HTMLDivElement>

/**
 * Creates a [SemanticUI icon](https://semantic-ui.com/modules/accordion.html#/definition)
 * using the specified [key] to determine if the visual representation needs to be re-created.
 */
@Composable
public fun Accordion(
    key: Any?,
    attrs: SemanticAttrBuilderContext<AccordionElement>? = null,
    content: SemanticContentBuilder<AccordionElement>? = null,
) {
    SemanticDivElement<AccordionElement>({
        classes("ui")
        attrs?.invoke(this)
        classes("accordion")
    }) {
        content?.invoke(this)
        DisposableEffect(key) {
            jQuery(scopeElement).accordion()
            onDispose { }
        }
    }
}

/**
 * Creates a `<div class="title">` to be used inside a [Accordion].
 */
@Suppress("unused")
@Composable
public fun SemanticElementScope<AccordionElement>.Title(
    attrs: SemanticAttrBuilderContext<AccordionElement>? = null,
    content: SemanticContentBuilder<AccordionElement>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes("title")
    }, content)
}

/**
 * Creates a `<div class="content">` to be used inside a [Accordion].
 */
@Suppress("unused")
@Composable
public fun SemanticElementScope<AccordionElement>.Content(
    attrs: SemanticAttrBuilderContext<AccordionElement>? = null,
    content: SemanticContentBuilder<AccordionElement>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes("content")
    }, content)
}

/**
 * Creates the elements [Title] with the specified [title] and [Content]
 * with the specified [content].
 *
 * The [content] is provided with an instance of [AttrBuilderContext]
 * which needs to be applied to all immediate child elements that
 * should transition.
 */
@Suppress("unused")
@Composable
public fun SemanticElementScope<AccordionElement>.Dropdown(
    title: String,
    attrs: SemanticAttrBuilderContext<SemanticElement<HTMLElement>>? = null,
    content: SemanticContentBuilder<SemanticElement<HTMLElement>>? = null,
) {
    Title {
        Icon("dropdown")
        Text(title)
    }
    Content({
        attrs?.invoke(this)
        classes("transition", "hidden")
    }) {
        content?.invoke(this)
    }
}
