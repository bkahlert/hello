package com.bkahlert.semanticui.module

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticDivElement
import com.bkahlert.semanticui.core.dom.SemanticElement
import com.bkahlert.semanticui.core.dom.SemanticElementScope
import com.bkahlert.semanticui.element.Icon
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

public interface AccordionElement : SemanticElement<HTMLDivElement>


public external interface SemanticAccordion : SemanticModule

public fun Element.accordion(settings: SemanticAccordionSettings): SemanticAccordion = SemanticUI.create(this, "accordion", settings)

public external interface SemanticAccordionSettings : SemanticModuleSettings

/**
 * Creates a [SemanticUI icon](https://semantic-ui.com/modules/accordion.html#/definition).
 */
@Composable
public fun Accordion(
    attrs: SemanticAttrBuilderContext<AccordionElement>? = null,
    content: SemanticContentBuilder<AccordionElement>? = null,
) {
    SemanticModuleElement<AccordionElement, SemanticAccordionSettings>({
        classes("ui")
        attrs?.invoke(this)
        classes("accordion")
    }) {
        content?.invoke(this)
        DisposableEffect(Unit) {
            scopeElement.accordion(settings)
            onDispose { /* cleaned up by Accordion module automatically */ } // TODO check
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
 * Creates the elements [Title] with the specified [title] and content
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
