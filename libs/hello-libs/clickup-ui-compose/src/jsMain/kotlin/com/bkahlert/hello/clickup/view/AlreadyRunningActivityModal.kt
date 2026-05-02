package com.bkahlert.hello.clickup.view

import androidx.compose.runtime.Composable
import com.bkahlert.hello.clickup.Pomodoro
import com.bkahlert.hello.clickup.view.Activity.RunningTaskActivity
import com.bkahlert.kommons.time.toMomentString
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.element.BasicButton
import com.bkahlert.semanticui.element.Button
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.IconHeader
import com.bkahlert.semanticui.module.BasicModal
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Small
import org.jetbrains.compose.web.dom.Text

@Composable
public fun AlreadyRunningActivityModal(
    runningTaskActivity: RunningTaskActivity,
    onContinue: () -> Unit = { console.log("onContinue()") },
    onAbort: () -> Unit = { console.log("onAbort()") },
) {

    val icon = arrayOf("red", "stop", "circle")
    val pomodoro = Pomodoro.of(runningTaskActivity.timeEntry)

    BasicModal({
        +"small"
        settings {
            onApprove = { onContinue(); true }
            onDeny = { onAbort();true }
            closable = false
        }
    }) {
        IconHeader(*icon) {
            Text("Already running session")
            Br()
            Small { Text(pomodoro.duration.toMomentString(descriptive = false)) }
        }
        S("content") {
            P { Text("A pomodoro session with ID ${runningTaskActivity.id.stringValue} is already running.") }
            P { Text("Do you want to continue and stop this session first?") }
        }
        S("actions") {
            Button({
                +"approve"
                +"blue"
                +"inverted"
            }) {
                Icon("stop", "circle")
                Text("Continue")
            }
            BasicButton({ +"deny" }) {
                Icon("remove")
                Text("Abort")
            }
        }
    }
}
