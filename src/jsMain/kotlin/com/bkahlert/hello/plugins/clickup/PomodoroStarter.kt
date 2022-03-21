package com.bkahlert.hello.plugins.clickup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.plugins.clickup.Pomodoro.Companion.format
import com.bkahlert.hello.plugins.clickup.Pomodoro.Type
import com.clickup.api.Tag
import com.clickup.api.Task
import com.semanticui.compose.State.Disabled
import com.semanticui.compose.element.Icon
import com.semanticui.compose.module.Checkbox
import com.semanticui.compose.module.CheckboxElementType.Toggle
import com.semanticui.compose.module.DropdownMenu
import com.semanticui.compose.module.DropdownText
import com.semanticui.compose.module.InlineDropdown
import com.semanticui.compose.view.Item
import org.jetbrains.compose.web.attributes.InputType.Checkbox
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.Text

@Composable
fun PomodoroStarter(
    taskID: Task.ID?,
    type: Type = Type.Default,
    billable: Boolean = false,
    onStart: (Task.ID, List<Tag>, billable: Boolean) -> Unit = { _, _, _ -> },
) {
    var selectedType by remember { mutableStateOf(type) }
    var selectedBillable by remember { mutableStateOf(billable) }
    val icon = if (selectedBillable) "dollar" else "play"

    if (taskID != null) {
        Icon("green", icon, {
            +Link
            onClick { onStart(taskID, listOf(selectedType.tag), selectedBillable) }
        })
    } else {
        Icon("green", icon, "disabled")
    }
    InlineDropdown(taskID,
        { value, _, _ -> selectedType = enumValueOf(value) }) {
        DropdownText { Text(selectedType.duration.format()) }
        Icon("dropdown")
        DropdownMenu {
            Item({
                attr("data-value", selectedType.name)
                attr("data-text", selectedType.duration.format())
            }) {
                Checkbox(Toggle) {
                    Input(Checkbox) {
                        checked(selectedBillable)
                        onChange { selectedBillable = it.value }
                    }
                    Label {
                        if (selectedBillable) {
                            Icon("green", "dollar")
                            Text("Billable")
                        } else {
                            Icon("green", "dollar", { +Disabled })
                            Text("Non-Billable")
                        }
                    }
                }
            }
            Type.values().forEach {
                Item({ attr("data-value", it.name) }) { Text(it.duration.format()) }
            }
        }
    }
}
