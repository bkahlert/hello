package com.bkahlert.hello.clickup.demo

import com.bkahlert.hello.clickup.model.fixtures.ImageFixtures
import com.bkahlert.semanticui.demo.DemoProvider

public val ClickUpDemoProvider: DemoProvider = DemoProvider(
    id = "clickup",
    name = "ClickUp",
    logo = ImageFixtures.ClickUpMark,
    {
        ActivityDropdownDemo()
        PomodoroStarterDemo()
        PomodoroTimerDemo()
    },
    {
        ClickUpMenuDemo()
    },
)
