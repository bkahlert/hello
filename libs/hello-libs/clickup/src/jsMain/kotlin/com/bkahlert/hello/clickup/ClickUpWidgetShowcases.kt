package com.bkahlert.hello.clickup

import com.bkahlert.hello.icon.heroicons.HeroIcons
import com.bkahlert.hello.page.SimplePage
import com.bkahlert.hello.showcase.showcase
import com.bkahlert.hello.showcase.showcases

public object ClickUpWidgetShowcases : SimplePage(
    "clickup-widget",
    "ClickUp Widget",
    "ClickUp widget showcases",
    heroIcon = HeroIcons::clipboard_document_list,
    content = {
        // The current ClickUpComponent uses singleton state for `root` and
        // `composition`, so mounting two `<clickup-menu-v2>` instances on the same
        // page causes the second to clobber the first. Until that is fixed, we
        // expose a single showcase whose demo-state can be cycled by editing the
        // attribute below — the fixtures support fully-loaded, fully-loaded-running,
        // partially-loaded, team-selecting, disconnected, and disabled.
        showcases("ClickUp Menu") {
            showcase("Fully-loaded with running pomodoro") {
                clickUpMenu(demoState = "fully-loaded-running")
            }
        }
    },
)
