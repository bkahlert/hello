package com.bkahlert.hello.clickup.viewmodel.fixtures

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Succeeded.Disabled
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuViewModel
import org.jetbrains.compose.web.testutils.TestScope
import io.kotest.matchers.shouldBe
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.testutils.runTest
import kotlin.test.Test

class ClickUpMenuTestViewModelKtTest {

    @Test
    fun instantiation() = runTest {
        composition {
            TestMenu(rememberClickUpMenuTestViewModel { toTeamSelecting() })
        }
        waitForRecompositionComplete()
        root.innerHTML shouldBe "<div>TeamSelecting</div>"
    }
}

@Composable
fun TestMenu(
    viewModel: ClickUpMenuViewModel,
) {
    val state = viewModel.state.collectAsState(Disabled).value
    Div { Text(state::class.simpleName ?: "?") }
}
