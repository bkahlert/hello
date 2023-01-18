package com.bkahlert.hello.clickup.view

import androidx.compose.runtime.Composable
import com.bkahlert.hello.clickup.model.ClickUpClient
import com.bkahlert.hello.semanticui.core.Semantic
import com.bkahlert.hello.semanticui.core.SemanticUI
import com.bkahlert.hello.semanticui.core.attributes.Variation.Floated
import com.bkahlert.hello.semanticui.custom.Configurer
import com.bkahlert.hello.semanticui.element.Icon
import com.bkahlert.hello.semanticui.element.IconHeader
import com.bkahlert.hello.semanticui.element.IconSubHeader
import com.bkahlert.hello.semanticui.module.Actions
import com.bkahlert.hello.semanticui.module.BasicModal
import com.bkahlert.hello.semanticui.module.Content
import com.bkahlert.hello.semanticui.module.DenyButton
import com.bkahlert.hello.semanticui.module.onApprove
import com.bkahlert.hello.semanticui.module.onDeny
import com.bkahlert.hello.semanticui.module.size
import org.jetbrains.compose.web.attributes.ATarget.Blank
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

@Composable
public fun ConfigurationModal(
    onConnect: (ClickUpClient) -> Unit,
    onCancel: () -> Unit,
    vararg configurers: Configurer<ClickUpClient>,
) {
    BasicModal({
        +size.Tiny
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
            P { Text("Currently, OAuth2 is not supported yet.") }
            P { Text("To use this feature at its current state please enter your personal ClickUp API token.") }
            P {
                Text("Detailed instructions on how to create your personal access token can be found at: ")
                A("https://clickup.com/api", { target(Blank) }) {
                    Text("ClickUp 2.0 API documentation")
                    Text(" ")
                    Icon("external", "alternate")
                }
            }

            Div({ classes("ui", "form") }) {
                SemanticUI("placeholder", "segment") {
                    SemanticUI("stackable", "two", "column", "center", "aligned", "grid") {
                        SemanticUI("vertical", "divider") { Text("Or") }
                        Semantic("middle", "aligned", "row") {
                            configurers.forEach { configurer ->
                                Semantic("column") {
                                    IconSubHeader(*configurer.icon) { Text(configurer.name) }
                                    with(configurer) {
                                        Content(onComplete = { onConnect(it) })
                                    }
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
