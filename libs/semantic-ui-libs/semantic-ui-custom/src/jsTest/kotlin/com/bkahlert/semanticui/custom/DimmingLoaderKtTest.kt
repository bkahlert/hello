package com.bkahlert.semanticui.custom

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

class DimmingLoaderKtTest {

    @Test
    fun checkbox() = runTest {
        compositionWith(JQueryLibrary, SemanticUiLibrary) {
            DimmingLoader(true, { style { color(Color("yellow")) } }) {
                Text("foo")
            }
        }
        root { it.innerHTML shouldBe "<div class=\"ui inverted active dimmer\"><div class=\"ui text loader\" style=\"color: yellow;\">foo</div></div>" }
    }
}
