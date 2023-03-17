@file:Suppress("RedundantVisibilityModifier")

package playground.components.app

import com.bkahlert.hello.environment.data.DynamicEnvironmentDataSource
import com.bkahlert.hello.environment.domain.Environment
import com.bkahlert.hello.props.demo.InMemoryPropsDataSource
import com.bkahlert.hello.props.domain.Props
import com.bkahlert.hello.session.demo.FakeSessionDataSource
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import playground.components.Page
import playground.components.environment.EnvironmentStore
import playground.components.props.PropsStore
import playground.components.session.SessionStore
import playground.components.showcase.showcase
import playground.components.showcase.showcases
import playground.tailwind.heroicons.HeroIcons

public val AppShowcases: Page = Page(
    "app",
    "App",
    "App showcases",
    heroIcon = HeroIcons::window,
) {
    showcases("App") {
        val attemptConnect = false
        showcase("Using Mock (attemptConnect=$attemptConnect)") {
            if (attemptConnect) {
                clickUpApp(
                    AppStore(
                        environment = Environment.EMPTY,
                        sessionStore = SessionStore(FakeSessionDataSource(initiallyAuthorized = true)),
                        propsStoreProvider = { x, y ->
                            PropsStore(
                                propsDataSource = InMemoryPropsDataSource(Props(buildJsonObject {
                                    put("clickup", buildJsonObject { put("api-token", JsonPrimitive("pk_123_abc")) })
                                })),
                            )
                        })
                )
            } else {
                clickUpApp(AppStore())
            }
        }
        showcase("Using Environment") {
            val envStore = EnvironmentStore(DynamicEnvironmentDataSource())
            envStore.data.render { env ->
                if (env == null) {
                    landingScreen()
                } else {
                    clickUpApp(AppStore(environment = env))
                }
            }
        }
    }
}
