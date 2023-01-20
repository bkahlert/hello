package com.bkahlert.semanticui.collection

import com.bkahlert.semanticui.test.JQueryLibrary
import com.bkahlert.semanticui.test.SemanticUiLibrary
import com.bkahlert.semanticui.test.compositionWith
import com.bkahlert.semanticui.test.root
import io.kotest.matchers.shouldBe
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.testutils.runTest
import kotlin.test.Test

class MenuKtTest {

    @Test
    fun menu() = runTest {
        compositionWith(JQueryLibrary, SemanticUiLibrary) {
            Menu({ style { color(Color("yellow")) } }) {
                Text("foo")
            }
        }
        root { it.innerHTML shouldBe "<div class=\"ui menu\" style=\"color: yellow;\">foo</div>" }
    }
}
