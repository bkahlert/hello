package com.bkahlert.hello.plugins.clickup.menu

import androidx.compose.runtime.Composable
import com.bkahlert.hello.ui.errorMessage
import com.bkahlert.kommons.compose.data
import com.clickup.api.rest.ClickUpException
import com.semanticui.compose.element.Icon
import com.semanticui.compose.element.IconHeader
import com.semanticui.compose.module.Accordion
import com.semanticui.compose.module.Actions
import com.semanticui.compose.module.ApproveButton
import com.semanticui.compose.module.BasicModal
import com.semanticui.compose.module.Content
import com.semanticui.compose.module.DenyButton
import com.semanticui.compose.module.Dropdown
import com.semanticui.compose.module.closable
import com.semanticui.compose.module.onApprove
import com.semanticui.compose.module.onDeny
import com.semanticui.compose.module.scrolling
import com.semanticui.compose.module.size
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Pre
import org.jetbrains.compose.web.dom.Small
import org.jetbrains.compose.web.dom.Text

@Composable
fun FailureModal(
    operation: String,
    cause: Throwable,
    onRetry: () -> Unit = { console.log("onRetry()") },
    onIgnore: () -> Unit = { console.log("onIgnore()") },
    onSignOut: () -> Unit = { console.log("onSignOut()") },
) {

    val warning = cause is ClickUpException
    val icon = if (warning) arrayOf("yellow", "warning", "circle") else arrayOf("red", "exclamation", "circle")

    BasicModal({
        +size.Small
        onApprove = {
            if (it.attr("data-action") == "sign-out") onSignOut()
            else onRetry()
            true
        }
        onDeny = { onIgnore();true }
        closable = false
    }) {
        IconHeader(*icon) {
            Text("Oops, an error occurred")
            Br()
            Small { Text("while $operation") }
        }
        Content({ +scrolling }) {
            P {
                Text(cause.errorMessage)
            }
            Accordion(cause, { +Inverted }) {
                Dropdown("Stacktrace") {
                    Pre(it) {
                        Text(cause.stackTraceToString())
                    }
                }
            }
        }
        Actions {
            ApproveButton({
                +Colored.Yellow
                +Inverted
            }) {
                Icon("redo", "alternate")
                Text("Retry")
            }
            DenyButton({
                +Colored.Yellow
                +Basic
                +Inverted
                onClick { onIgnore() }
            }) {
                Icon("remove")
                Text("Ignore")
            }
            ApproveButton({
                +Colored.Red
                +Basic
                +Inverted
                data("action", "sign-out")
            }) {
                Icon("sign-out")
                Text("Sign out")
            }
        }
    }
}
