package com.bkahlert.hello.clickup.view

import androidx.compose.runtime.Composable
import com.bkahlert.hello.clickup.Pomodoro
import com.bkahlert.hello.clickup.view.Activity.RunningTaskActivity
import com.bkahlert.hello.semanticui.element.Icon
import com.bkahlert.hello.semanticui.element.IconHeader
import com.bkahlert.hello.semanticui.module.Actions
import com.bkahlert.hello.semanticui.module.ApproveButton
import com.bkahlert.hello.semanticui.module.BasicModal
import com.bkahlert.hello.semanticui.module.Content
import com.bkahlert.hello.semanticui.module.DenyButton
import com.bkahlert.hello.semanticui.module.closable
import com.bkahlert.hello.semanticui.module.onApprove
import com.bkahlert.hello.semanticui.module.onDeny
import com.bkahlert.hello.semanticui.module.size
import com.bkahlert.kommons.toMomentString
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
        +size.Small
        onApprove = { onContinue(); true }
        onDeny = { onAbort();true }
        closable = false
    }) {
        IconHeader(*icon) {
            Text("Already running session")
            Br()
            Small { Text(pomodoro.duration.toMomentString(descriptive = false)) }
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
