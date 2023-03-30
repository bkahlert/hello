package com.bkahlert.hello.fritz2.app.env

import com.bkahlert.hello.fritz2.components.Page
import com.bkahlert.hello.fritz2.components.heroicons.HeroIcons
import com.bkahlert.hello.fritz2.components.showcase.showcase
import com.bkahlert.hello.fritz2.components.showcase.showcases
import com.bkahlert.hello.fritz2.load

public object EnvironmentShowcases : Page(
    "environment",
    "Environment",
    "Environment showcases",
    heroIcon = HeroIcons::command_line,
    pageContent = {
        showcases("EnvironmentView") {
            showcase("Empty") {
                environmentView(EnvironmentStore())
            }
            showcase("Filled") {
                environmentView(EnvironmentStore(Environment.of("FOO" to "bar", "BAZ" to "")))
            }
            showcase("Actual") {
                environmentView(EnvironmentStore().load { Environment.load() })
            }
        }
    },
)
