package playground.clickupapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.bkahlert.hello.app.ui.App
import com.bkahlert.hello.app.ui.AppViewModel
import com.bkahlert.hello.app.ui.rememberAppViewModel
import com.bkahlert.hello.clickup.client.http.ClickUpHttpClient
import com.bkahlert.hello.clickup.client.http.ClickUpHttpClientConfigurer
import com.bkahlert.hello.clickup.view.ClickUpTestClientConfigurer
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenu
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState
import com.bkahlert.hello.clickup.viewmodel.rememberClickUpMenuViewModel
import com.bkahlert.hello.data.Resource.Failure
import com.bkahlert.hello.data.Resource.Success
import com.bkahlert.kommons.dom.InMemoryStorage
import com.bkahlert.semanticui.custom.ErrorMessage
import com.bkahlert.semanticui.custom.LoadingState
import com.bkahlert.semanticui.element.Header
import org.jetbrains.compose.web.dom.Text
import playground.clickupapp.ClickUpProps.Companion.mapClickUpProps

@Composable
fun ClickUpApp(
    viewModel: AppViewModel = rememberAppViewModel(),
) {
    App(viewModel) {
        val clickUpPropsResource by viewModel.getProp("clickup").mapClickUpProps().collectAsState(null)
        Header { Text("ClickUp") }
        when (val resource = clickUpPropsResource) {
            null -> ClickUpMenu(
                viewModel = rememberClickUpMenuViewModel(),
                state = ClickUpMenuState.Transitioned.Succeeded.Disabled,
                loadingState = LoadingState.On,
            )

            is Success -> {
                ClickUpMenu(
                    viewModel = rememberClickUpMenuViewModel(
                        ClickUpHttpClientConfigurer(),
                        ClickUpTestClientConfigurer(),
                    ).apply {
                        val clickUpProps = resource.data
                        if (clickUpProps?.apiToken != null) {
                            enable(ClickUpHttpClient(clickUpProps.apiToken, InMemoryStorage()))
                        } else {
                            enable()
                        }
                    },
                )
            }

            is Failure -> ErrorMessage(resource.message, resource.cause)
        }
    }
}
