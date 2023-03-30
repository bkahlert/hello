package com.bkahlert.kommons.dom

import kotlinx.dom.appendText
import org.w3c.dom.HTMLScriptElement
import org.w3c.dom.HTMLStyleElement
import org.w3c.dom.Node
import org.w3c.dom.ParentNode
import org.w3c.dom.asList

/**
 * Returns the node [E] returned by the specified [get], or—if absent—
 * created by the specified [create].
 *
 * @throws IllegalStateException if [get] returns `null` after [create] was called
 */
public inline fun <T, E> T.getOrCreate(get: T.() -> E?, crossinline create: T.() -> Unit): E
    where T : ParentNode,
          T : Node =
    get() ?: run {
        append(create())
        get() ?: error("Required element was not created by specified create function.")
    }

/**
 * Removes all child nodes from this parent node for which [predicate]
 * returns `true` (default: all child nodes).
 */
public fun <T> T.removeChildren(predicate: (Node) -> Boolean = { true }): List<Node>
    where T : ParentNode,
          T : Node =
    childNodes.asList().filter(predicate).map { removeChild(it) }

/**
 * Removes all child nodes from this parent node selected by the specified [selectors].
 */
public fun <T> T.removeChildren(selectors: String): List<Node>
    where T : ParentNode,
          T : Node =
    querySelectorAll(selectors).asList().map { it: Node -> removeChild(it) }


/**
 * Appends a new `<style>` tag with the specified [css] to this [Node].
 */
public fun Node.appendStyle(
    css: String,
    customInit: HTMLStyleElement.() -> Unit = {},
): HTMLStyleElement = checkNotNull(ownerDocument) { "ShadowRoot has no ownerDocument" }
    .createElement("style")
    .unsafeCast<HTMLStyleElement>()
    .apply { appendText(css) }
    .apply(customInit)
    .also(this::appendChild)

/**
 * Appends a new `<script>` tag with the specified [src] to this [Node].
 */
public fun Node.appendScript(
    src: String?,
    customInit: HTMLScriptElement.() -> Unit = {},
): HTMLScriptElement = checkNotNull(ownerDocument) { "ShadowRoot has no ownerDocument" }
    .createElement("script")
    .unsafeCast<HTMLScriptElement>()
    .apply { if (src != null) this.src = src }
    .apply(customInit)
    .also(this::appendChild)
