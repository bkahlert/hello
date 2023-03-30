package playground.components.app

import com.bkahlert.hello.clickup.client.http.ClickUpHttpClient
import com.bkahlert.hello.clickup.client.http.ClickUpHttpClientConfigurer
import com.bkahlert.hello.clickup.view.ClickUpTestClientConfigurer
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenu
import com.bkahlert.hello.clickup.viewmodel.rememberClickUpMenuViewModel
import com.bkahlert.hello.fritz2.compose.compose
import com.bkahlert.kommons.dom.InMemoryStorage
import dev.fritz2.core.RenderContext
import playground.components.app.ClickUpProps.Companion.mapClickUpProps

fun RenderContext.clickUpApp(
    store: AppStore,
) {
    app(store) {
        store.getProp("clickup").mapClickUpProps().render { clickUpProps ->
            compose {
                ClickUpMenu(
                    viewModel = rememberClickUpMenuViewModel(
                        ClickUpHttpClientConfigurer(),
                        ClickUpTestClientConfigurer(),
                    ).apply {
                        if (clickUpProps?.apiToken != null) {
                            enable(ClickUpHttpClient(clickUpProps.apiToken, InMemoryStorage()))
                        } else {
                            enable()
                        }
                    },
                )
            }
        }
    }
}
