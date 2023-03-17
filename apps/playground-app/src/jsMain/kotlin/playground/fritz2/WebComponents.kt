@file:Suppress("RedundantVisibilityModifier")

package playground.fritz2

import dev.fritz2.core.Tag
import dev.fritz2.headless.foundation.TagFactory
import dev.fritz2.webcomponents.WebComponent
import dev.fritz2.webcomponents.registerWebComponent
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLStyleElement
import org.w3c.dom.ShadowRoot
import org.w3c.dom.asList

/**
 * Registers this [WebComponent] at the browser's registry
 * and return a [TagFactory] that creates custom-[HtmlTag] instances.
 *
 * @see [registerWebComponent]
 */
public fun <E : Element> WebComponent<E>.register(localName: String, vararg observedAttributes: String): TagFactory<Tag<E>> {
    registerWebComponent(localName, this, *observedAttributes)
    return custom(localName)
}

/**
 * Inherits the stylesheets from the specified [element]—optionally filtered
 * by the specified [predicate].
 */
public fun ShadowRoot.inheritStylesheets(
    element: HTMLElement,
    predicate: (HTMLStyleElement) -> Boolean = { it.textContent?.contains("tailwind") == true },
): List<HTMLStyleElement> = element
    .ownerDocument
    ?.head
    ?.getElementsByTagName("style")
    ?.asList()
    .orEmpty()
    .filterIsInstance<HTMLStyleElement>()
    .filter(predicate)
    .map { styleElement ->
        styleElement.cloneNode(true).also(this::appendChild)
    }
    .filterIsInstance<HTMLStyleElement>()
