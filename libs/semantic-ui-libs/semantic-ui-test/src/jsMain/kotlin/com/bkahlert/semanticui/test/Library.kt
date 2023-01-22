package com.bkahlert.semanticui.test

import io.ktor.util.decodeBase64String
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.ParentNode
import org.w3c.dom.asList

/**
 * A library that can be appended to a [ParentNode].
 */
public class Library(
    name: String,
    private val base64Content: String,
) {

    public val id: String = "$NAME_PREFIX-${name.lowercase().filter { it in 'a'..'z' }}"

    /**
     * Appends the library to the specified [element].
     */
    public fun appendTo(element: Element): Element {
        val document: Document = requireNotNull(element.ownerDocument) { "Missing owner document of $element" }
        val scriptElement: Element = document.createElement("script")
        scriptElement.id = id
        scriptElement.textContent = base64Content.decodeBase64String()
        element.append(scriptElement)
        return scriptElement
    }

    public fun removeFrom(element: Element) {
        element.querySelector("#$id")?.remove()
    }

    public companion object {
        public const val NAME_PREFIX: String = "semantic-ui-test"

        private fun Node.remove() {
            val parentNode = checkNotNull(parentNode) { "Missing parent node of $this" }
            parentNode.removeChild(this)
        }

        public fun removeAllFrom(element: Element) {
            element.querySelectorAll("[id^='$NAME_PREFIX-']").asList().forEach { it.remove() }
        }
    }
}
