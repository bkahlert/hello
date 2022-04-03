package com.bkahlert.hello.plugins.clickup.menu

import androidx.compose.runtime.Composable
import com.semanticui.compose.element.Icon
import org.jetbrains.compose.web.css.color

@Composable
fun ActivityIcon(activity: Activity<*>) {
    Icon("square") { activity.color?.also { style { color(it) } } }
}
