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
import com.semanticui.compose.element.Button
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
    val onTypeSelect: (oldSelectedType: Type?, newSelectedType: Type?) -> Unit
    fun onStart()
    val onCloseTask: (() -> Unit)?
}

class PomodoroStarterStateImpl(
    override val taskID: TaskID?,
    billable: Boolean,
    override val acousticFeedback: AcousticFeedback,
    override val availableTypes: List<Type>,
    selectedType: Type?,
    override val onTypeSelect: (oldSelectedType: Type?, newSelectedType: Type?) -> Unit,
    private val onStart: (selectedType: Type?, billable: Boolean) -> Unit,
    override val onCloseTask: (() -> Unit)?,
    options: Map<String, Any?>,
    private val toString: (Type) -> String = { it.name },
    private val fromString: (String) -> Type = run {
        val mappings: Map<String, Type> = availableTypes.associateBy { it.name }
        ({ mappings.getValue(it) })
    },
) : PomodoroStarterState, DropdownState by DropdownStateImpl(
    availableTypes.map { toString(it) },
    selectedType?.let { toString(it) },
    { old, new -> onTypeSelect(old?.let(fromString), new?.let(fromString)) },
    options,
) {
    override var billable: Boolean by mutableStateOf(billable)

    override var selectedType: Type?
        get() = selectedValue?.let { fromString(it) }
        set(value) {
            selectedValue = value?.let { toString(it) }
        }

    override fun onStart() {
        onStart(selectedType, billable)
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
    onStart: (TaskID?, List<Tag>, billable: Boolean) -> Unit = { id, tags, bill ->
        console.log("started ${if (bill) "billable " else ""}$id with $tags")
    },
    onCloseTask: (() -> Unit)? = { console.log("close task") },
    debug: Boolean = false,
): PomodoroStarterState {
    val selectedType = types.firstOrNull(selected)
    val availableTypes = types.toList()
    val options = mapOf("debug" to debug, "placeholder" to "Select duration...")
    return remember(taskID, billable, acousticFeedback, selectedType, availableTypes, onTypeSelect, onStart) {
        PomodoroStarterStateImpl(
            taskID = taskID,
            billable = billable,
            acousticFeedback = acousticFeedback,
            availableTypes = availableTypes,
            selectedType = selectedType,
            onTypeSelect = onTypeSelect,
            onStart = { type, billable -> onStart(taskID, listOf((type ?: Type.Default).tag), billable) },
            onCloseTask = onCloseTask,
            options = options,
        )
    }
}

@Composable
fun PomodoroStarter(
    state: PomodoroStarterState = rememberPomodoroStarterState(),
    start: () -> Boolean = { false },
) {
    IconGroup({
        +Link
        if (start()) {
            state.onStart()
        }
        onClick {
            it.preventDefault()
            state.onStart()
        }
    }) {
        Icon("green", "play")
        if (state.billable) Icon("green", "dollar") { +Position.Bottom + Position.Right + Corner }
    }
    InlineDropdown(state) {
        Input(Hidden) { name("type");value((state.selectedType ?: Type.Default).name) }
        Text { Text((state.selectedType ?: Type.Default).duration.format()) }
        Icon("dropdown")
        Menu {
            Button({
                +Size.Mini
                +Icon
                classes("input", "positive")
                when (val onCloseTask = state.onCloseTask) {
                    null -> +Disabled
                    else -> onClick { onCloseTask() }
                }
            }) {
                Text("Close task")
                Icon("check")
            }
            Divider()
            Header {
                Text("Options")
            }
            Checkbox(Toggle, { classes("input") }) {
                Input(Checkbox) {
                    tabIndex(0)
                    checked(state.billable)
                    onChange { state.billable = it.value }
                }
                Label { Text("Billable") }
            }
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
