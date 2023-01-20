package com.bkahlert.semanticui.module

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.semanticui.core.attributes.SemanticAttrsScope
import com.bkahlert.semanticui.core.attributes.SemanticAttrsScope.Companion.or
import com.bkahlert.semanticui.core.attributes.SemanticAttrsScopeBuilder
import com.bkahlert.semanticui.core.attributes.Variation
import com.bkahlert.semanticui.core.attributes.Variation.Actions.Approve
import com.bkahlert.semanticui.core.attributes.Variation.Actions.Deny
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticDivElement
import com.bkahlert.semanticui.core.dom.SemanticElement
import com.bkahlert.semanticui.core.dom.SemanticElementScope
import com.bkahlert.semanticui.core.jQuery
import com.bkahlert.semanticui.core.modal
import com.bkahlert.semanticui.element.Button
import com.bkahlert.semanticui.element.ButtonElement
import com.bkahlert.semanticui.element.ButtonElementType
import kotlinx.browser.document
import org.jetbrains.compose.web.attributes.AttrsScopeBuilder
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

public interface ModalElement : SemanticElement<HTMLDivElement>

/** [Variation.FullScreen](https://semantic-ui.com/modules/modal.html#full-screen) */
public fun SemanticAttrsScope<ModalElement>.v(value: Variation.FullScreen): Unit = variation(value)

/** [Variation.Size](https://semantic-ui.com/modules/modal.html#size) */
public fun SemanticAttrsScope<ModalElement>.v(value: Variation.Size): Unit = variation(value)

/** [Variation.Longer](https://semantic-ui.com/modules/modal.html#longer) */
public fun SemanticAttrsScope<ModalElement>.v(value: Variation.Longer): Unit = variation(value)


// true will blur popups inside the debug mode, too
public var <TSemantic : ModalElement> SemanticAttrsScope<TSemantic>.blurring: Boolean? by SemanticAttrsScope or null

/** When `true`, the first form input inside the modal will receive focus when shown. Set this to `false` to prevent this behavior. */
public var <TSemantic : ModalElement> SemanticAttrsScope<TSemantic>.autofocus: Boolean? by SemanticAttrsScope or null

/** Whether to vertically center content. */
public var <TSemantic : ModalElement> SemanticAttrsScope<TSemantic>.centered: Boolean? by SemanticAttrsScope or null

/** Setting to `false` will not allow you to close the modal by clicking on the dimmer. */
public var <TSemantic : ModalElement> SemanticAttrsScope<TSemantic>.closable: Boolean? by SemanticAttrsScope or null

/** If set to `true` will not close other visible modals when opening a new one. */
public var <TSemantic : ModalElement> SemanticAttrsScope<TSemantic>.allowMultiple: Boolean? by SemanticAttrsScope or null

/** Selector or [jQuery] object specifying the area to dim. */
public var <TSemantic : ModalElement> SemanticAttrsScope<TSemantic>.context: String? by SemanticAttrsScope or null

/**
 * Is called when a modal starts to show.
 * *Scope: Modal*
 */
public var <TSemantic : ModalElement> SemanticAttrsScope<TSemantic>.onShow: (() -> Unit)? by SemanticAttrsScope or null

/**
 * Is called after a modal has finished showing animating.
 * *Scope: Modal*
 */
public var <TSemantic : ModalElement> SemanticAttrsScope<TSemantic>.onVisible: (() -> Unit)? by SemanticAttrsScope or null

/**
 * Is called after a modal starts to hide. If the function returns `false`, the modal will not hide.
 * *Scope: Modal*
 */
public var <TSemantic : ModalElement> SemanticAttrsScope<TSemantic>.onHide: ((jQuery) -> Boolean)? by SemanticAttrsScope or null

/**
 * Is called after an [ApproveButton] is pressed. If the function returns `false`, the modal will not hide.
 * *Scope: Click*
 */
public var <TSemantic : ModalElement> SemanticAttrsScope<TSemantic>.onApprove: ((jQuery) -> Boolean)? by SemanticAttrsScope or null

/**
 * Is called after a [DenyButton] is pressed. If the function returns `false` the modal will not hide.
 * *Scope: Modal*
 */
public var <TSemantic : ModalElement> SemanticAttrsScope<TSemantic>.onDeny: ((jQuery) -> Boolean)? by SemanticAttrsScope or null

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
    val scope: SemanticAttrsScope<ModalElement> = SemanticAttrsScopeBuilder<ModalElement>(AttrsScopeBuilder()).apply {
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
                .modal(scope.settings)
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
): Unit = Modal({ attrs?.invoke(this);classes("basic") }, content)

/**
 * Creates a [header](https://semantic-ui.com/modules/modal.html#header) element for a [SemanticUI modal](https://semantic-ui.com/modules/modal.html).
 */
@Suppress("unused", "UnusedReceiverParameter")
@Composable
public fun SemanticElementScope<ModalElement>.Header(
    attrs: SemanticAttrBuilderContext<ModalElement>? = null,
    content: SemanticContentBuilder<ModalElement>? = null,
): Unit = SemanticDivElement({ attrs?.invoke(this); classes("header") }, content)


public interface ModalContentElement : SemanticElement<HTMLDivElement>

/** [Scrolling](https://semantic-ui.com/modules/modal.html#scrolling-content) variation of a [input](https://semantic-ui.com/modules/modal.html). */
@Suppress("unused", "UnusedReceiverParameter")
public val <TSemantic : ModalContentElement> SemanticAttrsScope<TSemantic>.scrolling: Variation get() = Variation.Scrolling

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

/**
 * Creates an approval button for modal [actions](https://semantic-ui.com/modules/modal.html#actions)
 * with the specified type.
 */
@Suppress("unused", "UnusedReceiverParameter")
@Composable
public fun SemanticElementScope<ModalActionsElement>.ApproveButton(
    type: ButtonElementType?,
    attrs: SemanticAttrBuilderContext<ButtonElement>? = null,
    content: SemanticContentBuilder<ButtonElement>? = null,
): Unit = Button(type, { attrs?.invoke(this); +Approve }, content)

/**
 * Creates an approval button for modal [actions](https://semantic-ui.com/modules/modal.html#actions).
 */
@Composable
public fun SemanticElementScope<ModalActionsElement>.ApproveButton(
    attrs: SemanticAttrBuilderContext<ButtonElement>? = null,
    content: SemanticContentBuilder<ButtonElement>? = null,
): Unit = ApproveButton(null, attrs, content)


/**
 * Creates a deny button for modal [actions](https://semantic-ui.com/modules/modal.html#actions)
 * with the specified type.
 */
@Suppress("unused", "UnusedReceiverParameter")
@Composable
public fun SemanticElementScope<ModalActionsElement>.DenyButton(
    type: ButtonElementType?,
    attrs: SemanticAttrBuilderContext<ButtonElement>? = null,
    content: SemanticContentBuilder<ButtonElement>? = null,
): Unit = Button(type, { attrs?.invoke(this); +Deny }, content)

/**
 * Creates a deny button for modal [actions](https://semantic-ui.com/modules/modal.html#actions).
 */
@Composable
public fun SemanticElementScope<ModalActionsElement>.DenyButton(
    attrs: SemanticAttrBuilderContext<ButtonElement>? = null,
    content: SemanticContentBuilder<ButtonElement>? = null,
): Unit = DenyButton(null, attrs, content)


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
