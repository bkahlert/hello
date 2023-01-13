package com.bkahlert.hello.clickup.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.clickup.client.http.PersonalAccessToken
import com.bkahlert.hello.semanticui.element.Icon
import com.bkahlert.hello.semanticui.element.IconHeader
import com.bkahlert.hello.semanticui.module.Actions
import com.bkahlert.hello.semanticui.module.ApproveButton
import com.bkahlert.hello.semanticui.module.BasicModal
import com.bkahlert.hello.semanticui.module.Content
import com.bkahlert.hello.semanticui.module.DenyButton
import com.bkahlert.hello.semanticui.module.onApprove
import com.bkahlert.hello.semanticui.module.onDeny
import com.bkahlert.hello.semanticui.module.size
import org.jetbrains.compose.web.attributes.ATarget.Blank
import org.jetbrains.compose.web.attributes.InputType.Password
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.attributes.pattern
import org.jetbrains.compose.web.attributes.required
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

@Composable
fun ConfigurationModal(
    onConnect: (PersonalAccessToken?) -> Unit,
    onCancel: () -> Unit,
    defaultAccessToken: PersonalAccessToken? = null,
) {
    var accessTokenInput by remember { mutableStateOf(defaultAccessToken?.token ?: "") }
    val isValid by derivedStateOf { PersonalAccessToken.REGEX.matches(accessTokenInput) }

    BasicModal({
        +size.Tiny
        onApprove = { runCatching { PersonalAccessToken(accessTokenInput) }.onSuccess(onConnect).isSuccess }
        onDeny = { onCancel(); false }
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
                Div({ classes("field") }) {
                    Label { Text("Access Token") }
                    Input(Password) {
                        name("clickup-access-token")
                        required()
                        pattern(PersonalAccessToken.REGEX.pattern)
                        value(accessTokenInput)
                        onInput { accessTokenInput = it.value }
                    }
                }
            }
        }
        Actions {
            ApproveButton({
                +Emphasis.Primary + Inverted
                if (!isValid) +Disabled
            }) { Text("Connect") }
            DenyButton({ +Emphasis.Secondary + Inverted }) { Text("Abort") }
        }
    }
}
