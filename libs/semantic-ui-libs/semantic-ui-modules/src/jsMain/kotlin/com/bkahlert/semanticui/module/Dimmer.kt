package com.bkahlert.semanticui.module

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.bkahlert.semanticui.core.attributes.Modifier.State.Active
import com.bkahlert.semanticui.core.attributes.Modifier.State.Disabled
import com.bkahlert.semanticui.core.attributes.Modifier.Variation
import com.bkahlert.semanticui.core.attributes.StatesScope
import com.bkahlert.semanticui.core.attributes.VariationsScope
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticDivElement
import com.bkahlert.semanticui.core.dom.SemanticElement
import kotlinx.dom.hasClass
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement

public interface DimmerElement : SemanticElement<HTMLDivElement>
public interface DimmerContentElement : SemanticElement<HTMLDivElement>

/** [State.Active](https://semantic-ui.com/modules/dimmer.html#active) */
public fun StatesScope<DimmerElement>.active(): StatesScope<DimmerElement> = +Active

/** [State.Disabled](https://semantic-ui.com/modules/dimmer.html#disabled) */
public fun StatesScope<DimmerElement>.disabled(): StatesScope<DimmerElement> = +Disabled

/**
 * [Variation.Blurring](https://semantic-ui.com/modules/dimmer.html#blurring)
 * that needs to be applied to the [dimmable] content of a [Dimmer].
 * That is, the parent [SemanticElement] of the dimmer.
 */
public fun VariationsScope<SemanticElement<Element>>.blurring(): VariationsScope<SemanticElement<Element>> = +Variation.Blurring

/**
 * [Variation.Dimmable](https://semantic-ui.com/modules/dimmer.html#/usage)
 * that needs to be applied to the enclosing content of a [Dimmer] if [jQuery.dimmer] is invoked on the `.dimmer` and not on the `.dimmable`.
 */
public fun VariationsScope<SemanticElement<Element>>.dimmable(): VariationsScope<SemanticElement<Element>> = +Variation.Dimmable

/** [Variation.VerticallyAligned](https://semantic-ui.com/modules/dimmer.html#vertically-aligned) */
public fun VariationsScope<DimmerElement>.verticallyAligned(value: Variation.VerticallyAligned): VariationsScope<DimmerElement> = +value

/**
 * [Variation.Inverted](https://semantic-ui.com/modules/dimmer.html#inverted)
 * that can be applied to the [Dimmer] itself and its content.
 */
public fun VariationsScope<SemanticElement<Element>>.inverted(): VariationsScope<SemanticElement<Element>> = +Variation.Inverted


public external interface SemanticDimmer : SemanticModule {
    public fun dimmer(behavior: String, vararg args: Any?): dynamic
}

public fun Element.dimmer(settings: SemanticDimmerSettings): SemanticDimmer = SemanticUI.create(this, "dimmer", settings)

public external interface SemanticDimmerSettings : SemanticModuleSettings {
    /** Callback on element show */
    public var onShow: (() -> Unit)?

    /** Callback on element hide */
    public var onHide: (() -> Unit)?

    /** Callback on element show or hide */
    public var onChange: (() -> Unit)?
}

// @formatter:off
/** Shows the dimmer */
public inline fun SemanticDimmer.show(noinline callback: (() -> Unit)? = null): SemanticDimmer = dimmer("show", callback).unsafeCast<SemanticDimmer>()
/** Hides the dimmer */
public inline fun SemanticDimmer.hide(noinline callback: (() -> Unit)? = null): SemanticDimmer = dimmer("hide", callback).unsafeCast<SemanticDimmer>()
public inline fun SemanticDimmer.destroy(): dynamic = dimmer("destroy")
// @formatter:on


/**
 * Creates a simple [SemanticUI dimmer](https://semantic-ui.com/modules/dimmer.html#dimmer) or
 * a simple [SemanticUI content dimmer](https://semantic-ui.com/modules/dimmer.html#content-dimmer) if [content] is specified.
 */
@Composable
public fun SimpleDimmer(
    attrs: SemanticAttrBuilderContext<DimmerElement>? = null,
    content: SemanticContentBuilder<DimmerContentElement>? = null,
) {
    SemanticDivElement<DimmerElement>({
        classes("ui")
        attrs?.invoke(this)
        classes("dimmer")
    }) {
        if (content != null) {
            SemanticDivElement<DimmerContentElement>({ classes("content") }, content)
        }
    }
}

/**
 * Creates a [SemanticUI dimmer](https://semantic-ui.com/modules/dimmer.html#dimmer) or
 * a [SemanticUI content dimmer](https://semantic-ui.com/modules/dimmer.html#content-dimmer) if [content] is specified.
 */
@Composable
public fun Dimmer(
    attrs: SemanticModuleAttrBuilderContext<DimmerElement, SemanticDimmerSettings>? = null,
    content: SemanticContentBuilder<DimmerContentElement>? = null,
) {
    SemanticModuleElement<DimmerElement, SemanticDimmerSettings>({
        classes("ui")
        attrs?.invoke(this)
        classes("dimmer")
    }) {
        DisposableEffect(settings) {
            val dimmerElement = scopeElement.dimmer(settings)
            if (scopeElement.hasClass("active")) dimmerElement.show()
            onDispose {
                dimmerElement.hide {
                    dimmerElement.destroy()
                    dimmerElement.asDynamic().remove()
                    Unit
                }
            }
        }
        if (content != null) {
            SemanticDivElement<DimmerContentElement>({ classes("content") }, content)
        }
    }
}

/**
 * Creates a [SemanticUI page dimmer](https://semantic-ui.com/modules/dimmer.html#page-dimmer).
 */
@Composable
public fun PageDimmer(
    attrs: SemanticModuleAttrBuilderContext<DimmerElement, SemanticDimmerSettings>? = null,
    content: SemanticContentBuilder<DimmerContentElement>? = null,
) {
    Dimmer({
        classes("page")
        attrs?.invoke(this)
    }, content)
}
