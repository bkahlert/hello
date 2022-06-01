package com.bkahlert.hello.clickup.ui.widgets

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.element.Icon
import org.jetbrains.compose.web.css.color

@Composable
fun ActivityIcon(activity: Activity<*>) {
    Icon("square") { activity.color?.also { style { color(it) } } }
}
