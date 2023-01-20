package com.bkahlert.hello.clickup.view

import androidx.compose.runtime.Composable
import com.bkahlert.hello.clickup.model.ClickUpException
import com.bkahlert.semanticui.core.attributes.Variation.Colored.Red
import com.bkahlert.semanticui.core.attributes.Variation.Colored.Yellow
import com.bkahlert.semanticui.core.attributes.Variation.Size
import com.bkahlert.semanticui.core.dataAttr
import com.bkahlert.semanticui.custom.data
import com.bkahlert.semanticui.custom.errorMessage
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.IconHeader
import com.bkahlert.semanticui.module.Accordion
import com.bkahlert.semanticui.module.Actions
import com.bkahlert.semanticui.module.ApproveButton
import com.bkahlert.semanticui.module.BasicModal
import com.bkahlert.semanticui.module.Content
import com.bkahlert.semanticui.module.DenyButton
import com.bkahlert.semanticui.module.Dropdown
import com.bkahlert.semanticui.module.closable
import com.bkahlert.semanticui.module.onApprove
import com.bkahlert.semanticui.module.onDeny
import com.bkahlert.semanticui.module.scrolling
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
        +Size.Small
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
                +Yellow
                +Inverted
            }) {
                Icon("redo", "alternate")
                Text("Retry")
            }
            DenyButton({
                +Yellow
                +Basic
                +Inverted
                onClick { onIgnore() }
            }) {
                Icon("remove")
                Text("Ignore")
            }
            ApproveButton({
                +Red
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
