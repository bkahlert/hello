package playground.clickupapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.bkahlert.hello.app.ui.App
import com.bkahlert.hello.app.ui.AppViewModel
import com.bkahlert.hello.app.ui.rememberAppViewModel
import com.bkahlert.hello.clickup.client.http.ClickUpHttpClient
import com.bkahlert.hello.clickup.client.http.ClickUpHttpClientConfigurer
import com.bkahlert.hello.clickup.view.ClickUpTestClientConfigurer
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenu
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState
import com.bkahlert.hello.clickup.viewmodel.rememberClickUpMenuViewModel
import com.bkahlert.kommons.dom.InMemoryStorage
import com.bkahlert.semanticui.custom.LoadingState
import com.bkahlert.semanticui.element.Header
import org.jetbrains.compose.web.dom.Text
import playground.clickupapp.ClickUpProps.Companion.mapClickUpProps


@Composable
fun ClickUpApp(
    viewModel: AppViewModel = rememberAppViewModel(),
) {
    App(viewModel) {
        val clickUpPropsState = viewModel.getProp("clickup").mapClickUpProps().collectAsState(null)
        Header { Text("ClickUp") }
        when (val clickUpProps = clickUpPropsState.value) {
            null -> ClickUpMenu(
                viewModel = rememberClickUpMenuViewModel(),
                state = ClickUpMenuState.Transitioned.Succeeded.Disabled,
                loadingState = LoadingState.Indeterminate,
            )

            else -> ClickUpMenu(
                viewModel = rememberClickUpMenuViewModel(
                    ClickUpHttpClientConfigurer(),
                    ClickUpTestClientConfigurer(),
                ).apply {
                    if (clickUpProps.apiToken != null) {
                        val clickUpClient = ClickUpHttpClient(clickUpProps.apiToken, InMemoryStorage())
                        connect(clickUpClient)
                    }
                },
            )
        }
    }
}
