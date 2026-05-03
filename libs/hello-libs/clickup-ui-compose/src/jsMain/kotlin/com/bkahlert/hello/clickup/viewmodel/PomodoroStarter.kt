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
import com.bkahlert.kommons.js.console
import com.bkahlert.kommons.time.toMomentString
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.custom.data
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.IconButton
import com.bkahlert.semanticui.element.IconGroup
import com.bkahlert.semanticui.module.Checkbox
import com.bkahlert.semanticui.module.CheckboxElementType.Toggle
import com.bkahlert.semanticui.module.DropdownState
import com.bkahlert.semanticui.module.DropdownStateImpl
import com.bkahlert.semanticui.module.InlineDropdown
import com.bkahlert.semanticui.module.Item
import com.bkahlert.semanticui.module.Menu
import com.bkahlert.semanticui.module.SemanticDropdownSettings
import com.bkahlert.semanticui.module.SemanticModuleSettingsBuilder
import com.bkahlert.semanticui.module.Text
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
    public fun onStart()
    public val onCloseTask: (() -> Unit)?
}

public class PomodoroStarterStateImpl(
    override val taskID: TaskID?,
    billable: Boolean,
    values: List<Type>,
    selection: Type?,
    onSelect: (old: Type?, new: Type?) -> Unit,
    private val onStart: (selectedType: Type?, billable: Boolean) -> Unit,
    override val onCloseTask: (() -> Unit)?,
    serializer: (Type) -> String = { it.name },
    deserializer: (String) -> Type = run {
        val mappings: Map<String, Type> = values.associateBy { it.name }
        ({ mappings.getValue(it) })
    },
    settings: SemanticModuleSettingsBuilder<SemanticDropdownSettings>,
) : PomodoroStarterState, DropdownState<Type> by DropdownStateImpl(
    values,
    selection,
    onSelect,
    serializer,
    deserializer,
    settings,
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
    vararg types: Type = Type.values(),
    selected: (Type) -> Boolean = { it == Type.Default },
    onSelect: (old: Type?, new: Type?) -> Unit = { old, new ->
        console.debug("selection changed from $old to $new")
    },
    onStart: (TaskID?, List<Tag>, billable: Boolean) -> Unit = { id, tags, bill ->
        console.debug("started ${if (bill) "billable " else ""}$id with $tags")
    },
    onCloseTask: (() -> Unit)? = { console.debug("close task") },
    debug: Boolean = false,
): PomodoroStarterState {
    val selection = types.firstOrNull(selected)
    return remember(taskID, billable, selection, types, onSelect, onStart) {
        PomodoroStarterStateImpl(
            taskID = taskID,
            billable = billable,
            values = types.toList(),
            selection = selection,
            onSelect = onSelect,
            onStart = { type, billable -> onStart(taskID, listOf((type ?: Type.Default).tag), billable) },
            onCloseTask = onCloseTask,
        ) {
            this.debug = debug
            placeholder = "Select duration..."
        }
    }
}

@Composable
public fun PomodoroStarter(
    state: PomodoroStarterState = rememberPomodoroStarterState(),
    start: () -> Boolean = { false },
) {
    IconGroup({
        classes("link")
        if (start()) {
            state.onStart()
        }
        onClick {
            // The play icon is the start trigger. Stop propagation so the click
            // does not bubble up to the surrounding LinkItem, where a sibling
            // handler opens the duration dropdown for any other click in the
            // pomodoro button area.
            it.preventDefault()
            it.stopPropagation()
            state.onStart()
        }
    }) {
        Icon("green", "play")
        if (state.billable) Icon("green", "dollar") { classes("bottom", "right", "corner") }
    }
    InlineDropdown(state, {
        style { property("cursor", "pointer") }
        // Whole-area click: any click inside the duration button rectangle
        // (text, caret, padding) opens the dropdown.
        // Same shadow-DOM workaround as the avatar fix: Semantic UI's
        // dropdown toggle uses `document.body.contains(e.target)`, which
        // is false for elements inside the `<clickup-menu-v2>` shadow
        // root, so direct clicks are silently ignored. Forward to the
        // child caret which has its own delegated handler without that guard.
        // Skip when the click is on the caret itself (SUI already toggles) or
        // inside the open menu panel (those clicks should select / dismiss).
        // Anchor `.menu` to the dropdown subtree so the surrounding toolbar
        // `.ui.menu` (which contains this dropdown) doesn't suppress legit
        // wrapper clicks.
        onClick { event ->
            val target = event.nativeEvent.target as? org.w3c.dom.Element ?: return@onClick
            if (target.asDynamic().closest(".dropdown.icon") != null) return@onClick
            if (target.asDynamic().closest(".dropdown > .menu") != null) return@onClick
            val el = event.nativeEvent.currentTarget as? org.w3c.dom.HTMLElement
            (el?.querySelector(":scope > i.dropdown.icon") as? org.w3c.dom.HTMLElement)?.click()
        }
    }) {
        Input(Hidden) { name("type"); value(state.selectionString) }
        Text({
            style { property("cursor", "pointer") }
        }) { Text(state.selection?.duration?.format() ?: "") }
        Icon("dropdown")
        Menu {
            IconButton({
                +"mini"
                classes("input", "positive")
                when (val onCloseTask = state.onCloseTask) {
                    null -> +"disabled"
                    else -> onClick { onCloseTask() }
                }
            }) {
                Text("Close task")
                Icon("check")
            }
            S("divider")
            S("header") {
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
