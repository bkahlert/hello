package com.bkahlert.hello.clickup.demo

import com.bkahlert.hello.clickup.model.fixtures.ImageFixtures
import com.bkahlert.semanticui.demo.Column
import com.bkahlert.semanticui.demo.DemoProvider
import com.bkahlert.semanticui.demo.Grid

public val ClickUpDemos: DemoProvider = DemoProvider(
    id = "clickup",
    name = "ClickUp",
    logo = ImageFixtures.ClickUpMark,
) {
    Grid {
        Column {
            ActivityDropdownDemo()
            PomodoroStarterDemo()
            PomodoroTimerDemo()
        }
        Column {
            ClickUpMenuDemo()
        }
    }
}
