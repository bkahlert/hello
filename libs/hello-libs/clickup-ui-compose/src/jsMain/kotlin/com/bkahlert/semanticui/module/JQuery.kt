package com.bkahlert.semanticui.module

import js.core.ArrayLike
import js.core.Object
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import kotlin.js.Json
import kotlin.js.json
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * An interface for the [jQuery library](https://jquery.com).
 */
public external class JQuery<TElement : HTMLElement> {

    /** Perform a custom animation of a set of CSS properties. */
    public fun animate(properties: Json = definedExternally, options: Json = definedExternally): JQuery<TElement>

    /**
     * Get the value of an attribute for the first element in the set of matched elements.
     */
    public fun attr(propertyName: String): String?

    /**
     * Set one or more attributes for the set of matched elements.
     */
    public fun attr(propertyName: String, value: Any?): JQuery<TElement>

    /**
     * Get the children of each element in the set of matched elements, optionally filtered by a selector.
     */
    public fun children(selector: String = definedExternally): JQuery<TElement>

    /**
     * Trigger the "click" JavaScript event on an element.
     */
    public fun click(): JQuery<TElement>

    /**
     * For each element in the set, get the first element that matches the
     * selector by testing the element itself and traversing up through its ancestors in the DOM tree.
     */
    public fun closest(selector: String = definedExternally): JQuery<TElement>

    /**
     * Set one CSS property for the set of matched elements.
     */
    public fun css(propertyName: String, value: Any?): JQuery<TElement>

    /**
     * Iterate over a jQuery object, executing a function for each matched element.
     */
    public fun each(fn: (index: Int, element: Element) -> Unit): JQuery<TElement>

    /**
     * Get the descendants of each element in the current set of matched elements, filtered by the [selector].
     */
    public fun find(selector: String = definedExternally): JQuery<TElement>

    /**
     * Trigger the "focus" JavaScript event on an element.
     */
    public fun focus(): JQuery<TElement>

    /**
     * Retrieve one of the elements matched by the jQuery object.
     */
    public operator fun get(index: Int): TElement

    /**
     * The number of elements in the jQuery object.
     */
    public val length: Int

    /**
     * Get the immediately preceding sibling of each element in the set of matched elements.
     *
     * If a [selector] is provided, it retrieves the previous sibling only if it matches that selector.
     */
    public fun prev(selector: String = definedExternally): JQuery<TElement>

    /**
     * Remove the set of matched elements from the DOM.
     */
    public fun remove(selector: String = definedExternally): JQuery<TElement>

    /**
     * Get the siblings of each element in the set of matched elements, optionally filtered by a selector.
     */
    public fun siblings(selector: String = definedExternally): JQuery<TElement>

    public companion object {
        public var fn: Object
        public fun contains(container: Element, contained: Element): Boolean
        public fun css(elem: Element, name: String): Any
        public fun <T> each(array: ArrayLike<T>, callback: (self: T, indexInArray: Number, value: T) -> Any): ArrayLike<T>
        public fun <T, K : Any> each(obj: T, callback: (self: Any, propertyName: K, valueOfProperty: Any) -> Any): T
    }
}

public inline operator fun <TElement : HTMLElement> JQuery<TElement>.iterator(): Iterator<TElement> = asDynamic().iterator()


/** Perform a custom animation of a set of CSS properties. */
public fun <TElement : HTMLElement> JQuery<TElement>.animate(
    vararg properties: Pair<String, Any?>,
    duration: Duration = 400.milliseconds,
    easing: String = "swing",
    complete: () -> Unit = {},
): JQuery<TElement> = animate(
    properties = json(*properties),
    options = json(
        "duration" to duration.inWholeMilliseconds.toInt(),
        "easing" to easing,
        "complete" to complete,
    )
)


/** Convenience shortcut for [jQuery.attr] and [key] prefixed with `data-`. */
public fun <TElement : HTMLElement> JQuery<TElement>.dataAttr(key: String): String? =
    attr("data-$key")

/** Convenience shortcut for [jQuery.attr] and [key] prefixed with `data-`. */
public fun <TElement : HTMLElement> JQuery<TElement>.dataAttr(key: String, value: Any?): JQuery<TElement> =
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
