package com.bkahlert.hello.plugins.clickup.menu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.bkahlert.hello.ui.textOverflow
import com.semanticui.compose.element.Icon
import com.semanticui.compose.element.Input
import com.semanticui.compose.module.Divider
import com.semanticui.compose.module.DropdownState
import com.semanticui.compose.module.DropdownStateImpl
import com.semanticui.compose.module.Header
import com.semanticui.compose.module.InlineDropdown
import com.semanticui.compose.module.Menu
import com.semanticui.compose.module.Text
import com.semanticui.compose.module.scrolling
import org.jetbrains.compose.web.attributes.InputType.Hidden
import org.jetbrains.compose.web.attributes.InputType.Text
import org.jetbrains.compose.web.attributes.name
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

@Stable
interface ActivityDropdownState : DropdownState<Activity<*>> {
    val groups: List<ActivityGroup>
}

class ActivityDropdownStateImpl(
    override val groups: List<ActivityGroup>,
    selection: Activity<*>?,
    override val onSelect: (old: Activity<*>?, new: Activity<*>?) -> Unit,
    options: Map<String, Any?>,
    serializer: (Activity<*>) -> String = { it.id.typedStringValue },
    deserializer: (String) -> Activity<*> = run {
        val mappings: Map<String?, Activity<*>> = groups.activities.associateBy { it.id.typedStringValue }
        ({ mappings.getValue(it) })
    },
) : ActivityDropdownState, DropdownState<Activity<*>> by DropdownStateImpl(
    options,
    groups.activities,
    selection,
    onSelect,
    serializer,
    deserializer,
)

@Composable
fun rememberActivityDropdownState(
    groups: List<ActivityGroup> = emptyList(),
    selection: Activity<*>? = null,
    onSelect: (old: Activity<*>?, new: Activity<*>?) -> Unit = { old, new ->
        console.log("selection changed from $old to $new")
    },
    debug: Boolean = false,
): ActivityDropdownState {
    return remember(groups, selection, onSelect) {
        ActivityDropdownStateImpl(
            groups = groups,
            selection = selection,
            onSelect = onSelect,
            options = mapOf(
                "debug" to debug,
                "fullTextSearch" to true,
                "placeholder" to "Select task...",
            ),
        )
    }
}

@Composable
fun ActivityDropdown(
    state: ActivityDropdownState = rememberActivityDropdownState(),
) {
    when (val selectedActivity = state.selection) {
        null -> Icon { +Inverted }
        else -> ActivityIcon(selectedActivity)
    }

    InlineDropdown(state, {
        style {
            flex(1, 1)
            minWidth("0") // https://css-tricks.com/flexbox-truncated-text/
        }
    }) {
        Input(Hidden) { name("activity");value(state.selection?.id?.typedStringValue ?: "") }
        Text({
            style {
                maxWidth(100.percent)
                textOverflow()
                lineHeight(1.1.em)
            }
        }) {
            when (val task = state.selection) {
                null -> Text("Select task...")
                else -> Text(task.name)
            }
        }
        Menu({
            style { maxWidth(200.percent) }
        }) {
            Header {
                Icon("search")
                Text("Search tasks")
            }
            Input({ +Icon("search") }) {
                Input(Text) { placeholder("Search tasks...") }
                Icon("search")
            }
            Menu({ +scrolling }) {
                state.groups.onEachIndexed { index, (meta, color, activities) ->
                    if (index > 0) Divider()
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
}
