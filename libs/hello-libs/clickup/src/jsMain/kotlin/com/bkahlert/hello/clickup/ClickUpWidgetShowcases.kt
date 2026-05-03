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
        showcases("ClickUp Menu") {
            showcase("Fully-loaded", overflowVisible = true) {
                clickUpMenu(demoState = "fully-loaded")
            }
            showcase("Fully-loaded with running pomodoro", overflowVisible = true) {
                clickUpMenu(demoState = "fully-loaded-running")
            }
            showcase("Partially loaded", overflowVisible = true) {
                clickUpMenu(demoState = "partially-loaded")
            }
            showcase("Team selecting", overflowVisible = true) {
                clickUpMenu(demoState = "team-selecting")
            }
            showcase("Disconnected", overflowVisible = true) {
                clickUpMenu(demoState = "disconnected")
            }
            showcase("Disabled", overflowVisible = true) {
                clickUpMenu(demoState = "disabled")
            }
        }
    },
)
