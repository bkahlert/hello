package com.bkahlert.hello.semanticui.element

import com.bkahlert.hello.semanticui.test.JQueryLibrary
import com.bkahlert.hello.semanticui.test.SemanticUiLibrary
import com.bkahlert.hello.semanticui.test.compositionWith
import com.bkahlert.hello.semanticui.test.root
import io.kotest.matchers.shouldBe
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.testutils.runTest
import kotlin.test.Test

class IconKtTest {

    @Test
    fun icon() = runTest {
        compositionWith(JQueryLibrary, SemanticUiLibrary) {
            Icon("help") { style { color(Color("yellow")) } }
        }
        root { it.innerHTML shouldBe "<i class=\"help icon\" style=\"color: yellow;\"></i>" }
    }
}
