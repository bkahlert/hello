package com.bkahlert.hello.clickup.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.clickup.Pomodoro.Companion.format
import com.bkahlert.hello.clickup.Pomodoro.Type
import com.bkahlert.hello.clickup.model.Tag
import com.bkahlert.hello.clickup.model.TaskID
import com.bkahlert.hello.dom.AcousticFeedback
import com.bkahlert.hello.semanticui.element.Button
import com.bkahlert.hello.semanticui.element.Icon
import com.bkahlert.hello.semanticui.element.IconGroup
import com.bkahlert.hello.semanticui.module.Checkbox
import com.bkahlert.hello.semanticui.module.CheckboxElementType.Toggle
import com.bkahlert.hello.semanticui.module.Divider
import com.bkahlert.hello.semanticui.module.DropdownState
import com.bkahlert.hello.semanticui.module.DropdownStateImpl
import com.bkahlert.hello.semanticui.module.Header
import com.bkahlert.hello.semanticui.module.InlineDropdown
import com.bkahlert.hello.semanticui.module.Item
import com.bkahlert.hello.semanticui.module.Menu
import com.bkahlert.hello.semanticui.module.Text
import com.bkahlert.hello.ui.compose.data
import com.bkahlert.kommons.toMomentString
import org.jetbrains.compose.web.attributes.InputType.Checkbox
import org.jetbrains.compose.web.attributes.InputType.Hidden
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.Text

@Stable
public interface PomodoroStarterState : DropdownState<Type> {
    public val taskID: TaskID?
    public var billable: Boolean
    public val acousticFeedback: AcousticFeedback
    public fun onStart()
    public val onCloseTask: (() -> Unit)?
}

public class PomodoroStarterStateImpl(
    override val taskID: TaskID?,
    billable: Boolean,
    override val acousticFeedback: AcousticFeedback,
    values: List<Type>,
    selection: Type?,
    onSelect: (old: Type?, new: Type?) -> Unit,
    private val onStart: (selectedType: Type?, billable: Boolean) -> Unit,
    override val onCloseTask: (() -> Unit)?,
    options: Map<String, Any?>,
    serializer: (Type) -> String = { it.name },
    deserializer: (String) -> Type = run {
        val mappings: Map<String, Type> = values.associateBy { it.name }
        ({ mappings.getValue(it) })
    },
) : PomodoroStarterState, DropdownState<Type> by DropdownStateImpl(
    options,
    values,
    selection,
    onSelect,
    serializer,
    deserializer,
) {
    override var billable: Boolean by mutableStateOf(billable)
    override fun onStart() {
        onStart(selection, billable)
    }
}

@Composable
public fun rememberPomodoroStarterState(
    taskID: TaskID? = null,
    billable: Boolean = false,
    acousticFeedback: AcousticFeedback = AcousticFeedback.NoFeedback,
    vararg types: Type = Type.values(),
    selected: (Type) -> Boolean = { it == Type.Default },
    onSelect: (old: Type?, new: Type?) -> Unit = { old, new ->
        console.log("selection changed from $old to $new")
    },
    onStart: (TaskID?, List<Tag>, billable: Boolean) -> Unit = { id, tags, bill ->
        console.log("started ${if (bill) "billable " else ""}$id with $tags")
    },
    onCloseTask: (() -> Unit)? = { console.log("close task") },
    debug: Boolean = false,
): PomodoroStarterState {
    val selection = types.firstOrNull(selected)
    val options = mapOf("debug" to debug, "placeholder" to "Select duration...")
    return remember(taskID, billable, acousticFeedback, selection, types, onSelect, onStart) {
        PomodoroStarterStateImpl(
            taskID = taskID,
            billable = billable,
            acousticFeedback = acousticFeedback,
            values = types.toList(),
            selection = selection,
            onSelect = onSelect,
            onStart = { type, billable -> onStart(taskID, listOf((type ?: Type.Default).tag), billable) },
            onCloseTask = onCloseTask,
            options = options,
        )
    }
}

@Composable
public fun PomodoroStarter(
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
        Input(Hidden) { name("type");value(state.selectionString) }
        Text { Text(state.selection?.duration?.format() ?: "") }
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
                    Text(type.duration.toMomentString(descriptive = false))
                }
            }
        }
    }
}
