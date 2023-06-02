@file:Suppress("RedundantVisibilityModifier")

package com.bkahlert.hello.app.session

import com.bkahlert.hello.components.SimplePage
import com.bkahlert.hello.icon.heroicons.HeroIcons
import com.bkahlert.hello.showcase.showcase
import com.bkahlert.hello.showcase.showcases

public object SessionShowcases : SimplePage(
    "session",
    "Session",
    "Session showcases",
    heroIcon = HeroIcons::key,
    content = {
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
