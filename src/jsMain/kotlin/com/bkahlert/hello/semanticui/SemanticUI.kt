package com.bkahlert.hello.semanticui

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.ElementScope
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A semantic UI element of the form `<div class="ui $classes">$content</div>` that
 * can be used as a fallback for not yet implemented Semantic UI features. */
@Composable
fun SemanticUI(
    vararg classes: String,
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: ContentBuilder<HTMLDivElement>? = null,
) {
    Semantic("ui", *classes, attrs = attrs, content = content)
}

/**
 * A semantic UI element of the form `<div class="$classes">$content</div>` that
 * can be used as a fallback for not yet implemented Semantic UI features. */
@Composable
fun Semantic(
    vararg classes: String,
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: ContentBuilder<HTMLDivElement>? = null,
) {
    Div({
        attrs?.invoke(this)
        classes(*classes.flatMap { it.split(' ') }.toTypedArray())
    }, content)
}

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
    val settings: MutableMap<String, Any?>

    companion object {
        fun <TSemantic : SemanticElement, TElement : Element> of(attrsScope: AttrsScope<TElement>): SemanticAttrsScope<TSemantic, TElement> =
            DelegatingSemanticAttrsScope(attrsScope)

        /** Delegate to handle custom settings. */
        inline infix fun <reified V> Companion.or(default: V): ReadWriteProperty<SemanticAttrsScope<*, *>, V> =
            object : ReadWriteProperty<SemanticAttrsScope<*, *>, V> {
                override fun getValue(thisRef: SemanticAttrsScope<*, *>, property: KProperty<*>): V =
                    (thisRef.settings[property.name] as? V) ?: default

                override fun setValue(thisRef: SemanticAttrsScope<*, *>, property: KProperty<*>, value: V) {
                    thisRef.settings[property.name] = value
                }
            }

        private data class DelegatingSemanticAttrsScope<TSemantic : SemanticElement, TElement : Element>(
            private val delegate: AttrsScope<TElement>,
            override val settings: MutableMap<String, Any?> = mutableMapOf(),
        ) : SemanticAttrsScope<TSemantic, TElement>, AttrsScope<TElement> by delegate
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

    val Inline get() = Variation.Inline
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

    val Icon get() = Variation.Icon.Companion
    fun Icon(vararg names: String) = Variation.Icon(*names)

    val Transparent get() = Variation.Transparent
    val Scrolling get() = Variation.Scrolling
    val Attached get() = Variation.Attached
    val Padded get() = Variation.Padded
    val Emphasis get() = Variation.Emphasis
    val Basic get() = Variation.Basic
    val Clearing get() = Variation.Clearing
    val Fullscreen get() = Variation.Fullscreen
    val Length get() = Variation.Length
    val Long get() = Variation.Long

    val Actions get() = Variation.Actions
    val Dimmable get() = Variation.Dimmable

    val Active get() = State.Active
    val Indeterminate get() = State.Indeterminate
    val Focus get() = State.Focus
    val Loading get() = State.Loading
    val Disabled get() = State.Disabled
    val Error get() = State.Error
}

interface SemanticElementScope<out TSemantic : SemanticElement, out TElement : Element> : ElementScope<TElement> {
    companion object {
        fun <TSemantic : SemanticElement, TElement : Element> of(elementScope: ElementScope<TElement>): SemanticElementScope<TSemantic, TElement> =
            object : SemanticElementScope<TSemantic, TElement>, ElementScope<TElement> by elementScope {}
    }
}

/**
 * Creates a [TSemantic] representing element with
 * the specified [attrs] and
 * the specified [content]
 * based on the [TElement]
 * built using the specified [builder].
 */
@Composable
fun <TSemantic : SemanticElement, TElement : Element> SemanticElement(
    attrs: SemanticAttrBuilder<TSemantic, TElement>? = null,
    content: SemanticBuilder<TSemantic, TElement>? = null,
    builder: @Composable (AttrBuilderContext<TElement>?, ContentBuilder<TElement>?) -> Unit,
) = builder({
    attrs?.invoke(SemanticAttrsScope.of(this))
}, {
    content?.invoke(SemanticElementScope.of(this))
})

/**
 * Creates a [TSemantic] representing element with
 * the specified [attrs] and
 * the specified [content]
 * based on a [HTMLDivElement].
 */
@Composable
fun <TSemantic : SemanticElement> SemanticDivElement(
    attrs: SemanticAttrBuilder<TSemantic, HTMLDivElement>? = null,
    content: SemanticBuilder<TSemantic, HTMLDivElement>? = null,
) = SemanticElement(attrs, content) { a, c -> Div(a, c) }

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
    object Avatar : Variation("avatar")
    object Inline : Variation("inline")
    object Fitted : Variation("fitted")
    object Compact : Variation("compact")
    object Positive : Variation("positive")
    object Negative : Variation("negative")
    object FullScreen : Variation("fullscreen")
    object Longer : Variation("longer")
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
    object Horizontal : Variation("horizontal")
    object Selection : Variation("selection")
    object Animated : Variation("animated")
    object Relaxed : Variation("relaxed")
    object Divided : Variation("divided")
    object Celled : Variation("celled")

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

    object VerticallyAligned : Variation("aligned") {
        val Top = Variation("top", *classNames)
        val Middle = Variation("middle", *classNames)
        val Bottom = Variation("bottom", *classNames)
    }

    object Floated : Variation("floated") {
        val Right = Variation("right", *classNames)
    }

    object Centered : Variation("centered")
    object Spaced : Variation("spaced")
    object Fluid : Variation("fluid")
    object Rounded : Variation("rounded")

    object Floating : Variation("floating")
    object Borderless : Variation("borderless")
    object Labeled : Variation("labeled")
    object Action : Variation("action")
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

    object Basic : Variation("basic")

    object Emphasis {
        val Primary = Variation("primary")
        val Secondary = Variation("secondary")
        val Tertiary = Variation("tertiary")
    }

    object Clearing : Variation("clearing")
    object Fullscreen : Variation("fullscreen")

    object Length : Variation() {
        val Full = Variation("full")
        val VeryLong = Variation("very", "long")
        val Long = Variation("long")
        val Medium = Variation("medium")
        val Short = Variation("short")
        val VeryShort = Variation("very", "short")
    }

    object Long : Variation("long")
    object Actions : Variation() {
        val Approve = Variation("approve")
        val Positive = Variation("positive")
        val Ok = Variation("ok")
        val Deny = Variation("deny")
        val Negative = Variation("Negative")
        val Cancel = Variation("Cancel")
    }

    object Dimmable : Variation("dimmable")
}

sealed class State(override vararg val classNames: String) : Modifier {
    object Active : State("active")
    object Hidden : State("hidden")
    object Indeterminate : State("indeterminate")
    object Focus : State("focus")
    object Loading : State("loading")
    object Disabled : State("disabled")
    object Error : State("error")
}
