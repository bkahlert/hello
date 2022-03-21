package com.bkahlert.hello.plugins.clickup

import androidx.compose.runtime.Composable
import com.bkahlert.hello.ui.textOverflow
import com.semanticui.compose.Variation
import com.semanticui.compose.element.Icon
import com.semanticui.compose.element.Input
import com.semanticui.compose.module.Divider
import com.semanticui.compose.module.DropdownMenu
import com.semanticui.compose.module.DropdownText
import com.semanticui.compose.module.Header
import com.semanticui.compose.module.InlineDropdown
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
    selectedActivity: Activity<*>? = null,
    onSelect: (Activity<*>) -> Unit = {},
) {
    selectedActivity?.also { TaskIcon(it) } ?: Icon({ variation(Inverted) })
    InlineDropdown(
        key = activityGroups,
        onChange = { value, _, _ ->
            onSelect(activityGroups.firstNotNullOf { (_, _, activities) ->
                activities.firstOrNull { it.id?.stringValue == value }
            })
        },
        attrs = {
            variation(Variation.Scrolling)
            style {
                flex(1, 1)
                minWidth("0") // https://css-tricks.com/flexbox-truncated-text/
            }
        }
    ) {
        DropdownText({
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
        DropdownMenu({
            style { maxWidth(200.percent) }
        }) {
            Input({ variation(Variation.Icon("search")) }) {
                Input(Text) { placeholder("Search tasks...") }
                Icon("search")
            }

            activityGroups.onEach { (meta, color, activities) ->
                Divider()
                Header({ style { color?.also { color(it) } } }) {
                    meta.forEachIndexed { index, meta ->
                        if (index > 0) Icon("inverted")
                        Icon({
                            title(meta.title)
                            style { classes(*meta.iconVariations.toTypedArray()) }
                        })
                        meta.text?.also { Text(it) }
                    }
                }
                activities.forEach { activity ->
                    ActivityItem(activity)
                }
            }
        }
    }
}
