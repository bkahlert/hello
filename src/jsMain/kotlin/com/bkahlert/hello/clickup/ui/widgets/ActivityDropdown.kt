package com.bkahlert.hello.clickup.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.clickup.api.TaskListID
import com.bkahlert.hello.semanticui.element.Icon
import com.bkahlert.hello.semanticui.element.Input
import com.bkahlert.hello.semanticui.module.Divider
import com.bkahlert.hello.semanticui.module.DropdownState
import com.bkahlert.hello.semanticui.module.DropdownStateImpl
import com.bkahlert.hello.semanticui.module.Header
import com.bkahlert.hello.semanticui.module.InlineDropdown
import com.bkahlert.hello.semanticui.module.Menu
import com.bkahlert.hello.semanticui.module.Text
import com.bkahlert.hello.semanticui.module.scrolling
import com.bkahlert.hello.ui.textOverflow
import com.bkahlert.kommons.debug.quoted
import com.bkahlert.kommons.takeUnlessBlank
import org.jetbrains.compose.web.attributes.InputType.Hidden
import org.jetbrains.compose.web.attributes.InputType.Text
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.flex
import org.jetbrains.compose.web.css.lineHeight
import org.jetbrains.compose.web.css.maxWidth
import org.jetbrains.compose.web.css.minWidth
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Text

@Stable
interface ActivityDropdownState : DropdownState<Activity<*>> {
    val groups: List<ActivityGroup>
    val onCreate: (TaskListID, String?) -> Unit
}

class ActivityDropdownStateImpl(
    override val groups: List<ActivityGroup>,
    selection: Activity<*>?,
    override val onSelect: (old: Activity<*>?, new: Activity<*>?) -> Unit,
    override val onCreate: (TaskListID, String?) -> Unit,
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
    onCreate: (TaskListID, String?) -> Unit = { taskListId, name ->
        console.log("task added to $taskListId with name ${name?.quoted}")
    },
    debug: Boolean = false,
): ActivityDropdownState {
    return remember(groups, selection, onSelect) {
        ActivityDropdownStateImpl(
            groups = groups,
            selection = selection,
            onSelect = onSelect,
            onCreate = onCreate,
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
    var query by mutableStateOf("")

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
        Input(Hidden) { name("activity");value(state.selectionString) }
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
            style {
                minWidth(350.px) // https://css-tricks.com/flexbox-truncated-text/
                maxWidth(150.percent)
            }
        }) {
            Header {
                Icon("search")
                Text("Search tasks")
            }
            Input({ +Icon("search") }) {
                Input(Text) {
                    placeholder("Search tasks...")
                    value(query)
                    onInput {
                        console.error(it.value)
                        query = it.value
                    }
                }
                Icon("search")
            }
            Menu({ +scrolling }) {
                state.groups.onEachIndexed { index, group ->
                    if (index > 0) Divider()
                    ActivityGroupHeader(group, onCreate = group.listId?.let { ({ state.onCreate(it, query.takeUnlessBlank()) }) })
                    ActivityItems(group.tasks)
                }
            }
        }
    }
}
