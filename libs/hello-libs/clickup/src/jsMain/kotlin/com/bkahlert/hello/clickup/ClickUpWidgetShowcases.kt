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
            showcase("Default (fixture-fed)") {
                clickUpMenu()
            }
        }
    },
)
