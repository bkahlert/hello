package com.bkahlert.semanticui.custom

import io.kotest.matchers.shouldBe
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.testutils.runTest
import kotlin.test.Test

class LoadingStateKtTest {

    @Test
    fun indeterminate() = runTest {
        composition {
            Div({ apply(LoadingState.Indeterminate) }) {
                apply(LoadingState.Indeterminate)
            }
        }
        root.innerHTML shouldBe "<div class=\"dimmable dimmed\" style=\"min-height: 5em;\"><div class=\"ui dimmer\"><div class=\"content\"><div class=\"ui indeterminate loader\"></div></div></div></div>"
    }
}
