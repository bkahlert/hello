package com.bkahlert.hello.clickup.view

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.element.Icon
import com.bkahlert.hello.ui.compose.color

@Composable
public fun ActivityIcon(activity: Activity<*>) {
    Icon("square") { activity.color?.also { style { color(it) } } }
}
