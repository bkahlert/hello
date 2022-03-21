package com.semanticui.compose

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.ElementScope
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement

interface SemanticElement {
    fun classes(states: List<State>, variations: List<Variation>): List<String>
}

interface SemanticElementType<TSegment : SemanticElement> {
    val classNames: Array<out String>
    operator fun invoke(attrsScope: SemanticAttrsScope<TSegment, *>) {
        attrsScope.classes(*classNames)
    }
}
typealias SemanticAttrBuilder<S, T> = SemanticAttrsScope<S, T>.() -> Unit
typealias SemanticBuilder<S, T> = @Composable SemanticElementScope<S, T>.() -> Unit

interface SemanticAttrsScope<TSemantic : SemanticElement, TElement : Element> : AttrsScope<TElement> {
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

    operator fun <T : Modifier> T.unaryPlus(): T {
        classes(*classNames)
        return this
    }

    operator fun State.plus(other: State): State {
        classes(*other.classNames)
        return other
    }

    operator fun Variation.plus(other: Variation): Variation {
        classes(*other.classNames)
        return other
    }

    val Fitted get() = Variation.Fitted
    val Compact get() = Variation.Compact
    val Size get() = Variation.Size
    val Colored get() = Variation.Colored
    val Flipped get() = Variation.Flipped
    val Rotated get() = Variation.Rotated
    val Link get() = Variation.Link
    val Circular get() = Variation.Circular
    val Bordered get() = Variation.Bordered
    val Inverted get() = Variation.Inverted
    val Corner get() = Variation.Corner
    val Position get() = Variation.Position
    val Direction get() = Variation.Direction
    val Columns get() = Variation.Columns
    val Aligned get() = Variation.Aligned
    val Fluid get() = Variation.Fluid
    val Floating get() = Variation.Floating
    val Borderless get() = Variation.Borderless

    class Icon(vararg names: String) : Variation(*names, "icon") {
        companion object : Variation("icon")
    }

    val Transparent get() = Variation.Transparent
    val Scrolling get() = Variation.Scrolling
    val Attached get() = Variation.Attached
    val Padded get() = Variation.Padded
    val Emphasis get() = Variation.Emphasis
    val Clearing get() = Variation.Clearing
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

interface Modifier {
    val classNames: Array<out String>

    companion object {
        fun of(vararg classNames: String) = object : Modifier {
            override val classNames: Array<out String> get() = classNames
        }
    }
}

operator fun Modifier.plus(other: Modifier) = Modifier.of(*classNames, *other.classNames)

inline val Array<out Modifier>.classNames: Array<out String>
    get() = flatMap { it.classNames.asIterable() }.toTypedArray()


open class Variation(override vararg val classNames: String) : Modifier {
    object Fitted : Variation("fitted")
    object Compact : Variation("compact")
    object Size {
        val Mini = Variation("mini")
        val Tiny = Variation("tiny")
        val Small = Variation("small")
        val Large = Variation("large")
        val Big = Variation("big")
        val Huge = Variation("huge")
        val Massive = Variation("massive")
    }

    object Colored {
        val Red = Variation("red")
        val Orange = Variation("orange")
        val Yellow = Variation("yellow")
        val Olive = Variation("olive")
        val Green = Variation("green")
        val Teal = Variation("teal")
        val Blue = Variation("blue")
        val Violet = Variation("violet")
        val Purple = Variation("purple")
        val Pink = Variation("pink")
        val Brown = Variation("brown")
        val Grey = Variation("grey")
        val Black = Variation("black")
    }

    object Flipped {
        val Horizontally = Variation("horizontally")
        val Vertically = Variation("vertically")
    }

    object Rotated {
        val Clockwise = Variation("clockwise")
        val Counterclockwise = Variation("counterclockwise")
    }

    object Link : Variation("link")
    object Circular : Variation("circular")
    object Bordered : Variation("bordered")
    object Inverted : Variation("inverted")

    object Corner : Variation("corner")

    object Position {
        val Top = Variation("top")
        val Right = Variation("right")
        val Bottom = Variation("bottom")
        val Left = Variation("left")
    }

    object Direction {
        val Top = Variation("top")
        val Right = Variation("right")
        val Bottom = Variation("bottom")
        val Left = Variation("left")
    }

    object Columns {
        val One = Variation("one")
        val Two = Variation("two")
        val Three = Variation("three")
        val Four = Variation("four")
    }

    object Aligned : Variation("aligned") {
        val Left = Variation("left", *classNames)
        val Center = Variation("center", *classNames)
        val Right = Variation("right", *classNames)
        val Justified = Variation("justified")
    }

    object Fluid : Variation("fluid")

    object Floating : Variation("floating")
    object Borderless : Variation("borderless")
    class Icon(vararg names: String) : Variation(*names, "icon") {
        companion object : Variation("icon")
    }

    object Transparent : Variation("transparent")

    object Scrolling : Variation("scrolling")
    object Attached : Variation("attached") {
        val Top = Variation("top", *classNames)
        val Right = Variation("right", *classNames)
        val Bottom = Variation("bottom", *classNames)
        val Left = Variation("left", *classNames)
    }

    object Padded : Variation("padded")
    object Emphasis {
        val Primary = Variation()
        val Secondary = Variation("secondary")
        val Tertiary = Variation("tertiary")
    }

    object Clearing : Variation("clearing")
}

sealed class State(override vararg val classNames: String) : Modifier {
    object Focus : State("focus")
    object Loading : State("loading")
    object Disabled : State("disabled")
    object Error : State("error")
}
