@file:Suppress("RedundantVisibilityModifier")

package playground.components.app

import com.bkahlert.hello.fritz2.app.AppStore
import com.bkahlert.hello.fritz2.app.props.StoragePropsDataSource
import com.bkahlert.hello.fritz2.app.session.FakeSession
import com.bkahlert.hello.fritz2.components.Page
import com.bkahlert.hello.fritz2.components.heroicons.HeroIcons
import com.bkahlert.hello.fritz2.components.showcase.showcase
import com.bkahlert.hello.fritz2.components.showcase.showcases

public val AppShowcases: Page = Page(
    "app",
    "App",
    "App showcases",
    heroIcon = HeroIcons::window,
) {
    showcases("App") {
        showcase("Without ClickUp API Token") {
            clickUpApp(
                AppStore(
                    sessionResolver = { _ -> { FakeSession.Authorized() } },
                    propsProvider = { _, _ -> StoragePropsDataSource.InMemoryPropsDataSource() }
                )
            )
        }
//        showcase("With ClickUp API Token") {
//            clickUpApp(
//                AppStore(
//                    sessionResolver = { _ -> { FakeSession.Authorized() } },
//                    propsProvider = { _, _ ->
//                        InMemoryPropsDataSource("clickup" to buildJsonObject { put("api-token", JsonPrimitive("pk_123_abc")) })
//                    }
//                )
//            )
//        }
        showcase("Actual") {
            clickUpApp(AppStore())
        }
    }
}
