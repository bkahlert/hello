package com.bkahlert.semanticui.module

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.kommons.js.ConsoleLogger
import com.bkahlert.semanticui.core.dom.SemanticElement
import kotlinx.browser.document
import kotlinx.dom.createElement
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

public interface ModalElement : SemanticElement<HTMLDivElement>


public external interface SemanticModal : SemanticModule {
    public fun modal(behavior: String, vararg args: Any?): dynamic
}

public fun Element.modal(settings: SemanticModalSettings): SemanticModal = SemanticUI.create(this, "modal", settings)


public external interface SemanticModalSettings : SemanticModuleSettings {

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
     * Is called after an ApproveAction occurs. If the function returns `false`, the modal will not hide.
     * *Scope: Click*
     */
    public var onApprove: ((JQuery<HTMLElement>) -> Boolean)?

    /**
     * Is called after a DenyAction occurs. If the function returns `false` the modal will not hide.
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
