package com.bkahlert.hello.clickup.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.clickup.model.TaskListID
import com.bkahlert.kommons.quoted
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.custom.textOverflow
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.module.DropdownState
import com.bkahlert.semanticui.module.DropdownStateImpl
import com.bkahlert.semanticui.module.InlineDropdown
import com.bkahlert.semanticui.module.Menu
import com.bkahlert.semanticui.module.SemanticDropdownSettings
import com.bkahlert.semanticui.module.SemanticModuleSettingsBuilder
import com.bkahlert.semanticui.module.Text
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
import org.w3c.dom.HTMLElement

@Stable
public interface ActivityDropdownState : DropdownState<Activity<*>> {
    public val groups: List<ActivityGroup>
    public val onCreate: (TaskListID, String?) -> Unit
}

public class ActivityDropdownStateImpl(
    override val groups: List<ActivityGroup>,
    selection: Activity<*>?,
    override val onSelect: (old: Activity<*>?, new: Activity<*>?) -> Unit,
    override val onCreate: (TaskListID, String?) -> Unit,
    serializer: (Activity<*>) -> String = { it.id.typedStringValue },
    deserializer: (String) -> Activity<*> = run {
        val mappings: Map<String?, Activity<*>> = groups.activities.associateBy { it.id.typedStringValue }
        ({ mappings.getValue(it) })
    },
    settings: SemanticModuleSettingsBuilder<SemanticDropdownSettings>,
) : ActivityDropdownState, DropdownState<Activity<*>> by DropdownStateImpl(
    groups.activities,
    selection,
    onSelect,
    serializer,
    deserializer,
    settings,
)

@Composable
public fun rememberActivityDropdownState(
    groups: List<ActivityGroup> = emptyList(),
    selection: Activity<*>? = null,
    onSelect: (old: Activity<*>?, new: Activity<*>?) -> Unit = { old, new ->
        console.log("selection changed from $old to $new")
    },
    onCreate: (TaskListID, String?) -> Unit = { taskListId, name ->
        console.log("task added to $taskListId with name ${name?.quoted}")
    },
    settings: SemanticModuleSettingsBuilder<SemanticDropdownSettings> = {},
): ActivityDropdownState {
    return remember(groups, selection, onSelect) {
        ActivityDropdownStateImpl(
            groups = groups,
            selection = selection,
            onSelect = onSelect,
            onCreate = onCreate,
        ) {
            fullTextSearch = true
            placeholder = "Select task..."
            settings.invoke(this)
        }
    }
}

@Composable
public fun ActivityDropdown(
    state: ActivityDropdownState = rememberActivityDropdownState {},
) {
    var query by mutableStateOf("")

    when (val selectedActivity = state.selection) {
        null -> Icon("inverted")
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
                maxWidth(130.percent)
            }
        }) {
            S("header") {
                Icon("search")
                Text("Search tasks")
            }
            S("ui", "icon", "input") {
                Input(Text) {
                    placeholder("Search tasks...")
                    value(query)
                    onInput { query = it.value }
                }
                Icon("search")
            }
            Menu({
                +"scrolling"
            }) {
                DisposableEffect(Unit) {
                    (scopeElement as? HTMLElement)?.style?.setProperty("margin-right", "0", "important")
                    onDispose { }
                }
                state.groups.onEachIndexed { index, group ->
                    if (index > 0) S("divider")
                    ActivityGroupHeader(group, onCreate = group.listId?.let { ({ state.onCreate(it, query.takeUnless { it.isEmpty() }) }) })
                    ActivityItems(group.tasks)
                }
            }
        }
    }
}
