package com.bkahlert.hello.clickup.view

import androidx.compose.runtime.Composable
import com.bkahlert.hello.clickup.Pomodoro
import com.bkahlert.hello.clickup.view.Activity.RunningTaskActivity
import com.bkahlert.kommons.toMomentString
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Colored.Blue
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Small
import com.bkahlert.semanticui.element.BasicButton
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.IconHeader
import com.bkahlert.semanticui.element.InvertedButton
import com.bkahlert.semanticui.element.colored
import com.bkahlert.semanticui.module.Actions
import com.bkahlert.semanticui.module.BasicModal
import com.bkahlert.semanticui.module.Content
import com.bkahlert.semanticui.module.approve
import com.bkahlert.semanticui.module.closable
import com.bkahlert.semanticui.module.deny
import com.bkahlert.semanticui.module.onApprove
import com.bkahlert.semanticui.module.onDeny
import com.bkahlert.semanticui.module.size
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
        v.size(Small)
        b.onApprove = { onContinue(); true }
        b.onDeny = { onAbort();true }
        b.closable = false
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
            InvertedButton({
                v.approve()
                v.colored(Blue)
            }) {
                Icon("stop", "circle")
                Text("Continue")
            }
            BasicButton({
                v.deny()
            }) {
                Icon("remove")
                Text("Abort")
            }
        }
    }
}
