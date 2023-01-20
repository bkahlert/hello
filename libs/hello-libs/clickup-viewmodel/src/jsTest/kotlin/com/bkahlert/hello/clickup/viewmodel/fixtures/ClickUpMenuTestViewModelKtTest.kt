package com.bkahlert.hello.clickup.viewmodel.fixtures

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Succeeded.Disabled
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuViewModel
import com.bkahlert.semanticui.test.JQueryLibrary
import com.bkahlert.semanticui.test.SemanticUiLibrary
import com.bkahlert.semanticui.test.compositionWith
import com.bkahlert.semanticui.test.root
import io.kotest.matchers.shouldBe
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.testutils.runTest
import kotlin.test.Test

class ClickUpMenuTestViewModelKtTest {

    @Test
    fun instantiation() = runTest {
        compositionWith(JQueryLibrary, SemanticUiLibrary) {
            TestMenu(rememberClickUpMenuTestViewModel { toTeamSelecting() })
        }
        waitForRecompositionComplete()
        root { it.innerHTML shouldBe "<div>TeamSelecting</div>" }
    }
}

@Composable
fun TestMenu(
    viewModel: ClickUpMenuViewModel,
) {
    val state = viewModel.state.collectAsState(Disabled).value
    Div { Text(state::class.simpleName ?: "?") }
}
