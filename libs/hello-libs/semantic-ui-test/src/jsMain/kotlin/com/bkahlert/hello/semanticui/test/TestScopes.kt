package com.bkahlert.hello.semanticui.test

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.testutils.TestScope
import org.w3c.dom.Element

/**
 * Cleans up the [TestScope.root] content,
 * appends the specified [libraries] to it,
 * and creates a new composition with a given [content].
 */
public fun TestScope.compositionWith(
    vararg libraries: Library,
    content: @Composable () -> Unit,
) {
    composition {
        libraries.forEach { it.appendTo(root) }
        content()
    }
}

/**
 * Removes all [Library] instances from the [TestScope.root],
 * and applies the specified [assertions] to it.
 */
public fun TestScope.root(assertions: (Element) -> Unit) {
    Library.removeAllFrom(root)
    assertions(root)
}
