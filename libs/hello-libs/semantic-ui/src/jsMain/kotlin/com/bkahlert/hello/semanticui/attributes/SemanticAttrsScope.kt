package com.bkahlert.hello.semanticui.attributes

import com.bkahlert.hello.semanticui.attributes.State.Active
import com.bkahlert.hello.semanticui.attributes.State.Disabled
import com.bkahlert.hello.semanticui.attributes.State.Error
import com.bkahlert.hello.semanticui.attributes.State.Focus
import com.bkahlert.hello.semanticui.attributes.State.Indeterminate
import com.bkahlert.hello.semanticui.attributes.State.Loading
import com.bkahlert.hello.semanticui.attributes.Variation.Actions
import com.bkahlert.hello.semanticui.attributes.Variation.Aligned
import com.bkahlert.hello.semanticui.attributes.Variation.Attached
import com.bkahlert.hello.semanticui.attributes.Variation.Basic
import com.bkahlert.hello.semanticui.attributes.Variation.Bordered
import com.bkahlert.hello.semanticui.attributes.Variation.Borderless
import com.bkahlert.hello.semanticui.attributes.Variation.Circular
import com.bkahlert.hello.semanticui.attributes.Variation.Clearing
import com.bkahlert.hello.semanticui.attributes.Variation.Colored
import com.bkahlert.hello.semanticui.attributes.Variation.Columns
import com.bkahlert.hello.semanticui.attributes.Variation.Compact
import com.bkahlert.hello.semanticui.attributes.Variation.Corner
import com.bkahlert.hello.semanticui.attributes.Variation.Dimmable
import com.bkahlert.hello.semanticui.attributes.Variation.Direction
import com.bkahlert.hello.semanticui.attributes.Variation.Emphasis
import com.bkahlert.hello.semanticui.attributes.Variation.Fitted
import com.bkahlert.hello.semanticui.attributes.Variation.Flipped
import com.bkahlert.hello.semanticui.attributes.Variation.Floating
import com.bkahlert.hello.semanticui.attributes.Variation.Fluid
import com.bkahlert.hello.semanticui.attributes.Variation.Fullscreen
import com.bkahlert.hello.semanticui.attributes.Variation.Icon
import com.bkahlert.hello.semanticui.attributes.Variation.Inline
import com.bkahlert.hello.semanticui.attributes.Variation.Inverted
import com.bkahlert.hello.semanticui.attributes.Variation.Length
import com.bkahlert.hello.semanticui.attributes.Variation.Link
import com.bkahlert.hello.semanticui.attributes.Variation.Long
import com.bkahlert.hello.semanticui.attributes.Variation.Padded
import com.bkahlert.hello.semanticui.attributes.Variation.Position
import com.bkahlert.hello.semanticui.attributes.Variation.Rotated
import com.bkahlert.hello.semanticui.attributes.Variation.Scrolling
import com.bkahlert.hello.semanticui.attributes.Variation.Size
import com.bkahlert.hello.semanticui.attributes.Variation.Transparent
import com.bkahlert.hello.semanticui.dom.SemanticElement
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.attributes.AttrsScopeBuilder
import org.w3c.dom.Element
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

public interface SemanticAttrsScope<out TSemantic : SemanticElement<Element>> : AttrsScope<Element> {

    public val settings: MutableMap<String, Any?>

    public fun variation(vararg variation: Variation) {
        variation.forEach { classes(*it.classNames) }
    }

    public fun state(vararg states: State) {
        states.forEach { classes(*it.classNames) }
    }

    public operator fun <T : Modifier> T.unaryPlus(): T {
        classes(*classNames)
        return this
    }

    public operator fun State.plus(other: State): State {
        classes(*other.classNames)
        return other
    }

    public operator fun Variation.plus(other: Variation): Variation {
        classes(*other.classNames)
        return other
    }

    public val Inline: Inline get() = Variation.Inline
    public val Fitted: Fitted get() = Variation.Fitted
    public val Compact: Compact get() = Variation.Compact
    public val Size: Size get() = Variation.Size
    public val Colored: Colored get() = Variation.Colored
    public val Flipped: Flipped get() = Variation.Flipped
    public val Rotated: Rotated get() = Variation.Rotated
    public val Link: Link get() = Variation.Link
    public val Circular: Circular get() = Variation.Circular
    public val Bordered: Bordered get() = Variation.Bordered
    public val Inverted: Inverted get() = Variation.Inverted
    public val Corner: Corner get() = Variation.Corner
    public val Position: Position get() = Variation.Position
    public val Direction: Direction get() = Variation.Direction
    public val Columns: Columns get() = Variation.Columns
    public val Aligned: Aligned get() = Variation.Aligned
    public val Fluid: Fluid get() = Variation.Fluid
    public val Floating: Floating get() = Variation.Floating
    public val Borderless: Borderless get() = Variation.Borderless

    public val Icon: Icon.Companion get() = Variation.Icon
    public fun Icon(vararg names: String): Icon = Variation.Icon(*names)

    public val Transparent: Transparent get() = Variation.Transparent
    public val Scrolling: Scrolling get() = Variation.Scrolling
    public val Attached: Attached get() = Variation.Attached
    public val Padded: Padded get() = Variation.Padded
    public val Emphasis: Emphasis get() = Variation.Emphasis
    public val Basic: Basic get() = Variation.Basic
    public val Clearing: Clearing get() = Variation.Clearing
    public val Fullscreen: Fullscreen get() = Variation.Fullscreen
    public val Length: Length get() = Variation.Length
    public val Long: Long get() = Variation.Long

    public val Actions: Actions get() = Variation.Actions
    public val Dimmable: Dimmable get() = Variation.Dimmable

    public val Active: Active get() = State.Active
    public val Indeterminate: Indeterminate get() = State.Indeterminate
    public val Focus: Focus get() = State.Focus
    public val Loading: Loading get() = State.Loading
    public val Disabled: Disabled get() = State.Disabled
    public val Error: Error get() = State.Error

    public companion object {

        /** Delegate to handle custom settings. */
        public inline infix fun <TSemantic : SemanticElement<TElement>, TElement : Element, reified V> Companion.or(default: V): ReadWriteProperty<SemanticAttrsScope<TSemantic>, V> =
            object : ReadWriteProperty<SemanticAttrsScope<TSemantic>, V> {
                override fun getValue(thisRef: SemanticAttrsScope<TSemantic>, property: KProperty<*>): V =
                    (thisRef.settings[property.name] as? V) ?: default

                override fun setValue(thisRef: SemanticAttrsScope<TSemantic>, property: KProperty<*>, value: V) {
                    thisRef.settings[property.name] = value
                }
            }
    }
}

public open class SemanticAttrsScopeBuilder<out TSemantic : SemanticElement<Element>>(
    internal val attrsScope: AttrsScope<Element> = AttrsScopeBuilder(),
) : SemanticAttrsScope<TSemantic>, AttrsScope<Element> by attrsScope {
    override val settings: MutableMap<String, Any?> = mutableMapOf()
}
