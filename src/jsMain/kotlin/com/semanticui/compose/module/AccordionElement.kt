package com.semanticui.compose.module

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.SemanticBuilder
import com.semanticui.compose.SemanticDivElement
import com.semanticui.compose.SemanticElement
import com.semanticui.compose.SemanticElementScope
import com.semanticui.compose.element.Icon
import com.semanticui.compose.jQuery
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ElementScope
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement

interface AccordionElement : SemanticElement

/**
 * Creates a [SemanticUI icon](https://semantic-ui.com/modules/accordion.html#/definition)
 * using the specified [key] to determine if the visual representation needs to be updated.
 */
@Composable
fun Accordion(
    key: Any?,
    attrs: SemanticAttrBuilder<AccordionElement, HTMLDivElement>? = null,
    content: SemanticBuilder<AccordionElement, HTMLDivElement>? = null,
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
fun SemanticElementScope<AccordionElement, *>.Title(
    attrs: SemanticAttrBuilder<AccordionElement, HTMLDivElement>? = null,
    content: SemanticBuilder<AccordionElement, HTMLDivElement>? = null,
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
fun SemanticElementScope<AccordionElement, *>.Content(
    attrs: SemanticAttrBuilder<AccordionElement, HTMLDivElement>? = null,
    content: SemanticBuilder<AccordionElement, HTMLDivElement>? = null,
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
 * The is provided with an instance of [AttrBuilderContext]
 * which needs to be applied to all immediate child elements that
 * should transition.
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<AccordionElement, *>.Dropdown(
    title: String,
    content: (@Composable ElementScope<HTMLDivElement>.(
        attrs: AttrBuilderContext<*>,
    ) -> Unit)? = null,
) {
    Title {
        Icon("dropdown")
        Text(title)
    }
    Content {
        content?.invoke(this) { classes("transition", "hidden") }
    }
}
