package com.bkahlert.hello.clickup.view

import androidx.compose.runtime.Composable
import com.bkahlert.hello.clickup.model.ClickUpException
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.custom.data
import com.bkahlert.semanticui.custom.errorMessage
import com.bkahlert.semanticui.element.BasicButton
import com.bkahlert.semanticui.element.Button
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.IconHeader
import com.bkahlert.semanticui.module.Accordion
import com.bkahlert.semanticui.module.BasicModal
import com.bkahlert.semanticui.module.Dropdown
import com.bkahlert.semanticui.module.dataAttr
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Pre
import org.jetbrains.compose.web.dom.Small
import org.jetbrains.compose.web.dom.Text

@Composable
public fun FailureModal(
    operation: String,
    cause: Throwable,
    onRetry: (() -> Unit)? = null,
    onIgnore: (() -> Unit)? = null,
    onSignOut: (() -> Unit)? = null,
) {

    val warning = cause is ClickUpException
    val icon = if (warning) arrayOf("yellow", "warning", "circle") else arrayOf("red", "exclamation", "circle")

    BasicModal({
        +"small"
        settings {
            onApprove = {
                if (it.dataAttr("action") == "sign-out") {
                    if (onSignOut != null) onSignOut()
                } else {
                    if (onRetry != null) onRetry()
                }
                true
            }
            onDeny = {
                if (onIgnore != null) {
                    onIgnore()
                }
                true
            }
            closable = false
        }
    }) {
        IconHeader(*icon) {
            Text("Oops, an error occurred")
            Br()
            Small { Text("while $operation") }
        }
        S("content scrolling") {
            P {
                Text(cause.errorMessage)
            }
            Accordion({ +"inverted" }) {
                Dropdown("Stacktrace") {
                    Pre {
                        Text(cause.stackTraceToString())
                    }
                }
            }
        }
        S("actions") {
            if (onRetry != null) {
                Button({
                    +"approve"
                    +"yellow"
                    +"inverted"
                }) {
                    Icon("redo", "alternate")
                    Text("Retry")
                }
            }
            if (onIgnore != null) {
                BasicButton({
                    +"deny"
                    +"yellow"
                    onClick { onIgnore() }
                }) {
                    Icon("remove")
                    Text("Ignore")
                }
            }
            if (onSignOut != null) {
                Button({
                    +"approve"
                    +"red"
                    +"inverted"
                    data("action", "sign-out")
                }) {
                    Icon("sign-out")
                    Text("Sign out")
                }
            }
        }
    }
}
