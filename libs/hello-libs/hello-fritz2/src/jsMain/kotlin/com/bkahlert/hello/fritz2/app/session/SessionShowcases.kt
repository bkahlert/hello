@file:Suppress("RedundantVisibilityModifier")

package com.bkahlert.hello.fritz2.app.session

import com.bkahlert.hello.fritz2.components.Page
import com.bkahlert.hello.fritz2.components.heroicons.HeroIcons
import com.bkahlert.hello.fritz2.components.showcase.showcase
import com.bkahlert.hello.fritz2.components.showcase.showcases

public object SessionShowcases : Page(
    "session",
    "Session",
    "Session showcases",
    heroIcon = HeroIcons::key,
    pageContent = {
        showcases("SessionView") {
            showcase("Unauthorized") {
                sessionView(SessionStore(FakeSession.Unauthorized()))
            }
            showcase("Authorized") {
                sessionView(SessionStore(FakeSession.Authorized()))
            }
        }
    },
)
