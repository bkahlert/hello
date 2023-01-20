package com.bkahlert.hello.clickup.view

import androidx.compose.runtime.Composable
import com.bkahlert.hello.clickup.model.ClickUpClient
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.attributes.Variation.Floated
import com.bkahlert.semanticui.core.attributes.Variation.Size.Small
import com.bkahlert.semanticui.element.IconHeader
import com.bkahlert.semanticui.element.IconSubHeader
import com.bkahlert.semanticui.module.Actions
import com.bkahlert.semanticui.module.BasicModal
import com.bkahlert.semanticui.module.Content
import com.bkahlert.semanticui.module.DenyButton
import com.bkahlert.semanticui.module.onApprove
import com.bkahlert.semanticui.module.onDeny
import com.bkahlert.semanticui.module.v
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
public fun ConfigurationModal(
    onConnect: (ClickUpClient) -> Unit,
    onCancel: () -> Unit,
    vararg configurers: Configurer<ClickUpClient>,
) {
    BasicModal({
        v(Small)
        onApprove = {
            console.log("approved", it)
            true
        }
        onDeny = {
            onCancel()
            false
        }
    }) {
        IconHeader("sign-in") { Text("Connect to ClickUp") }
        Content {
            Div({ classes("ui", "form") }) {
                S("ui", "placeholder", "segment") {
                    S("ui", "stackable", "two", "column", "center", "aligned", "grid") {
                        S("ui", "vertical", "divider") { Text("Or") }
                        S("middle", "aligned", "row") {
                            configurers.forEach { configurer ->
                                S("column") {
                                    IconSubHeader(*configurer.icon) { Text(configurer.name) }
                                    configurer.content.invoke(this) { onConnect(it) }
                                }
                            }
                        }
                    }
                }

                Actions {
                    DenyButton({
                        +Floated.Right
                        +Emphasis.Secondary + Inverted
                    }) { Text("Abort") }
                }
            }
        }
    }
}
