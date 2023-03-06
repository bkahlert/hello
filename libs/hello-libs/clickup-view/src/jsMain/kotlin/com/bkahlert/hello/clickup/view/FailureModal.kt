package com.bkahlert.hello.clickup.view

import androidx.compose.runtime.Composable
import com.bkahlert.hello.clickup.model.ClickUpException
import com.bkahlert.semanticui.core.attributes.Modifier
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Colored.Red
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Colored.Yellow
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Small
import com.bkahlert.semanticui.core.attributes.raw
import com.bkahlert.semanticui.custom.data
import com.bkahlert.semanticui.custom.errorMessage
import com.bkahlert.semanticui.element.BasicButton
import com.bkahlert.semanticui.element.Button
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.IconHeader
import com.bkahlert.semanticui.element.colored
import com.bkahlert.semanticui.module.Accordion
import com.bkahlert.semanticui.module.Actions
import com.bkahlert.semanticui.module.BasicModal
import com.bkahlert.semanticui.module.Content
import com.bkahlert.semanticui.module.Dropdown
import com.bkahlert.semanticui.module.approve
import com.bkahlert.semanticui.module.dataAttr
import com.bkahlert.semanticui.module.deny
import com.bkahlert.semanticui.module.inverted
import com.bkahlert.semanticui.module.scrolling
import com.bkahlert.semanticui.module.size
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
        v.size(Small)
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
        Content({ v.scrolling() }) {
            P {
                Text(cause.errorMessage)
            }
            Accordion({ raw(Modifier.Variation.Inverted) }) {
                Dropdown("Stacktrace") {
                    Pre {
                        Text(cause.stackTraceToString())
                    }
                }
            }
        }
        Actions {
            if (onRetry != null) {
                Button({
                    v.approve().colored(Yellow).inverted()
                }) {
                    Icon("redo", "alternate")
                    Text("Retry")
                }
            }
            if (onIgnore != null) {
                BasicButton({
                    v.deny().colored(Yellow)
                    onClick { onIgnore() }
                }) {
                    Icon("remove")
                    Text("Ignore")
                }
            }
            if (onSignOut != null) {
                Button({
                    v.approve().colored(Red).inverted()
                    data("action", "sign-out")
                }) {
                    Icon("sign-out")
                    Text("Sign out")
                }
            }
        }
    }
}
