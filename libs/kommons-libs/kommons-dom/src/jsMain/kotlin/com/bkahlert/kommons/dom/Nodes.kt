package com.bkahlert.kommons.dom

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
