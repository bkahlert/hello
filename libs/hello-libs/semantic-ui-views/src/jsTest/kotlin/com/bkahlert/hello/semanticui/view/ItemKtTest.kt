package com.bkahlert.hello.semanticui.view

import com.bkahlert.hello.semanticui.test.JQueryLibrary
import com.bkahlert.hello.semanticui.test.SemanticUiLibrary
import com.bkahlert.hello.semanticui.test.compositionWith
import com.bkahlert.hello.semanticui.test.root
import io.kotest.matchers.shouldBe
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.testutils.runTest
import kotlin.test.Test

class ItemKtTest {

    @Test
    fun items() = runTest {
        compositionWith(JQueryLibrary, SemanticUiLibrary) {
            Items {
                Item({ style { color(Color("yellow")) } }) {
                    Text("foo")
                }
            }
        }
        root { it.innerHTML shouldBe "<div class=\"ui item\"><div class=\"item\" style=\"color: yellow;\">foo</div></div>" }
    }
}
