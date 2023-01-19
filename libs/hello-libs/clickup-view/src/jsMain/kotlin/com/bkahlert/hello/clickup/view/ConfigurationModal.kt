package com.bkahlert.hello.clickup.view

import androidx.compose.runtime.Composable
import com.bkahlert.hello.clickup.model.ClickUpClient
import com.bkahlert.hello.semanticui.core.S
import com.bkahlert.hello.semanticui.core.attributes.Variation.Floated
import com.bkahlert.hello.semanticui.core.attributes.Variation.Size
import com.bkahlert.hello.semanticui.element.IconHeader
import com.bkahlert.hello.semanticui.element.IconSubHeader
import com.bkahlert.hello.semanticui.module.Actions
import com.bkahlert.hello.semanticui.module.BasicModal
import com.bkahlert.hello.semanticui.module.Content
import com.bkahlert.hello.semanticui.module.DenyButton
import com.bkahlert.hello.semanticui.module.onApprove
import com.bkahlert.hello.semanticui.module.onDeny
import com.bkahlert.hello.semanticui.module.v
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
public fun ConfigurationModal(
    onConnect: (ClickUpClient) -> Unit,
    onCancel: () -> Unit,
    vararg configurers: Configurer<ClickUpClient>,
) {
    BasicModal({
        v(Size.Tiny)
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
