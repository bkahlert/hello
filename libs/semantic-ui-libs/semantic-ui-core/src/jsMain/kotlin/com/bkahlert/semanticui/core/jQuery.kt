package com.bkahlert.semanticui.core

import org.w3c.dom.Element
import kotlin.js.Json
import kotlin.js.json
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * An interface for the [jQuery library](https://jquery.com).
 */
@Suppress("ClassName")
public external interface jQuery {

    /** Perform a custom animation of a set of CSS properties. */
    public fun animate(properties: Json = definedExternally, options: Json = definedExternally): jQuery

    /**
     * Get the value of an attribute for the first element in the set of matched elements.
     */
    public fun attr(propertyName: String): String?

    /**
     * Set one or more attributes for the set of matched elements.
     */
    public fun attr(propertyName: String, value: Any?): jQuery

    /**
     * Get the children of each element in the set of matched elements, optionally filtered by a selector.
     */
    public fun children(selector: String = definedExternally): jQuery

    /**
     * Trigger the "click" JavaScript event on an element.
     */
    public fun click(): jQuery

    /**
     * For each element in the set, get the first element that matches the
     * selector by testing the element itself and traversing up through its ancestors in the DOM tree.
     */
    public fun closest(selector: String = definedExternally): jQuery

    /**
     * Set one CSS property for the set of matched elements.
     */
    public fun css(propertyName: String, value: Any?): jQuery

    /**
     * Iterate over a jQuery object, executing a function for each matched element.
     */
    public fun each(fn: (index: Int, element: Element) -> Unit): jQuery

    /**
     * Get the descendants of each element in the current set of matched elements, filtered by the [selector].
     */
    public fun find(selector: String = definedExternally): jQuery

    /**
     * Trigger the "focus" JavaScript event on an element.
     */
    public fun focus(): jQuery

    /**
     * Retrieve one of the elements matched by the jQuery object.
     */
    public operator fun get(index: Int): Element

    /**
     * The number of elements in the jQuery object.
     */
    public val length: Int

    /**
     * Get the immediately preceding sibling of each element in the set of matched elements.
     *
     * If a [selector] is provided, it retrieves the previous sibling only if it matches that selector.
     */
    public fun prev(selector: String = definedExternally): jQuery

    /**
     * Remove the set of matched elements from the DOM.
     */
    public fun remove(selector: String = definedExternally): jQuery

    /**
     * Get the siblings of each element in the set of matched elements, optionally filtered by a selector.
     */
    public fun siblings(selector: String = definedExternally): jQuery
}

/** Creates a jQuery instance using the optional [deep]. */
public external fun jQuery(deep: Any? = definedExternally): jQuery

/**
 * Returns the view of the elements matched by this [jQuery] instance.
 */
public fun jQuery.asList(): List<Element> = object : AbstractList<Element>() {
    override val size: Int get() = this@asList.length

    override fun get(index: Int): Element = when (index) {
        in 0..lastIndex -> this@asList[index]
        else -> throw IndexOutOfBoundsException("index $index is not in range [0..$lastIndex]")
    }
}

/** Perform a custom animation of a set of CSS properties. */
public fun jQuery.animate(
    vararg properties: Pair<String, Any?>,
    duration: Duration = 400.milliseconds,
    easing: String = "swing",
    complete: () -> Unit = {},
): jQuery = animate(
    properties = json(*properties),
    options = json(
        "duration" to duration.inWholeMilliseconds.toInt(),
        "easing" to easing,
        "complete" to complete,
    )
)


/** Convenience shortcut for [jQuery.attr] and [key] prefixed with `data-`. */
public fun jQuery.dataAttr(key: String): String? =
    attr("data-$key")

/** Convenience shortcut for [jQuery.attr] and [key] prefixed with `data-`. */
public fun jQuery.dataAttr(key: String, value: Any?): jQuery =
    attr("data-$key", value)


/**
 * Helper function to convert an optional value to something that jQuery / Semantic UI
 * accepts as an array.
 */
@Deprecated("verify alternatives and move to appropriate module")
public fun <T> T?.toJsonArrayOrEmpty(transform: (T) -> String = { it.toString() }): Array<String> =
    this?.let { arrayOf(transform(it)) } ?: emptyArray()

/**
 * Helper function to convert collections to something that jQuery / Semantic UI
 * accepts as an array.
 */
@Deprecated("verify alternatives and move to appropriate module")
public fun <T> Iterable<T>.toJsonArray(transform: (T) -> String = { it.toString() }): Array<String> =
    map { transform(it) }.toTypedArray()
