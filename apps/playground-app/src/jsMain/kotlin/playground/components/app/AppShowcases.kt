@file:Suppress("RedundantVisibilityModifier")

package playground.components.app

import com.bkahlert.hello.fritz2.app.AppStore
import com.bkahlert.hello.fritz2.app.props.StoragePropsDataSource
import com.bkahlert.hello.fritz2.app.session.FakeSession
import com.bkahlert.hello.fritz2.components.SimplePage
import com.bkahlert.hello.fritz2.components.heroicons.HeroIcons
import com.bkahlert.hello.fritz2.components.showcase.showcase
import com.bkahlert.hello.fritz2.components.showcase.showcases

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
