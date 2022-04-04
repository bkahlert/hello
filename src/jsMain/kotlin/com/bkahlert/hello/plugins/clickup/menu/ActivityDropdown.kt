package com.bkahlert.hello.plugins.clickup.menu

import androidx.compose.runtime.Composable
import com.bkahlert.hello.plugins.clickup.Selection
import com.bkahlert.hello.ui.textOverflow
import com.clickup.api.Identifier
import com.semanticui.compose.element.Icon
import com.semanticui.compose.element.Input
import com.semanticui.compose.module.Divider
import com.semanticui.compose.module.Header
import com.semanticui.compose.module.InlineDropdown
import com.semanticui.compose.module.Menu
import com.semanticui.compose.module.Text
import com.semanticui.compose.module.onChange
import org.jetbrains.compose.web.attributes.InputType.Text
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.flex
import org.jetbrains.compose.web.css.lineHeight
import org.jetbrains.compose.web.css.maxWidth
import org.jetbrains.compose.web.css.minWidth
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Text

@Composable
fun ActivityDropdown(
    activityGroups: List<ActivityGroup>,
    onSelect: (Selection) -> Unit = {},
) {
    val selectedActivity: Activity<*>? = activityGroups.selected.firstOrNull()

    if (selectedActivity != null) {
        ActivityIcon(selectedActivity)
    } else {
        Icon { +Inverted }
    }

    InlineDropdown(
        activityGroups,
        {
            +Scrolling
            style {
                flex(1, 1)
                minWidth("0") // https://css-tricks.com/flexbox-truncated-text/
            }
            onChange = { value ->
                val activityId = value.takeIf { it.isNotEmpty() }?.let { Identifier.of(it) }
                onSelect(listOfNotNull(activityId))
            }
        },
    ) {
        Text({
            style {
                maxWidth(100.percent)
                textOverflow()
                lineHeight(1.1.em)
            }
        }) {
            when (val task = selectedActivity) {
                null -> Text("Select task...")
                else -> Text(task.name)
            }
        }
        Menu({
            style { maxWidth(200.percent) }
        }) {
            Input({ +Icon("search") }) {
                Input(Text) { placeholder("Search tasks...") }
                Icon("search")
            }

            activityGroups.onEach { (meta, color, activities) ->
                Divider()
                Header({ style { color?.also { color(it) } } }) {
                    meta.forEachIndexed { index, meta ->
                        if (index > 0) Icon("inverted")
                        MetaIcon(meta)
                    }
                }
                activities.forEach { activity ->
                    ActivityItem(activity)
                }
            }
        }
    }
}
