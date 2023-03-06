package com.bkahlert.hello.clickup.view

import androidx.compose.runtime.Composable
import com.bkahlert.hello.clickup.model.ClickUpClient
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Colored.Yellow
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Floated
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Small
import com.bkahlert.semanticui.custom.Options
import com.bkahlert.semanticui.element.BasicButton
import com.bkahlert.semanticui.element.Divider
import com.bkahlert.semanticui.element.IconHeader
import com.bkahlert.semanticui.element.IconSubHeader
import com.bkahlert.semanticui.element.colored
import com.bkahlert.semanticui.element.floated
import com.bkahlert.semanticui.module.Actions
import com.bkahlert.semanticui.module.BasicModal
import com.bkahlert.semanticui.module.Content
import com.bkahlert.semanticui.module.deny
import com.bkahlert.semanticui.module.size
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
public fun ConfigurationModal(
    onConnect: (ClickUpClient) -> Unit,
    onCancel: () -> Unit,
    vararg configurers: Configurer<ClickUpClient>,
) {
    BasicModal({
        v.size(Small)
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
        Content {
            Div({ classes("ui", "form") }) {
                Options(*configurers) { configurer ->
                    {
                        IconSubHeader(*configurer.icon) { Text(configurer.name) }
                        Divider({ classes("fitted", "hidden", "clearing") })
                        with(configurer) { content { onConnect(it) } }
                    }
                }

                Actions {
                    BasicButton({
                        v.colored(Yellow)
                        v.deny().floated(Floated.Right)
                    }) { Text("Abort") }
                }
            }
        }
    }
}
