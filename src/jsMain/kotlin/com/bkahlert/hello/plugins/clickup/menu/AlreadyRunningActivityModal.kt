package com.bkahlert.hello.plugins.clickup.menu

import androidx.compose.runtime.Composable
import com.bkahlert.hello.plugins.clickup.Pomodoro
import com.bkahlert.hello.plugins.clickup.menu.Activity.RunningTaskActivity
import com.bkahlert.kommons.time.toMoment
import com.semanticui.compose.element.Icon
import com.semanticui.compose.element.IconHeader
import com.semanticui.compose.module.Actions
import com.semanticui.compose.module.ApproveButton
import com.semanticui.compose.module.BasicModal
import com.semanticui.compose.module.Content
import com.semanticui.compose.module.DenyButton
import com.semanticui.compose.module.closable
import com.semanticui.compose.module.onApprove
import com.semanticui.compose.module.onDeny
import com.semanticui.compose.module.size
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Small
import org.jetbrains.compose.web.dom.Text

@Composable
fun AlreadyRunningActivityModal(
    runningTaskActivity: RunningTaskActivity,
    onContinue: () -> Unit = { console.log("onContinue()") },
    onAbort: () -> Unit = { console.log("onAbort()") },
) {

    val icon = arrayOf("red", "stop", "circle")
    val pomodoro = Pomodoro.of(runningTaskActivity.timeEntry)

    BasicModal({
        +size.Small
        onApprove = { onContinue(); true }
        onDeny = { onAbort();true }
        closable = false
    }) {
        IconHeader(*icon) {
            Text("Already running session")
            Br()
            Small { Text(pomodoro.duration.toMoment(comparative = false)) }
        }
        Content {
            P { Text("A pomodoro session with ID ${runningTaskActivity.id.stringValue} is already running.") }
            P { Text("Do you want to continue and stop this session first?") }
        }
        Actions {
            ApproveButton({
                +Colored.Blue
                +Inverted
            }) {
                Icon("stop", "circle")
                Text("Continue")
            }
            DenyButton({
                +Basic
                +Inverted
            }) {
                Icon("remove")
                Text("Abort")
            }
        }
    }
}
