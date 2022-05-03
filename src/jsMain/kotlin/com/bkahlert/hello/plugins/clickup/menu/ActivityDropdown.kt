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
interface ActivityDropdownState : DropdownState {
    val availableActivityGroups: List<ActivityGroup>
    var selectedActivity: Activity<*>?
    val onActivitySelect: (oldSelectedActivity: Activity<*>?, newSelectedActivity: Activity<*>?) -> Unit
}

class ActivityDropdownStateImpl(
    override val availableActivityGroups: List<ActivityGroup>,
    selectedActivity: Activity<*>?,
    override val onActivitySelect: (oldSelectedActivity: Activity<*>?, newSelectedActivity: Activity<*>?) -> Unit,
    options: Map<String, Any?>,
    private val toString: (Activity<*>) -> String? = { it.id?.typedStringValue },
    private val fromString: (String?) -> Activity<*> = run {
        val mappings: Map<String?, Activity<*>> = availableActivityGroups.activities.associateBy { it.id?.typedStringValue }
        ({ mappings.getValue(it) })
    },
) : ActivityDropdownState, DropdownState by DropdownStateImpl(
    availableValues = availableActivityGroups.activities.map { toString(it) ?: "" },
    selectedValue = selectedActivity?.let { toString(it) },
    onSelect = { old, new -> onActivitySelect(old?.let(fromString), new?.let(fromString)) },
    options = options,
) {
    override var selectedActivity: Activity<*>?
        get() = selectedValue?.let(fromString)
        set(value) {
            selectedValue = value?.let(toString)
        }
}

@Composable
fun rememberActivityDropdownState(
    availableActivityGroups: List<ActivityGroup> = emptyList(),
    selectedActivity: Activity<*>? = null,
    onActivitySelect: (oldSelectedActivity: Activity<*>?, newSelectedActivity: Activity<*>?) -> Unit = { old, new ->
        console.log("selection changed from $old to $new")
    },
    debug: Boolean = false,
): ActivityDropdownState {
    return remember(availableActivityGroups, selectedActivity, onActivitySelect) {
        ActivityDropdownStateImpl(
            availableActivityGroups = availableActivityGroups,
            selectedActivity = selectedActivity,
            onActivitySelect = onActivitySelect,
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
    when (val selectedActivity = state.selectedActivity) {
        null -> Icon { +Inverted }
        else -> ActivityIcon(selectedActivity)
    }

    InlineDropdown(state, {
        style {
            flex(1, 1)
            minWidth("0") // https://css-tricks.com/flexbox-truncated-text/
        }
    }) {
        Input(Hidden) { name("activity");value(state.selectedActivity?.id?.typedStringValue ?: "") }
        Text({
            style {
                maxWidth(100.percent)
                textOverflow()
                lineHeight(1.1.em)
            }
        }) {
            when (val task = state.selectedActivity) {
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
                state.availableActivityGroups.onEachIndexed { index, (meta, color, activities) ->
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
