package com.semanticui.compose.module

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.SemanticBuilder
import com.semanticui.compose.SemanticDivElement
import com.semanticui.compose.SemanticElement
import com.semanticui.compose.SemanticElementScope
import com.semanticui.compose.jQuery
import com.semanticui.compose.modal
import kotlinx.browser.document
import kotlinx.dom.clear
import org.w3c.dom.HTMLDivElement

interface ModalElement : SemanticElement

/**
 * Creates a [SemanticUI modal](https://semantic-ui.com/modules/modal.html#/definition).
 */
@Composable
fun Modal(
    attrs: SemanticAttrBuilder<ModalElement, HTMLDivElement>? = null,
    vararg options: Pair<String, Any?>,
    content: SemanticBuilder<ModalElement, HTMLDivElement>? = null,
) {
    SemanticDivElement<ModalElement>({
        classes("ui")
        attrs?.invoke(this)
        classes("modal")
    }) {
        content?.invoke(this)
        DisposableEffect(Unit) {
            jQuery(scopeElement)
                .modal(*options)
                .modal("show")
            onDispose {
                jQuery(scopeElement)
                    .modal("hide")
                document.querySelector(".modals")?.clear()
            }
        }
    }
}

/**
 * Creates a [header](https://semantic-ui.com/modules/modal.html#header) element for a [SemanticUI modal](https://semantic-ui.com/modules/modal.html).
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<ModalElement, *>.Header(
    attrs: SemanticAttrBuilder<ModalElement, HTMLDivElement>? = null,
    content: SemanticBuilder<ModalElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes("header")
    }, content)
}

/**
 * Creates a [content](https://semantic-ui.com/modules/modal.html#content) element for a [SemanticUI modal](https://semantic-ui.com/modules/modal.html).
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<ModalElement, *>.Content(
    attrs: SemanticAttrBuilder<ModalElement, HTMLDivElement>? = null,
    content: SemanticBuilder<ModalElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes("content")
    }, content)
}
