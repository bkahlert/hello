package com.bkahlert.kommons.dom

import org.w3c.dom.Document
import org.w3c.dom.HTMLBodyElement
import org.w3c.dom.HTMLHeadElement
import org.w3c.dom.HTMLLinkElement
import org.w3c.dom.Node
import org.w3c.dom.asList

/**
 * Returns the [head] element of this document if present, or
 * creates it otherwise.
 */
public fun Document.head(): HTMLHeadElement = getOrCreate({ head }) {
    createElement("head").also { prepend(it) }
}

/**
 * Returns the [body] element of this document if present, or
 * creates it otherwise.
 */
public fun Document.body(): HTMLBodyElement = getOrCreate({ body as? HTMLBodyElement }) {
    createElement("head").also { append(it) }
}

/**
 * [Favicon](https://en.wikipedia.org/wiki/Favicon) of this document.
 *
 * Settings the favicon creates eventually missing nodes automatically.
 */
public var Document.favicon: String?
    get() = faviconLinkElement?.href?.takeUnless { it.isBlank() }
    set(value) {
        val href = value ?: ""
        head().getOrCreate({ faviconLinkElement?.also { it.href = href } }) {
            createLinkElement(href = href, rel = FAVICON_REL)
        }
    }

private const val FAVICON_REL = "shortcut icon"

private val Document.faviconLinkNodes: List<Node>
    get() = querySelectorAll("head link[rel='$FAVICON_REL']").asList()

private val Document.faviconLinkElement
    get() = faviconLinkNodes.firstNotNullOfOrNull { it as? HTMLLinkElement }

private fun Document.createLinkElement(
    href: String? = null,
    rel: String? = null,
): HTMLLinkElement {
    val linkElement = createElement("link") as HTMLLinkElement
    if (href != null) linkElement.href = href
    if (rel != null) linkElement.rel = rel
    return linkElement
}
