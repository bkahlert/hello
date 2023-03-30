@file:Suppress("RedundantVisibilityModifier")

package playground.components.session

import com.bkahlert.hello.fritz2.components.Page
import com.bkahlert.hello.fritz2.components.heroicons.HeroIcons
import com.bkahlert.hello.fritz2.components.showcase.showcase
import com.bkahlert.hello.fritz2.components.showcase.showcases
import com.bkahlert.hello.session.demo.FakeSessionDataSource
import com.bkahlert.hello.session.demo.FakeSessionDataSource.Companion.FakeAuthorizedSession
import com.bkahlert.hello.session.demo.FakeSessionDataSource.Companion.FakeUnauthorizedSession

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
