package com.bkahlert.hello.clickup.view

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.custom.color
import com.bkahlert.semanticui.element.Icon

@Composable
public fun ActivityIcon(activity: Activity<*>) {
    Icon("square") { activity.color?.also { style { color(it) } } }
}
