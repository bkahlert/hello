package com.semanticui.compose.module

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.ui.hideVisually
import com.bkahlert.kommons.collections.pairArray
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.SemanticAttrsScope
import com.semanticui.compose.SemanticAttrsScope.Companion.or
import com.semanticui.compose.SemanticBuilder
import com.semanticui.compose.SemanticDivElement
import com.semanticui.compose.SemanticElement
import com.semanticui.compose.SemanticElementScope
import com.semanticui.compose.Variation
import com.semanticui.compose.Variation.Size
import com.semanticui.compose.element.Button
import com.semanticui.compose.element.ButtonElement
import com.semanticui.compose.element.ButtonElementType
import com.semanticui.compose.jQuery
import com.semanticui.compose.modal
import kotlinx.browser.document
import org.jetbrains.compose.web.attributes.AttrsScopeBuilder
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

interface ModalElement : SemanticElement

/** [Full Screen](https://semantic-ui.com/modules/modal.html#full-screen) variation of a [input](https://semantic-ui.com/modules/modal.html). */
@Suppress("unused") val <TSemantic : ModalElement> SemanticAttrsScope<TSemantic, *>.fullScreen: Variation get() = Variation.FullScreen

/** [Size](https://semantic-ui.com/modules/modal.html#size) variation of a [input](https://semantic-ui.com/modules/modal.html). */
@Suppress("unused") val <TSemantic : ModalElement> SemanticAttrsScope<TSemantic, *>.size: Size get() = Variation.Size

/** [Longer](https://semantic-ui.com/modules/modal.html#longer) variation of a [input](https://semantic-ui.com/modules/modal.html). */
@Suppress("unused") val <TSemantic : ModalElement> SemanticAttrsScope<TSemantic, *>.longer: Variation get() = Variation.Longer

// true will blur popups inside the debug mode, too
var <TSemantic : ModalElement> SemanticAttrsScope<TSemantic, *>.blurring: Boolean? by SemanticAttrsScope or null

/** When `true`, the first form input inside the modal will receive focus when shown. Set this to `false` to prevent this behavior. */
var <TSemantic : ModalElement> SemanticAttrsScope<TSemantic, *>.autofocus: Boolean? by SemanticAttrsScope or null

/** Whether to vertically center content. */
var <TSemantic : ModalElement> SemanticAttrsScope<TSemantic, *>.centered: Boolean? by SemanticAttrsScope or null

/** Setting to `false` will not allow you to close the modal by clicking on the dimmer. */
var <TSemantic : ModalElement> SemanticAttrsScope<TSemantic, *>.closable: Boolean? by SemanticAttrsScope or null

/** If set to `true` will not close other visible modals when opening a new one. */
var <TSemantic : ModalElement> SemanticAttrsScope<TSemantic, *>.allowMultiple: Boolean? by SemanticAttrsScope or null

/** Selector or [jQuery] object specifying the area to dim. */
var <TSemantic : ModalElement> SemanticAttrsScope<TSemantic, *>.context: String? by SemanticAttrsScope or null

/**
 * Is called when a modal starts to show.
 * *Scope: Modal*
 */
var <TSemantic : ModalElement> SemanticAttrsScope<TSemantic, *>.onShow: (() -> Unit)? by SemanticAttrsScope or null

/**
 * Is called after a modal has finished showing animating.
 * *Scope: Modal*
 */
var <TSemantic : ModalElement> SemanticAttrsScope<TSemantic, *>.onVisible: (() -> Unit)? by SemanticAttrsScope or null

/**
 * Is called after a modal starts to hide. If the function returns `false`, the modal will not hide.
 * *Scope: Modal*
 */
var <TSemantic : ModalElement> SemanticAttrsScope<TSemantic, *>.onHide: ((jQuery) -> Boolean)? by SemanticAttrsScope or null

/**
 * Is called after an [ApproveButton] is pressed. If the function returns `false`, the modal will not hide.
 * *Scope: Click*
 */
var <TSemantic : ModalElement> SemanticAttrsScope<TSemantic, *>.onApprove: ((jQuery) -> Boolean)? by SemanticAttrsScope or null

/**
 * Is called after a [DenyButton] is pressed. If the function returns `false` the modal will not hide.
 * *Scope: Modal*
 */
var <TSemantic : ModalElement> SemanticAttrsScope<TSemantic, *>.onDeny: ((jQuery) -> Boolean)? by SemanticAttrsScope or null

private fun <T, R> ((T) -> R)?.overruleBy(overrulingValue: () -> R): (T) -> R = {
    this?.invoke(it)
    overrulingValue()
}

/**
 * Creates a [SemanticUI modal](https://semantic-ui.com/modules/modal.html#modal).
 */
@Composable
fun Modal(
    attrs: SemanticAttrBuilder<ModalElement, HTMLDivElement>? = null,
    content: SemanticBuilder<ModalElement, HTMLDivElement>? = null,
) {
    var closing by remember { mutableStateOf(false) }
    val scope = SemanticAttrsScope.of<ModalElement, HTMLDivElement>(AttrsScopeBuilder()).apply {
        attrs?.invoke(this)
        closable = false // "true" not easily supported; would need to propagate the event and stop Semantic UI from closing modal itself
        allowMultiple = true // "false" not easily supported as it would also make Semantic UI close dialogs on its own
        onApprove = onApprove.overruleBy { closing }
        onDeny = onDeny.overruleBy { closing }
        onHide = onHide.overruleBy { closing }
    }
    SemanticDivElement<ModalElement>({
        classes("ui")
        attrs?.invoke(this)
        classes("modal")
    }) {
        content?.invoke(this)
        DisposableEffect(Unit) {
            // Semantic UI will move the node to the dimmer element but Compose expects the node to be here
            // in order to dispose it when no longer needed. Let's insert an invisible replacement instead.
            val insertionPoint = checkNotNull(scopeElement.parentElement) { "missing parent" } to scopeElement.nextSibling
            val jQueryElement = jQuery(scopeElement)
            jQueryElement
                .modal(*scope.settings.pairArray)
                .modal("show")
            val invisiblePlaceholder = (document.createElement("span") as HTMLElement).apply { hideVisually() }
            insertionPoint.first.insertBefore(invisiblePlaceholder, insertionPoint.second)
            onDispose {
                closing = true
                jQueryElement
                    .modal("onHidden" to { jQueryElement.asDynamic().remove() })
                    .modal("hide")
            }
        }
    }
}

/**
 * Creates a [SemanticUI basic modal](https://semantic-ui.com/modules/modal.html#basic).
 */
@Composable
fun BasicModal(
    attrs: SemanticAttrBuilder<ModalElement, HTMLDivElement>? = null,
    content: SemanticBuilder<ModalElement, HTMLDivElement>? = null,
) = Modal({ attrs?.invoke(this);classes("basic") }, content)

/**
 * Creates a [header](https://semantic-ui.com/modules/modal.html#header) element for a [SemanticUI modal](https://semantic-ui.com/modules/modal.html).
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<ModalElement, *>.Header(
    attrs: SemanticAttrBuilder<ModalElement, HTMLDivElement>? = null,
    content: SemanticBuilder<ModalElement, HTMLDivElement>? = null,
) = SemanticDivElement({ attrs?.invoke(this); classes("header") }, content)


interface ModalContentElement : SemanticElement

/** [Scrolling](https://semantic-ui.com/modules/modal.html#scrolling-content) variation of a [input](https://semantic-ui.com/modules/modal.html). */
@Suppress("unused") val <TSemantic : ModalContentElement> SemanticAttrsScope<TSemantic, *>.scrolling: Variation get() = Variation.Scrolling

/**
 * Creates a [content](https://semantic-ui.com/modules/modal.html#content) element for a [SemanticUI modal](https://semantic-ui.com/modules/modal.html).
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<ModalElement, *>.Content(
    attrs: SemanticAttrBuilder<ModalContentElement, HTMLDivElement>? = null,
    content: SemanticBuilder<ModalContentElement, HTMLDivElement>? = null,
) = SemanticDivElement({ attrs?.invoke(this); classes("content") }, content)

interface ModalActionsElement : ModalElement

/**
 * Creates [actions](https://semantic-ui.com/modules/modal.html#actions) element for a [SemanticUI modal](https://semantic-ui.com/modules/modal.html).
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<ModalElement, *>.Actions(
    attrs: SemanticAttrBuilder<ModalActionsElement, HTMLDivElement>? = null,
    content: SemanticBuilder<ModalActionsElement, HTMLDivElement>? = null,
) = SemanticDivElement({ attrs?.invoke(this); classes("actions") }, content)

/**
 * Creates an approval button for modal [actions](https://semantic-ui.com/modules/modal.html#actions)
 * with the specified type.
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<ModalActionsElement, *>.ApproveButton(
    type: ButtonElementType?,
    attrs: SemanticAttrBuilder<ButtonElement, HTMLDivElement>? = null,
    content: SemanticBuilder<ButtonElement, HTMLDivElement>? = null,
) = Button(type, { attrs?.invoke(this); +Actions.Approve }, content)

/**
 * Creates an approval button for modal [actions](https://semantic-ui.com/modules/modal.html#actions).
 */
@Composable
fun SemanticElementScope<ModalActionsElement, *>.ApproveButton(
    attrs: SemanticAttrBuilder<ButtonElement, HTMLDivElement>? = null,
    content: SemanticBuilder<ButtonElement, HTMLDivElement>? = null,
) = ApproveButton(null, attrs, content)


/**
 * Creates a deny button for modal [actions](https://semantic-ui.com/modules/modal.html#actions)
 * with the specified type.
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<ModalActionsElement, *>.DenyButton(
    type: ButtonElementType?,
    attrs: SemanticAttrBuilder<ButtonElement, HTMLDivElement>? = null,
    content: SemanticBuilder<ButtonElement, HTMLDivElement>? = null,
) = Button(type, { attrs?.invoke(this); +Actions.Deny }, content)

/**
 * Creates a deny button for modal [actions](https://semantic-ui.com/modules/modal.html#actions).
 */
@Composable
fun SemanticElementScope<ModalActionsElement, *>.DenyButton(
    attrs: SemanticAttrBuilder<ButtonElement, HTMLDivElement>? = null,
    content: SemanticBuilder<ButtonElement, HTMLDivElement>? = null,
) = DenyButton(null, attrs, content)
