package com.bkahlert.hello.semanticui.test

import io.kotest.matchers.string.shouldStartWith
import org.jetbrains.compose.web.testutils.runTest
import kotlin.test.Test

class JQueryLibraryTest {

    @Test
    fun append() = runTest {
        composition { JQueryLibrary.appendTo(root) }
        root.innerHTML shouldStartWith "<script id=\"hello-test-library-jquery\">!function(a,b)"
    }
}
