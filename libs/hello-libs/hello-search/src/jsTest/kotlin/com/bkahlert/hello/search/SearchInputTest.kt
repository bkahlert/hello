package com.bkahlert.hello.search

import com.bkahlert.semanticui.test.JQueryLibrary
import com.bkahlert.semanticui.test.SemanticUiLibrary
import com.bkahlert.semanticui.test.compositionWith
import com.bkahlert.semanticui.test.root
import io.kotest.matchers.shouldBe
import org.jetbrains.compose.web.testutils.runTest
import kotlin.test.Test

class SearchInputTest {

    @Test
    fun disabled_menu() = runTest {
        compositionWith(JQueryLibrary, SemanticUiLibrary) {
            SearchInput()
        }
        root { it.innerHTML shouldBe "<div class=\"ui search\"><div class=\"ui left icon input\"><i class=\"search icon\"></i><input class=\"prompt\" type=\"search\" placeholder=\"Search...\"></div></div>" }
    }
}
