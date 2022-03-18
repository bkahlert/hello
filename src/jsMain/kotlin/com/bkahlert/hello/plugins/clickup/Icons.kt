package com.bkahlert.hello.plugins.clickup

import androidx.compose.runtime.Composable
import com.bkahlert.hello.plugins.clickup.ClickupState.Loaded.Activated.Activity
import com.bkahlert.hello.plugins.clickup.ClickupState.Loaded.Activated.ActivityGroup.Meta
import com.clickup.api.Task
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.element.Icon
import com.semanticui.compose.element.IconElement
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLElement

@Composable
fun MetaIcon(
    meta: Meta,
    attrs: SemanticAttrBuilder<IconElement, HTMLElement>? = null,
) {
    Icon({
        title(meta.title)
        attrs?.invoke(this)
        style { classes(*meta.iconVariations.toTypedArray()) }
    })
    meta.text?.also { Text(it) }
}

@Composable
fun TaskIcon(activity: Activity<*>) {
    Icon("square", { activity.color?.also { style { color(it) } } })
}

@Deprecated("use ActivityIcon")
@Composable
fun TaskIcon(task: Task) {
    Icon("square", { style { color(task.status.color) } })
}
