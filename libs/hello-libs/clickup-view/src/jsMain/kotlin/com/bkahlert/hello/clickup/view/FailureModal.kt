package com.bkahlert.hello.clickup.view

import androidx.compose.runtime.Composable
import com.bkahlert.hello.clickup.model.ClickUpException
import com.bkahlert.hello.semanticui.core.attributes.Variation
import com.bkahlert.hello.semanticui.core.dataAttr
import com.bkahlert.hello.semanticui.custom.errorMessage
import com.bkahlert.hello.semanticui.element.Icon
import com.bkahlert.hello.semanticui.element.IconHeader
import com.bkahlert.hello.semanticui.module.Accordion
import com.bkahlert.hello.semanticui.module.Actions
import com.bkahlert.hello.semanticui.module.ApproveButton
import com.bkahlert.hello.semanticui.module.BasicModal
import com.bkahlert.hello.semanticui.module.Content
import com.bkahlert.hello.semanticui.module.DenyButton
import com.bkahlert.hello.semanticui.module.Dropdown
import com.bkahlert.hello.semanticui.module.closable
import com.bkahlert.hello.semanticui.module.onApprove
import com.bkahlert.hello.semanticui.module.onDeny
import com.bkahlert.hello.semanticui.module.scrolling
import com.bkahlert.hello.ui.compose.data
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Pre
import org.jetbrains.compose.web.dom.Small
import org.jetbrains.compose.web.dom.Text

@Composable
public fun FailureModal(
    operation: String,
    cause: Throwable,
    onRetry: () -> Unit = { console.log("onRetry()") },
    onIgnore: () -> Unit = { console.log("onIgnore()") },
    onSignOut: () -> Unit = { console.log("onSignOut()") },
) {

    val warning = cause is ClickUpException
    val icon = if (warning) arrayOf("yellow", "warning", "circle") else arrayOf("red", "exclamation", "circle")

    BasicModal({
        +Variation.Size.Small
        onApprove = {
            if (it.dataAttr("action") == "sign-out") onSignOut()
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
                    Pre {
                        Text(cause.stackTraceToString())
                    }
                }
            }
        }
        Actions {
            ApproveButton({
                +Variation.Colored.Yellow
                +Inverted
            }) {
                Icon("redo", "alternate")
                Text("Retry")
            }
            DenyButton({
                +Variation.Colored.Yellow
                +Basic
                +Inverted
                onClick { onIgnore() }
            }) {
                Icon("remove")
                Text("Ignore")
            }
            ApproveButton({
                +Variation.Colored.Red
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
