package com.bkahlert.hello.clickup.view

import androidx.compose.runtime.Composable
import com.bkahlert.hello.clickup.model.ClickUpClient
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.custom.Options
import com.bkahlert.semanticui.element.BasicButton
import com.bkahlert.semanticui.element.IconHeader
import com.bkahlert.semanticui.element.IconSubHeader
import com.bkahlert.semanticui.module.BasicModal
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
public fun ConfigurationModal(
    onConnect: (ClickUpClient) -> Unit,
    onCancel: () -> Unit,
    vararg configurers: Configurer<ClickUpClient>,
) {
    BasicModal({
        +"small"
        settings {
            onApprove = {
                console.log("approved", it)
                true
            }
            onDeny = {
                onCancel()
                false
            }
        }
    }) {
        IconHeader("sign-in") { Text("Connect to ClickUp") }
        S("content") {
            Div({ classes("ui", "form") }) {
                Options(*configurers) { configurer ->
                    {
                        IconSubHeader(*configurer.icon) { Text(configurer.name) }
                        S("ui", "fitted", "hidden", "clearing", "divider")
                        with(configurer) { content { onConnect(it) } }
                    }
                }

                S("actions") {
                    BasicButton({
                        +"yellow"
                        +"deny"
                        +"right"
                        +"floated"
                    }) { Text("Abort") }
                }
            }
        }
    }
}
