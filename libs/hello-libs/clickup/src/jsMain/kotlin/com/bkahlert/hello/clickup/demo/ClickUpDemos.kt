package com.bkahlert.hello.clickup.demo

import com.bkahlert.semanticui.demo.Column
import com.bkahlert.semanticui.demo.DemoProvider
import com.bkahlert.semanticui.demo.Grid

public val ClickUpDemos: DemoProvider = DemoProvider("clickup", "ClickUp") {
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
