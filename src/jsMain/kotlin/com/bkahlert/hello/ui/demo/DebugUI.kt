package com.bkahlert.hello.ui.demo

import androidx.compose.runtime.Composable
import com.bkahlert.hello.ui.demo.clickup.ActivityDropdownDemo
import com.bkahlert.hello.ui.demo.clickup.ClickUpMenuDemo1
import com.bkahlert.hello.ui.demo.clickup.ClickUpMenuDemo2
import com.bkahlert.hello.ui.demo.clickup.PomodoroStarterDemo
import com.bkahlert.hello.ui.demo.clickup.PomodoroTimerDemo
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLDivElement

@Composable
private fun Grid(
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: ContentBuilder<HTMLDivElement>? = null,
) {
    Div({
        attrs?.invoke(this)
//        classes("ui", "three", "column", "doubling", "grid", "container")
        classes("ui", "two", "column", "doubling", "grid", "container")
    }, content)
}

@Composable
private fun Column(
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: ContentBuilder<HTMLDivElement>? = null,
) {
    Div({
        attrs?.invoke(this)
        classes("column")
    }, content)
}

@Composable
fun DebugUI() {
    Grid({
        style { margin(2.em) }
    }) {
        Column {
            SearchDemo()
        }
        return@Grid
        Column {
            SearchDemo()
            ClickUpMenuDemo1()
        }
        Column { ClickUpMenuDemo2() }
        Column { ActivityDropdownDemo() }
        Column {
            PomodoroStarterDemo()
            PomodoroTimerDemo()
        }
        Column {
            SemanticDemo()
            DimmingLoaderDemo()
        }
        Column {
            ViewModelDemo()
            MutableFlowStateDemo()
            IdleDetectoryDemo()
        }
    }
}
