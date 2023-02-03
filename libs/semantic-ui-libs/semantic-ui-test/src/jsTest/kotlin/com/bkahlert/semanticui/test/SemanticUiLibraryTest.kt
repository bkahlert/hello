package com.bkahlert.semanticui.test

import io.kotest.matchers.sequences.shouldContainExactly
import org.jetbrains.compose.web.testutils.runTest
import kotlin.test.Test

class SemanticUiLibraryTest {

    @Test
    fun append() = runTest {
        composition { SemanticUiLibrary.appendTo(root) }
        root.innerHTML.lineSequence().take(2).shouldContainExactly(
            "<script id=\"semantic-ui-test-semanticui\"> /*",
            " * # Semantic UI - 2.5.0",
        )
    }
}
