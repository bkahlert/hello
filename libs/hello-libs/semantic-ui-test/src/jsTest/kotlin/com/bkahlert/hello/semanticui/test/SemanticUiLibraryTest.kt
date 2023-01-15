package com.bkahlert.hello.semanticui.test

import io.kotest.matchers.string.shouldContain
import org.jetbrains.compose.web.testutils.runTest
import kotlin.test.Test

class SemanticUiLibraryTest {

    @Test
    fun append() = runTest {
        composition { SemanticUiLibrary.appendTo(root) }
        root.innerHTML shouldContain "<script id=\"hello-test-library-semanticui\">!function(p,h,v,b){"
    }
}
