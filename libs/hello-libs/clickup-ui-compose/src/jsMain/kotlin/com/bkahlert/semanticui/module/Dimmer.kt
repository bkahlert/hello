package com.bkahlert.semanticui.module

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticDivElement
import com.bkahlert.semanticui.core.dom.SemanticElement
import kotlinx.dom.hasClass
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement

public interface DimmerElement : SemanticElement<HTMLDivElement>
public interface DimmerContentElement : SemanticElement<HTMLDivElement>


public external interface SemanticDimmer : SemanticModule {
    public fun dimmer(behavior: String, vararg args: Any?): dynamic
}

public fun Element.dimmer(settings: SemanticDimmerSettings): SemanticDimmer =
    SemanticUI.create(this, "dimmer", settings)

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
