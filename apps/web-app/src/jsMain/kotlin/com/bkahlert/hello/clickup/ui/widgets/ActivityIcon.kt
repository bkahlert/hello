package com.bkahlert.hello.clickup.ui.widgets

import androidx.compose.runtime.Composable
import com.bkahlert.hello.compose.color
import com.bkahlert.hello.semanticui.element.Icon

@Composable
fun ActivityIcon(activity: Activity<*>) {
    Icon("square") { activity.color?.also { style { color(it) } } }
}
