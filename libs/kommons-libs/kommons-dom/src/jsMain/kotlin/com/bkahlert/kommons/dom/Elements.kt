package com.bkahlert.kommons.dom

import kotlinx.dom.addClass
import kotlinx.dom.hasClass
import kotlinx.dom.removeClass
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.ElementCreationOptions
import org.w3c.dom.HTMLDivElement

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
    val document: Document = requireNotNull(ownerDocument) { "Missing owner document of $this" }
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
