@file:Suppress("RedundantVisibilityModifier")

package playground.components.session

import com.bkahlert.hello.session.demo.FakeSessionDataSource
import com.bkahlert.hello.session.demo.FakeSessionDataSource.Companion.FakeAuthorizedSession
import com.bkahlert.hello.session.demo.FakeSessionDataSource.Companion.FakeUnauthorizedSession
import playground.components.Page
import playground.components.showcase.showcase
import playground.components.showcase.showcases
import playground.tailwind.heroicons.HeroIcons

public object SessionShowcases : Page(
    "session",
    "Session",
    "Session showcases",
    heroIcon = HeroIcons::key,
    content = {
        showcases("SessionView") {
            showcase("Loading") {
                sessionView(null)
            }
            showcase("Unauthorized") {
                sessionView(FakeUnauthorizedSession())
            }
            showcase("Authorized") {
                sessionView(FakeAuthorizedSession())
            }
            showcase("Dynamic") {
                val repository = SessionStore(FakeSessionDataSource())
                repository.data.render {
                    sessionView(
                        it,
                        onAuthorize = { repository.authorize() },
                        onReauthorize = { repository.reauthorize(false) },
                        onUnauthorize = { repository.unauthorize() },
                    )
                }
            }
        }
    },
)
