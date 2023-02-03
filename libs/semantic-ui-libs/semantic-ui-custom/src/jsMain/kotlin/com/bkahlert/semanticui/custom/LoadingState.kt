package com.bkahlert.semanticui.custom

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.element.Loader
import com.bkahlert.semanticui.element.LoaderElement
import com.bkahlert.semanticui.element.active
import com.bkahlert.semanticui.element.disabled
import com.bkahlert.semanticui.element.indeterminate
import com.bkahlert.semanticui.module.Dimmer
import com.bkahlert.semanticui.module.DimmerElement
import org.jetbrains.compose.web.attributes.AttrsScope
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
    public val dimmableAttrs: AttrsScope<Element>.() -> Unit,
    public val dimmableContent: @Composable ElementScope<Element>.(
        SemanticAttrBuilderContext<DimmerElement>?,
        SemanticAttrBuilderContext<LoaderElement>?,
        String?,
    ) -> Unit,
) {

    public object On : LoadingState(
        { classes("dimmable", "dimmed") },
        { dimmerAttrs, loaderAttrs, loaderText ->
            Dimmer({
                dimmerAttrs?.invoke(this)
                classes("simple")
            }) {
                Loader(loaderText) {
                    loaderAttrs?.invoke(this)
                    s.active()
                }
            }
        },
    )

    public object Indeterminate : LoadingState(
        { classes("dimmable", "dimmed") },
        { dimmerAttrs, loaderAttrs, loaderText ->
            Dimmer({
                dimmerAttrs?.invoke(this)
                classes("simple")
            }) {
                Loader(loaderText) {
                    loaderAttrs?.invoke(this)
                    s.indeterminate()
                }
            }
        },
    )

    public object Off : LoadingState(
        { classes("dimmable") },
        { dimmerAttrs, loaderAttrs, loaderText ->
            Dimmer({
                dimmerAttrs?.invoke(this)
                classes("simple")
            }) {
                Loader(loaderText) {
                    loaderAttrs?.invoke(this)
                    s.disabled()
                }
            }
        },
    )
}

/**
 * Applies the [loadingState] to the [AttrsScope] of this element.
 */
public fun AttrsScope<Element>.apply(
    loadingState: LoadingState,
) {
    loadingState.dimmableAttrs(this)
}

/**
 * Applies the [loadingState] to the [ElementScope] of this element.
 *
 * The loader can be customized with [loaderAttrs].
 */
@Composable
public fun ElementScope<Element>.apply(
    loadingState: LoadingState,
    dimmerAttrs: SemanticAttrBuilderContext<DimmerElement>? = null,
    loaderText: String? = null,
    loaderAttrs: SemanticAttrBuilderContext<LoaderElement>? = null,
) {
    loadingState.dimmableContent(this, dimmerAttrs, loaderAttrs, loaderText)
}
