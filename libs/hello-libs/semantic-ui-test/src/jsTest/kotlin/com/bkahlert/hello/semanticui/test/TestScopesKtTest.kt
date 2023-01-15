package com.bkahlert.hello.semanticui.test

import io.kotest.matchers.shouldBe
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.testutils.runTest
import kotlin.test.Test

class TestScopesKtTest {

    @Test
    fun composition_with() = runTest {
        compositionWith(myTestLibrary) { Div { } }
        root.innerHTML shouldBe "<div></div><script id=\"hello-test-library-mytestlibrary\">console.log('Hello World!')</script>"
    }

    @Test
    fun root() = runTest {
        compositionWith(myTestLibrary) { Div { } }
        root { it.innerHTML shouldBe "<div></div>" }
    }
}
