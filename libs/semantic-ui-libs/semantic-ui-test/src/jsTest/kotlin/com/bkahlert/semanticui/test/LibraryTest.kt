package com.bkahlert.semanticui.test

import io.kotest.matchers.shouldBe
import io.ktor.util.encodeBase64
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.testutils.runTest
import kotlin.test.Test

class LibraryTest {

    @Test
    fun append() = runTest {
        composition { myTestLibrary.appendTo(root) }
        root.innerHTML shouldBe "<script id=\"semantic-ui-test-mytestlibrary\">console.log('Hello World!')</script>"
    }

    @Test
    fun remove_first() = runTest {
        composition {
            myTestLibrary.appendTo(root)
            Div { }
        }
        myTestLibrary.removeFrom(root)
        root.innerHTML shouldBe "<div></div>"
    }

    @Test
    fun remove_last() = runTest {
        composition {
            Div { }
            myTestLibrary.appendTo(root)
        }
        myTestLibrary.removeFrom(root)
        root.innerHTML shouldBe "<div></div>"
    }

    @Test
    fun remove_all() = runTest {
        composition {
            JQueryLibrary.appendTo(root)
            Div { }
            myTestLibrary.appendTo(root)
            SemanticUiLibrary.appendTo(root)
        }
        Library.removeAllFrom(root)
        root.innerHTML shouldBe "<div></div>"
    }
}

val myTestLibrary = Library("My test library", "console.log('Hello World!')".encodeBase64())
