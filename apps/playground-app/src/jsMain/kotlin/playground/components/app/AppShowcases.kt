@file:Suppress("RedundantVisibilityModifier")

package playground.components.app

import com.bkahlert.hello.app.AppStore
import com.bkahlert.hello.app.props.StoragePropsDataSource
import com.bkahlert.hello.app.session.FakeSession
import com.bkahlert.hello.components.SimplePage
import com.bkahlert.hello.icon.heroicons.HeroIcons
import com.bkahlert.hello.showcase.showcase
import com.bkahlert.hello.showcase.showcases

public val AppShowcases: SimplePage = SimplePage(
    "app",
    "App",
    "App showcases",
    heroIcon = HeroIcons::window,
) {
    showcases("App") {
        showcase("Fake Session") {
            app(
                AppStore(
                    sessionResolver = { _ -> { FakeSession.Authorized() } },
                    propsProvider = { _, _ -> StoragePropsDataSource.InMemoryPropsDataSource() }
                )
            )
        }
        showcase("Actual") {
            app(AppStore())
        }
    }
}
