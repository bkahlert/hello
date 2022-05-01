package com.bkahlert.hello.plugins.clickup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.plugins.clickup.Pomodoro.Companion.format
import com.bkahlert.hello.plugins.clickup.Pomodoro.Type
import com.bkahlert.hello.ui.AcousticFeedback
import com.bkahlert.kommons.compose.data
import com.bkahlert.kommons.time.toMoment
import com.clickup.api.Tag
import com.clickup.api.TaskID
import com.semanticui.compose.element.Icon
import com.semanticui.compose.element.IconGroup
import com.semanticui.compose.module.Checkbox
import com.semanticui.compose.module.CheckboxElementType.Toggle
import com.semanticui.compose.module.Divider
import com.semanticui.compose.module.DropdownState
import com.semanticui.compose.module.DropdownStateImpl
import com.semanticui.compose.module.Header
import com.semanticui.compose.module.InlineDropdown
import com.semanticui.compose.module.Item
import com.semanticui.compose.module.Menu
import com.semanticui.compose.module.Text
import com.semanticui.compose.module.scrolling
import org.jetbrains.compose.web.attributes.InputType.Checkbox
import org.jetbrains.compose.web.attributes.InputType.Hidden
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.Text

@Stable
interface PomodoroStarterState : DropdownState {
    val taskID: TaskID?
    var billable: Boolean
    val acousticFeedback: AcousticFeedback
    val availableTypes: List<Type>
    var selectedType: Type?
    val selectedTypeOrDefault: Type get() = selectedType ?: Type.Default
    val onTypeSelect: (oldSelectedType: Type?, newSelectedType: Type?) -> Unit
}

class PomodoroStarterStateImpl(
    override val taskID: TaskID?,
    billable: Boolean,
    override val acousticFeedback: AcousticFeedback,
    override val availableTypes: List<Type>,
    selectedType: Type?,
    override val onTypeSelect: (oldSelectedType: Type?, newSelectedType: Type?) -> Unit,
    debug: Boolean,
    message: Map<String, String?>,
    placeholder: String?,
    private val toString: (Type) -> String = { it.name },
    private val fromString: (String) -> Type = run {
        val mappings: Map<String, Type> = availableTypes.associateBy { it.name }
        ({ mappings.getValue(it) })
    },
) : PomodoroStarterState, DropdownState by DropdownStateImpl(
    availableTypes.map { toString(it) },
    selectedType?.let { toString(it) },
    { old, new -> onTypeSelect(old?.let(fromString), new?.let(fromString)) },
    debug,
    message,
    placeholder,
) {
    override var billable: Boolean by mutableStateOf(billable)

    override var selectedType: Type?
        get() = selectedValue?.let { fromString(it) }
        set(value) {
            selectedValue = value?.let { toString(it) }
        }
}

@Composable
fun rememberPomodoroStarterState(
    taskID: TaskID? = null,
    billable: Boolean = false,
    acousticFeedback: AcousticFeedback = AcousticFeedback.NoFeedback,
    vararg types: Type = Type.values(),
    selected: (Type) -> Boolean = { it == Type.Default },
    onTypeSelect: (oldSelectedType: Type?, newSelectedType: Type?) -> Unit = { old, new ->
        console.log("selection changed from $old to $new")
    },
    debug: Boolean = false,
    message: Map<String, String?> = emptyMap(),
    placeholder: String? = "Select duration...",
): PomodoroStarterState {
    val selectedType = types.firstOrNull(selected)
    val availableTypes = types.toList()
    return remember(taskID, billable, acousticFeedback, selectedType, availableTypes, onTypeSelect, debug, message, placeholder) {
        PomodoroStarterStateImpl(
            taskID = taskID,
            billable = billable,
            acousticFeedback = acousticFeedback,
            availableTypes = availableTypes,
            selectedType = selectedType,
            onTypeSelect = onTypeSelect,
            debug = debug,
            message = message,
            placeholder = placeholder
        )
    }
}

@Composable
fun PomodoroStarter(
    state: PomodoroStarterState = rememberPomodoroStarterState(),
    start: () -> Boolean = { false },
    onStart: (TaskID?, List<Tag>, billable: Boolean) -> Unit = { _, _, _ -> },
) {
    IconGroup({
        +Link
        if (start()) {
            onStart(state.taskID, listOf(state.selectedTypeOrDefault.tag), state.billable)
        }
        onClick {
            it.preventDefault()
            onStart(state.taskID, listOf(state.selectedTypeOrDefault.tag), state.billable)
        }
    }) {
        Icon("green", "play")
        if (state.billable) Icon("green", "dollar") { +Position.Bottom + Position.Right + Corner }
    }
    InlineDropdown(state) {
        Input(Hidden) { name("type");value(state.selectedTypeOrDefault.name) }
        Text { Text(state.selectedTypeOrDefault.duration.format()) }
        Icon("dropdown")
        Menu {
            Checkbox(Toggle, { classes("input") }) {
                Input(Checkbox) {
                    tabIndex(0)
                    checked(state.billable)
                    onChange { state.billable = it.value }
                }
                Label { Text("Billable") }
            }
            Divider()
            Header {
                Icon("stopwatch")
                Text("Select Duration")
            }
            Menu({ +scrolling }) {
                Type.values().forEach { type ->
                    Item({
                        data("value", type.name)
                    }) {
                        Text(type.duration.toMoment(comparative = false))
                    }
                }
            }
        }
    }
}
