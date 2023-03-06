package com.bkahlert.hello.search

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.testutils.TestScope
import io.kotest.matchers.shouldBe
import org.jetbrains.compose.web.testutils.runTest
import kotlin.test.Test

class SearchInputTest {

    @Test
    fun disabled_menu() = runTest {
        composition {
            SearchInput()
        }
        root.innerHTML shouldBe "<div class=\"ui search\"><div class=\"ui left icon input\"><i class=\"search icon\"></i><input class=\"prompt\" type=\"search\" placeholder=\"Search...\"></div></div>"
    }
}
