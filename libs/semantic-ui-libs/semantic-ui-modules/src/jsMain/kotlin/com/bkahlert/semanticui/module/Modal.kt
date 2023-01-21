package com.bkahlert.semanticui.module

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.semanticui.core.attributes.BehaviorScope
import com.bkahlert.semanticui.core.attributes.Modifier
import com.bkahlert.semanticui.core.attributes.Modifier.Variation
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.ApproveAction
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.DenyAction
import com.bkahlert.semanticui.core.attributes.Setting
import com.bkahlert.semanticui.core.attributes.StatesScope
import com.bkahlert.semanticui.core.attributes.VariationsScope
import com.bkahlert.semanticui.core.attributes.getValue
import com.bkahlert.semanticui.core.attributes.setValue
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticDivElement
import com.bkahlert.semanticui.core.dom.SemanticElement
import com.bkahlert.semanticui.core.dom.SemanticElementScope
import com.bkahlert.semanticui.core.jQuery
import com.bkahlert.semanticui.core.modal
import com.bkahlert.semanticui.element.ButtonElement
import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

public interface ModalElement : SemanticElement<HTMLDivElement>

/** [Variation.FullScreen](https://semantic-ui.com/modules/modal.html#active) */
public fun StatesScope<ModalElement>.active(): StatesScope<ModalElement> = +Modifier.State.Active

/** [Variation.FullScreen](https://semantic-ui.com/modules/modal.html#full-screen) */
public fun VariationsScope<ModalElement>.fullScreen(): VariationsScope<ModalElement> = +Variation.FullScreen

/** [Variation.Size](https://semantic-ui.com/modules/modal.html#size) */
public fun VariationsScope<ModalElement>.size(value: Variation.Size): VariationsScope<ModalElement> = +value

// true will blur popups inside the debug mode, too
public var BehaviorScope<ModalElement>.blurring: Boolean? by Setting()

/** When `true`, the first form input inside the modal will receive focus when shown. Set this to `false` to prevent this behavior. */
public var BehaviorScope<ModalElement>.autofocus: Boolean? by Setting()

/** Whether to center content vertically. */
public var BehaviorScope<ModalElement>.centered: Boolean? by Setting()

/** Setting to `false` will not allow you to close the modal by clicking on the dimmer. */
public var BehaviorScope<ModalElement>.closable: Boolean? by Setting()

/** If set to `true` will not close other visible modals when opening a new one. */
public var BehaviorScope<ModalElement>.allowMultiple: Boolean? by Setting()

/** Selector or [jQuery] object specifying the area to dim. */
public var BehaviorScope<ModalElement>.context: String? by Setting()

/**
 * Is called when a modal starts to show.
 * *Scope: Modal*
 */
public var BehaviorScope<ModalElement>.onShow: (() -> Unit)? by Setting()

/**
 * Is called after a modal has finished showing animating.
 * *Scope: Modal*
 */
public var BehaviorScope<ModalElement>.onVisible: (() -> Unit)? by Setting()

/**
 * Is called after a modal starts to hide. If the function returns `false`, the modal will not hide.
 * *Scope: Modal*
 */
public var BehaviorScope<ModalElement>.onHide: ((jQuery) -> Boolean)? by Setting()

/**
 * Is called after an [Variation.ApproveAction] occurs. If the function returns `false`, the modal will not hide.
 * *Scope: Click*
 */
public var BehaviorScope<ModalElement>.onApprove: ((jQuery) -> Boolean)? by Setting()

/**
 * Is called after a [Variation.DenyAction] occurs. If the function returns `false` the modal will not hide.
 * *Scope: Modal*
 */
public var BehaviorScope<ModalElement>.onDeny: ((jQuery) -> Boolean)? by Setting()

private fun <T, R> ((T) -> R)?.overruleBy(overrulingValue: () -> R): (T) -> R = {
    this?.invoke(it)
    overrulingValue()
}

/**
 * Creates a [SemanticUI modal](https://semantic-ui.com/modules/modal.html#modal).
 */
@Composable
public fun Modal(
    attrs: SemanticAttrBuilderContext<ModalElement>? = null,
    content: SemanticContentBuilder<ModalElement>? = null,
) {
    var closing by remember { mutableStateOf(false) }
    val settings = mutableMapOf<String, Any?>()
    SemanticDivElement<ModalElement>(
        behaviorSettings = settings,
        semanticAttrs = {
            classes("ui")
            attrs?.invoke(this)
            classes("modal")
            b.closable = false // "true" not easily supported; would need to propagate the event and stop Semantic UI from closing modal itself
            b.allowMultiple = true // "false" not easily supported as it would also make Semantic UI close dialogs on its own
            b.onApprove = b.onApprove.overruleBy { closing }
            b.onDeny = b.onDeny.overruleBy { closing }
            b.onHide = b.onHide.overruleBy { closing }
        }) {
        content?.invoke(this)
        DisposableEffect(Unit) {
            // Semantic UI will move the node to the dimmer element but Compose expects the node to be here
            // in order to dispose it when no longer needed. Let's insert an invisible replacement instead.
            val insertionPoint = checkNotNull(scopeElement.parentElement) { "missing parent" } to scopeElement.nextSibling
            val jQueryElement = jQuery(scopeElement)
            jQueryElement
                .modal(settings)
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
public fun BasicModal(
    attrs: SemanticAttrBuilderContext<ModalElement>? = null,
    content: SemanticContentBuilder<ModalElement>? = null,
): Unit = Modal({
    attrs?.invoke(this)
    classes("basic")
}, content)

/**
 * Creates a [header](https://semantic-ui.com/modules/modal.html#header) element for a [SemanticUI modal](https://semantic-ui.com/modules/modal.html).
 */
@Suppress("unused", "UnusedReceiverParameter")
@Composable
public fun SemanticElementScope<ModalElement>.Header(
    attrs: SemanticAttrBuilderContext<ModalElement>? = null,
    content: SemanticContentBuilder<ModalElement>? = null,
): Unit = SemanticDivElement({
    attrs?.invoke(this)
    classes("header")
}, content)


public interface ModalContentElement : SemanticElement<HTMLDivElement>

/** [Variation.Scrolling](https://semantic-ui.com/modules/modal.html#scrolling-content) */
public fun VariationsScope<ModalContentElement>.scrolling(): VariationsScope<ModalContentElement> = +Variation.Scrolling

/**
 * Creates a [content](https://semantic-ui.com/modules/modal.html#content) element for a [SemanticUI modal](https://semantic-ui.com/modules/modal.html).
 */
@Suppress("unused", "UnusedReceiverParameter")
@Composable
public fun SemanticElementScope<ModalElement>.Content(
    attrs: SemanticAttrBuilderContext<ModalContentElement>? = null,
    content: SemanticContentBuilder<ModalContentElement>? = null,
): Unit = SemanticDivElement({ attrs?.invoke(this); classes("content") }, content)


public interface ModalActionsElement : ModalElement


/**
 * Creates [actions](https://semantic-ui.com/modules/modal.html#actions) element for a [SemanticUI modal](https://semantic-ui.com/modules/modal.html).
 */
@Suppress("unused", "UnusedReceiverParameter")
@Composable
public fun SemanticElementScope<ModalElement>.Actions(
    attrs: SemanticAttrBuilderContext<ModalActionsElement>? = null,
    content: SemanticContentBuilder<ModalActionsElement>? = null,
): Unit = SemanticDivElement({ attrs?.invoke(this); classes("actions") }, content)


/** [Variation.ApproveAction](https://semantic-ui.com/modules/modal.html#actions) */
public fun VariationsScope<ButtonElement<Element>>.approve(value: ApproveAction = ApproveAction.Approve): VariationsScope<ButtonElement<Element>> = +value

/** [Variation.DenyAction](https://semantic-ui.com/modules/modal.html#actions) */
public fun VariationsScope<ButtonElement<Element>>.deny(value: DenyAction = DenyAction.Deny): VariationsScope<ButtonElement<Element>> = +value


/**
 * Visually hides this [HTMLElement].
 *
 * Screen readers treat these elements like any other visual element.
 *
 * @see <a href="https://css-tricks.com/html-inputs-and-labels-a-love-story/">HTML Inputs and Labels: A Love Story</a>
 */
private fun HTMLElement.hideVisually() {
    style.apply {
        position = "absolute"
        width = "1px"
        height = "1px"
        padding = "0"
        clip = "rect(1px, 1px, 1px, 1px)"
        borderWidth = "0"
        overflowX = "hidden"
        overflowY = "hidden"
        whiteSpace = "nowrap"
    }
}
