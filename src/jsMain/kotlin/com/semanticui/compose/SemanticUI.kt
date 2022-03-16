package com.semanticui.compose

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.ElementScope
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement

interface SemanticElement
typealias SemanticAttrBuilder<S, T> = SemanticAttrsScope<S, T>.() -> Unit
typealias SemanticBuilder<S, T> = @Composable SemanticElementScope<S, T>.() -> Unit

interface SemanticAttrsScope<out TSemantic : SemanticElement, TElement : Element> : AttrsScope<TElement> {
    companion object {
        fun <TSemantic : SemanticElement, TElement : Element> of(attrsScope: AttrsScope<TElement>): SemanticAttrsScope<TSemantic, TElement> =
            object : SemanticAttrsScope<TSemantic, TElement>, AttrsScope<TElement> by attrsScope {}
    }

    fun variation(vararg variation: Variation) {
        variation.forEach { classes(*it.classNames) }
    }

    fun state(vararg states: State) {
        states.forEach { classes(*it.classNames) }
    }
}

interface SemanticElementScope<out TSemantic : SemanticElement, out TElement : Element> : ElementScope<TElement> {
    companion object {
        fun <TSemantic : SemanticElement, TElement : Element> of(elementScope: ElementScope<TElement>): SemanticElementScope<TSemantic, TElement> =
            object : SemanticElementScope<TSemantic, TElement>, ElementScope<TElement> by elementScope {}
    }
}

@Composable
fun <TSemantic : SemanticElement, TElement : Element> SemanticElement(
    attrs: SemanticAttrBuilder<TSemantic, TElement>? = null,
    content: SemanticBuilder<TSemantic, TElement>? = null,
    invoke: @Composable (AttrBuilderContext<TElement>?, ContentBuilder<TElement>?) -> Unit,
) {
    invoke({
        attrs?.invoke(SemanticAttrsScope.of(this))
    }, {
        content?.invoke(SemanticElementScope.of(this))
    })
}

@Composable
fun <TSemantic : SemanticElement> SemanticDivElement(
    attrs: SemanticAttrBuilder<TSemantic, HTMLDivElement>? = null,
    content: SemanticBuilder<TSemantic, HTMLDivElement>? = null,
) {
    SemanticElement(attrs, content) { a, c -> Div(a, c) }
}

@Deprecated("use Type, Variation or State directly")
interface Modifier {
    val classNames: Array<out String>

    companion object {
        fun of(vararg classNames: String) = object : Modifier {
            override val classNames: Array<out String> get() = classNames
        }
    }
}

inline val Array<out Modifier>.classNames: Array<out String>
    get() = flatMap { it.classNames.asIterable() }.toTypedArray()


object Empty : Modifier {
    override val classNames: Array<out String> = emptyArray()
}

sealed class Variation(override vararg val classNames: String) : Modifier {
    object Fitted : Variation("fitted")
    sealed class Size(className: String) : Variation(className) {
        object Mini : Size("mini")
        object Tiny : Size("tiny")
        object Small : Size("small")
        object Large : Size("large")
        object Big : Size("big")
        object Huge : Size("huge")
        object Massive : Size("massive")
    }

    sealed class Colored(className: String) : Variation(className) {
        object Red : Colored("red")
        object Orange : Colored("orange")
        object Yellow : Colored("yellow")
        object Olive : Colored("olive")
        object Green : Colored("green")
        object Teal : Colored("teal")
        object Blue : Colored("blue")
        object Violet : Colored("violet")
        object Purple : Colored("purple")
        object Pink : Colored("pink")
        object Brown : Colored("brown")
        object Grey : Colored("grey")
        object Black : Colored("black")
    }

    sealed class Flipped(className: String) : Variation(className, "flipped") {
        object Horizontally : Flipped("horizontally")
        object Vertically : Flipped("vertically")
    }

    sealed class Rotated(className: String) : Variation(className, "rotated") {
        object Clockwise : Rotated("clockwise")
        object Counterclockwise : Rotated("counterclockwise")
    }

    object Circular : Variation("circular")
    object Bordered : Variation("bordered")
    object Inverted : Variation("inverted")

    object Corner : Variation("corner")
    sealed class Position(className: String) : Variation(className) {
        object Top : Colored("top")
        object Right : Colored("right")
        object Bottom : Colored("bottom")
        object Left : Colored("left")
    }

    object Fluid : Variation("fluid")
    object Floating : Variation("floating")
    object Borderless : Variation("borderless")
    object Error : Variation("error")
    object Negative : Variation("negative")
    class Icon(vararg names: String) : Variation(*names, "icon") {
        companion object : Variation("icon")
    }

    object Transparent : Variation("transparent")
}

sealed class State(override vararg val classNames: String) : Modifier {
    object Focus : State("focus")
    object Loading : State("loading")
    object Disabled : State("disabled")
    object Error : State("error")
}
