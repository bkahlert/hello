package com.bkahlert.semanticui.core.attributes

import com.bkahlert.semanticui.core.attributes.Modifier.State
import com.bkahlert.semanticui.core.attributes.Modifier.Variation
import com.bkahlert.semanticui.core.dom.SemanticElement
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.attributes.AttrsScopeBuilder
import org.w3c.dom.Element

public interface SemanticAttrsScope<out TSemantic : SemanticElement<Element>> : AttrsScope<Element> {

    /** The [StatesScope] of [TSemantic]. */
    public val s: StatesScope<TSemantic>

    /** The [VariationsScope] of [TSemantic]. */
    public val v: VariationsScope<TSemantic>

    /** The [BehaviorScope] of [TSemantic]. */
    public val b: BehaviorScope<TSemantic>
}

/** Adds the specified [modifier] without further validation to the [AttrsScope]. */
@Suppress("NOTHING_TO_INLINE")
public inline fun AttrsScope<Element>.raw(modifier: Modifier) {
    classes(modifier.classNames)
}

/** Scope for [State] configuration. */
public interface StatesScope<out TSemantic : SemanticElement<Element>> {
    /**
     * Adds this state to the scope.
     *
     * ***Important:**
     * The added state is not checked for compatibility.
     * Supported states can be added with corresponding
     * extension functions.*
     */
    public operator fun State.unaryPlus(): StatesScope<TSemantic>
}

/** Scope for [Variation] configuration. */
public interface VariationsScope<out TSemantic : SemanticElement<Element>> {
    /**
     * Adds this variation to the scope.
     *
     * ***Important:**
     * The added variation is not checked for compatibility.
     * Supported variations can be added with corresponding
     * extension functions.*
     */
    public operator fun Variation.unaryPlus(): VariationsScope<TSemantic>
}

/**
 * Scope for type configuration which is treated
 * like any other variation, as this seems to be the case for
 * some Semantic UI elements already.
 *
 * *Otherwise a [primary basic button](https://semantic-ui.com/elements/button.html#basic) wouldn't be
 * possible since [primary](https://semantic-ui.com/elements/button.html#emphasis) and
 * [basic](https://semantic-ui.com/elements/button.html#basic) are different button types.*
 */
public typealias TypeScope<TSemantic> = VariationsScope<TSemantic>

/** Scope for behavior configuration. */
public interface BehaviorScope<out TSemantic : SemanticElement<Element>> {
    /** Raw settings specifying the behavior of [TSemantic]. */
    public val settings: MutableMap<String, Any?>
}

public open class SemanticAttrsScopeBuilder<out TSemantic : SemanticElement<Element>>(
    internal val attrsScope: AttrsScope<Element> = AttrsScopeBuilder(),
    behaviorSettings: MutableMap<String, Any?>?,
) : SemanticAttrsScope<TSemantic>,
    StatesScope<TSemantic>,
    VariationsScope<TSemantic>,
    BehaviorScope<TSemantic>,
    AttrsScope<Element> by attrsScope {

    override val s: StatesScope<TSemantic> get() = this
    override fun State.unaryPlus(): StatesScope<TSemantic> {
        classes(classNames)
        return s
    }

    override val v: VariationsScope<TSemantic> get() = this
    override fun Variation.unaryPlus(): VariationsScope<TSemantic> {
        classes(classNames)
        return v
    }

    override val b: BehaviorScope<TSemantic> get() = this
    override val settings: MutableMap<String, Any?> = behaviorSettings ?: mutableMapOf()
}
