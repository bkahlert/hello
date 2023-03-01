package com.bkahlert.semanticui.custom

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.attributes.SemanticAttrsScope
import com.bkahlert.semanticui.core.dom.SemanticElement
import com.bkahlert.semanticui.element.Loader
import com.bkahlert.semanticui.element.LoaderElement
import com.bkahlert.semanticui.element.active
import com.bkahlert.semanticui.element.disabled
import com.bkahlert.semanticui.element.indeterminate
import com.bkahlert.semanticui.module.Dimmer
import com.bkahlert.semanticui.module.DimmerElement
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.minHeight
import org.jetbrains.compose.web.dom.ElementScope
import org.w3c.dom.Element

/**
 * Utility to upgrade UI elements with a loading capability.
 *
 * **Usage:**
 * - add a `loadingState` parameter of type [LoadingState] to your `@Composable`
 * - invoke the [apply] extension function on the [AttrsScope] and [ElementScope] of the element
 *   that should indicate the loading state
 */
public sealed class LoadingState(
    public val dimmableAttrs: AttrsScope<Element>.(
        dimmableAttrs: DimmableAttrsBuilderContext?,
    ) -> Unit,
    public val dimmableContent: @Composable ElementScope<Element>.(
        DimmableContentAttrsBuilderContext<DimmerElement>?,
        DimmableContentAttrsBuilderContext<LoaderElement>?,
        String?,
    ) -> Unit,
) {

    public object On : LoadingState(
        { dimmableAttrs ->
            dimmableAttrs?.invoke(this, On)
            classes("dimmable", "dimmed")
        },
        { dimmerAttrs, loaderAttrs, loaderText ->
            Dimmer({
                dimmerAttrs?.invoke(this, On)
                classes("simple")
            }) {
                Loader(loaderText) {
                    loaderAttrs?.invoke(this, On)
                    s.active()
                }
            }
        },
    )

    public object Indeterminate : LoadingState(
        { dimmableAttrs ->
            dimmableAttrs?.invoke(this, Indeterminate)
            classes("dimmable", "dimmed")
        },
        { dimmerAttrs, loaderAttrs, loaderText ->
            Dimmer({
                dimmerAttrs?.invoke(this, Indeterminate)
                classes("simple")
            }) {
                Loader(loaderText) {
                    loaderAttrs?.invoke(this, Indeterminate)
                    s.indeterminate()
                }
            }
        },
    )

    public object Off : LoadingState(
        { dimmableAttrs ->
            dimmableAttrs?.invoke(this, Off)
            classes("dimmable")
        },
        { dimmerAttrs, loaderAttrs, loaderText ->
            Dimmer({
                dimmerAttrs?.invoke(this, Off)
                classes("simple")
            }) {
                Loader(loaderText) {
                    loaderAttrs?.invoke(this, Off)
                    s.disabled()
                }
            }
        },
    )
}

/** Function that can be used to manipulate the dimmable [Element] attributes depending on the applied [LoadingState]. */
public typealias DimmableAttrsBuilderContext = AttrsScope<Element>.(LoadingState) -> Unit
/** Function that can be used to manipulate the corresponding [SemanticElement] attributes depending on the applied [LoadingState]. */
public typealias DimmableContentAttrsBuilderContext<TSemantic> = SemanticAttrsScope<TSemantic>.(LoadingState) -> Unit

/**
 * Applies the [loadingState] to the [AttrsScope] of this element.
 */
public fun AttrsScope<Element>.apply(
    loadingState: LoadingState,
    dimmableAttrs: DimmableAttrsBuilderContext? = { if (it != LoadingState.Off) style { minHeight("5em") } },
) {
    loadingState.dimmableAttrs(this, dimmableAttrs)
}

/**
 * Applies the [loadingState] to the [ElementScope] of this element.
 *
 * The loader can be customized with [loaderAttrs].
 */
@Composable
public fun ElementScope<Element>.apply(
    loadingState: LoadingState,
    dimmerAttrs: DimmableContentAttrsBuilderContext<DimmerElement>? = null,
    loaderText: String? = null,
    loaderAttrs: DimmableContentAttrsBuilderContext<LoaderElement>? = null,
) {
    loadingState.dimmableContent(this, dimmerAttrs, loaderAttrs, loaderText)
}
