package com.bkahlert.kommons.dom

import com.bkahlert.kommons.compose.data
import io.ktor.http.Url
import kotlinx.dom.addClass
import kotlinx.dom.hasClass
import kotlinx.dom.removeClass
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.HTMLBodyElement
import org.w3c.dom.HTMLHeadElement
import org.w3c.dom.HTMLLinkElement

/**
 * [Favicon](https://en.wikipedia.org/wiki/Favicon) of this [Document].
 *
 * On set, necessary tags are created implicitly.
 */
var Document.favicon: Url?
    get() = firstInstanceOrNull<HTMLLinkElement>("head link[rel='shortcut icon']")
        ?.href
        ?.takeUnless { it.isBlank() }
        ?.let { Url(it) }
    set(value) {
        head().getOrCreate({ firstInstanceOrNull<HTMLLinkElement>("head link[rel='shortcut icon']") }) {
            (createElement("link") as HTMLLinkElement).apply { href = value?.toString() ?: ""; rel = "shortcut icon" }
        }.href = value?.toString() ?: ""
    }

/**
 * Creates the [head] element if it does not already exist and returns it.
 */
fun Document.head(): HTMLHeadElement = getOrCreate({ head }) {
    createElement("head").also { prepend(it) }
}

/**
 * Creates the [body] element if it does not already exist and returns it.
 */
fun Document.body(): HTMLBodyElement = getOrCreate({ body as? HTMLBodyElement }) {
    createElement("head").also { append(it) }
}

/**
 * Adds the given [cssClass] if this element does not have the given CSS class style in its 'class' attribute
 * and removes it otherwise.
 */
fun Element.toggleClass(cssClass: String): Boolean =
    if (hasClass(cssClass)) removeClass(cssClass)
    else addClass(cssClass)

/**
 * [data] gets arbitrary `data` attribute of the Element.
 */
fun Element.data(dataAttr: String): String? = getAttribute("data-$dataAttr")

/**
 * [data] adds arbitrary `data` attribute to the Element.
 */
fun Element.data(dataAttr: String, value: String) = setAttribute("data-$dataAttr", value)
