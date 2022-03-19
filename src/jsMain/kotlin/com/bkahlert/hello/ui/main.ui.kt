package com.bkahlert.hello.ui

import androidx.compose.runtime.Composable
import com.bkahlert.hello.AppStylesheet
import com.bkahlert.hello.ui.demo.CurrentTaskDemo
import com.bkahlert.hello.ui.demo.IdleDetectoryDemo
import com.bkahlert.hello.ui.demo.PomodoroTimerDemo
import com.bkahlert.hello.ui.demo.SearchDemo
import com.bkahlert.hello.ui.demo.ViewModelDemo
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLDivElement

@Composable
private fun Grid(
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: ContentBuilder<HTMLDivElement>? = null,
) {
    Div({
        attrs?.invoke(this)
        classes("ui", "three", "column", "doubling", "grid", "container")
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

fun mainTest() {

    renderComposable("root") {
        Style(AppStylesheet)

        Grid({
            style { margin(2.em) }
        }) {
            Column {
                ViewModelDemo()
                IdleDetectoryDemo()
                SearchDemo()
            }
            Column { CurrentTaskDemo() }
            Column { PomodoroTimerDemo() }
        }
    }
}
