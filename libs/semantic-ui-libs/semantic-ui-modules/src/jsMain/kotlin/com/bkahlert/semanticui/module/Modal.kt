package com.bkahlert.semanticui.module

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.kommons.js.ConsoleLogger
import com.bkahlert.semanticui.core.attributes.Modifier
import com.bkahlert.semanticui.core.attributes.Modifier.Variation
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.ApproveAction
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.DenyAction
import com.bkahlert.semanticui.core.attributes.StatesScope
import com.bkahlert.semanticui.core.attributes.VariationsScope
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticDivElement
import com.bkahlert.semanticui.core.dom.SemanticElement
import com.bkahlert.semanticui.core.dom.SemanticElementScope
import com.bkahlert.semanticui.element.ButtonElement
import kotlinx.browser.document
import kotlinx.dom.createElement
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


public external interface SemanticModal : SemanticModule {
    public fun modal(behavior: String, vararg args: Any?): dynamic
}

public fun Element.modal(settings: SemanticModalSettings): SemanticModal = SemanticUI.create(this, "modal", settings)


public external interface SemanticModalSettings : SemanticModuleSettings {

    // true will blur popups inside the debug mode, too
    public var blurring: Boolean?

    /** When `true`, the first form input inside the modal will receive focus when shown. Set this to `false` to prevent this behavior. */
    public var autofocus: Boolean?

    /** Whether to center content vertically. */
    public var centered: Boolean?

    /** Setting to `false` will not allow you to close the modal by clicking on the dimmer. */
    public var closable: Boolean?

    /** If set to `true` will not close other visible modals when opening a new one. */
    public var allowMultiple: Boolean?

    /** Selector or [jQuery] object specifying the area to dim. */
    public var context: String?

    /**
     * Is called when a modal starts to show.
     * *Scope: Modal*
     */
    public var onShow: (() -> Unit)?

    /**
     * Is called after a modal has finished showing animating.
     * *Scope: Modal*
     */
    public var onVisible: (() -> Unit)?

    /**
     * Is called after a modal starts to hide. If the function returns `false`, the modal will not hide.
     * *Scope: Modal*
     */
    public var onHide: ((JQuery<HTMLElement>) -> Boolean)?

    /**
     * Is called after an [Variation.ApproveAction] occurs. If the function returns `false`, the modal will not hide.
     * *Scope: Click*
     */
    public var onApprove: ((JQuery<HTMLElement>) -> Boolean)?

    /**
     * Is called after a [Variation.DenyAction] occurs. If the function returns `false` the modal will not hide.
     * *Scope: Modal*
     */
    public var onDeny: ((JQuery<HTMLElement>) -> Boolean)?
}

// @formatter:off
/** Shows the modal */
public inline fun SemanticModal.show(): SemanticModal = modal("show").unsafeCast<SemanticModal>()
/** Hides the modal */
public inline fun SemanticModal.hide(noinline callback: (() -> Unit)? = null): SemanticModal = modal("hide", callback).unsafeCast<SemanticModal>()
/** Toggles the modal */
public inline fun SemanticModal.toggle(): SemanticModal = modal("toggle").unsafeCast<SemanticModal>()
/** Refreshes centering of modal on page */
public inline fun SemanticModal.refresh(): SemanticModal = modal("refresh").unsafeCast<SemanticModal>()
/** Shows associated page dimmer */
public inline fun SemanticModal.showDimmer(): SemanticModal = modal("show dimmer").unsafeCast<SemanticModal>()
/** Hides associated page dimmer */
public inline fun SemanticModal.hideDimmer(): SemanticModal = modal("hide dimmer").unsafeCast<SemanticModal>()
/** Hides all modals not selected modal in a dimmer */
public inline fun SemanticModal.hideOthers(noinline callback: (() -> Unit)? = null): SemanticModal = modal("hide others", callback).unsafeCast<SemanticModal>()
/** Hides all visible modals in the same dimmer */
public inline fun SemanticModal.hideAll(noinline callback: (() -> Unit)? = null): SemanticModal = modal("hide all", callback).unsafeCast<SemanticModal>()
/** Caches current modal size */
public inline fun SemanticModal.cacheSizes(): SemanticModal = modal("cache sizes").unsafeCast<SemanticModal>()
/** Returns whether the modal can fit on the page */
public inline fun SemanticModal.canFit():Boolean = modal("can fit").unsafeCast<Boolean>()
/** Returns whether the modal is active */
public inline fun SemanticModal.isActive():Boolean = modal("is active").unsafeCast<Boolean>()
/** Sets modal to active */
public inline fun SemanticModal.setActive(): SemanticModal = modal("set active").unsafeCast<SemanticModal>()
public inline fun SemanticModal.destroy(): dynamic = modal("destroy")
// @formatter:on

private fun <T, R> ((T) -> R)?.overruleBy(overrulingValue: () -> R): (T) -> R = {
    this?.invoke(it)
    overrulingValue()
}

/**
 * Creates a [SemanticUI modal](https://semantic-ui.com/modules/modal.html#modal).
 */
@Composable
public fun Modal(
    attrs: SemanticModuleAttrBuilderContext<ModalElement, SemanticModalSettings>? = null,
    content: SemanticModuleContentBuilder<ModalElement, SemanticModalSettings>? = null,
) {
    val logger = remember { ConsoleLogger("Modal") }
    var closing by remember { mutableStateOf(false) }
    SemanticModuleElement<ModalElement, SemanticModalSettings>({
        classes("ui")
        attrs?.invoke(this)
        classes("modal")
        settings {
            closable = false // "true" not easily supported; would need to propagate the event and stop Semantic UI from closing modal itself
            allowMultiple = true // "false" not easily supported as it would also make Semantic UI close dialogs on its own
            onApprove = onApprove.overruleBy { closing }
            onDeny = onDeny.overruleBy { closing }
            onHide = onHide.overruleBy { closing }
        }
    }) {
        content?.invoke(this)
        DisposableEffect(settings) {
            // Semantic UI moves the modal element to the dimmer element,
            // but Compose expects the modal element where it was created.
            // Luckily, Compose seems to accept an invisible placeholder instead.
            val scopeParent = checkNotNull(scopeElement.parentElement) { "missing parent" }
            val scopeSibling = scopeElement.nextSibling
            val placeholder = document.createElement("span") { unsafeCast<HTMLElement>().hideVisually() }
            logger.debug("Adding placeholder", placeholder, "before", scopeSibling)
            scopeParent.insertBefore(node = placeholder, child = scopeSibling)

            logger.debug("Showing", scopeElement)
            val modalElement = scopeElement
                .modal(settings)
                .show()

            onDispose {
                logger.debug("Disposing", modalElement)
                closing = true
                modalElement.hide {
                    logger.debug("Destroying", modalElement)
                    modalElement.destroy()

                    logger.debug("Removing", modalElement)
                    modalElement.asDynamic().remove()

                    logger.debug("Disposed", modalElement)
                }
            }
        }
    }
}

/**
 * Creates a [SemanticUI basic modal](https://semantic-ui.com/modules/modal.html#basic).
 */
@Composable
public fun BasicModal(
    attrs: SemanticModuleAttrBuilderContext<ModalElement, SemanticModalSettings>? = null,
    content: SemanticModuleContentBuilder<ModalElement, SemanticModalSettings>? = null,
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
