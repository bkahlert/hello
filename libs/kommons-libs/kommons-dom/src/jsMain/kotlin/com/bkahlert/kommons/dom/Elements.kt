package com.bkahlert.kommons.dom

import kotlinx.dom.addClass
import kotlinx.dom.hasClass
import kotlinx.dom.removeClass
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.ElementCreationOptions
import org.w3c.dom.HTMLDivElement

/**
 * A number in the range [0..1], that describes what percentage of the element's scroll height can be displayed.
 * A value of `1` signifies that no vertical scrolling is needed.
 */
public val Element.verticalScrollCoverageRatio: Double
    get() = (clientHeight / scrollHeight.toDouble()).coerceIn(0.0, 1.0)

/** A number in the range [0..1], that describes to what percentage the element is scrolled to the bottom. */
public val Element.verticalScrollProgress: Double
    get() = (scrollTop / (scrollHeight - clientHeight).toDouble()).coerceIn(0.0, 1.0)


/**
 * [data] gets arbitrary `data` attribute of the Element.
 */
public fun Element.data(dataAttr: String): String? = getAttribute("data-$dataAttr")

/**
 * [data] adds arbitrary `data` attribute to the Element.
 */
public fun Element.data(dataAttr: String, value: String?) {
    val qualifiedName = "data-$dataAttr"
    when (value) {
        null -> removeAttribute(qualifiedName)
        else -> setAttribute(qualifiedName, value)
    }
}

/**
 * Adds each of the given [cssClasses]
 * if this element does not have the respective CSS class style in its 'class' attribute
 * and removes it otherwise.
 *
 * @return `true` if at least one class has been toggled
 */
public fun Element.toggleClass(vararg cssClasses: String): Boolean = cssClasses.map { cssClass ->
    if (hasClass(cssClass)) removeClass(cssClass)
    else addClass(cssClass)
}.any { it }

/**
 * Returns an [Element]
 * - created using the specified [localName] and the optional [options],
 * - with the specified [block] applied to it, and
 * - appended to the current element as a new child.
 */
public inline fun <reified T : Element> Element.appendTypedElement(
    localName: String,
    options: ElementCreationOptions? = null,
    block: T.() -> Unit = {},
): T {
    val document: Document = requiredOwnerDocument
    val createdElement: T = if (options != null) {
        document.createElement(localName, options) as T
    } else {
        document.createElement(localName) as T
    }
    createdElement.apply(block)
    append(createdElement)
    return createdElement
}

/**
 * Returns an [HTMLDivElement]
 * - created using the optional [options],
 * - with the specified [block] applied to it, and
 * - appended to the current element as a new child.
 */
public fun Element.appendDivElement(
    options: ElementCreationOptions? = null,
    block: HTMLDivElement.() -> Unit = {},
): HTMLDivElement = appendTypedElement<HTMLDivElement>("div", options, block)
