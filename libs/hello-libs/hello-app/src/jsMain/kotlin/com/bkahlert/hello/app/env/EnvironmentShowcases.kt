package com.bkahlert.hello.app.env

import com.bkahlert.hello.components.SimplePage
import com.bkahlert.hello.icon.heroicons.HeroIcons
import com.bkahlert.hello.showcase.showcase
import com.bkahlert.hello.showcase.showcases

public object EnvironmentShowcases : SimplePage(
    "environment",
    "Environment",
    "Environment showcases",
    heroIcon = HeroIcons::command_line,
    content = {
        showcases("EnvironmentView") {
            showcase("Empty") {
                environmentView(EnvironmentStore())
            }
            showcase("Filled") {
                environmentView(EnvironmentStore(Environment.of("FOO" to "bar", "BAZ" to "")))
            }
            showcase("Actual") {
                val environmentStore = EnvironmentStore()
                environmentView(environmentStore.apply { environmentStore.handle<Unit> { _, _ -> Environment.load() }.invoke() })
            }
        }
    },
)
