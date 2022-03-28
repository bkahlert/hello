package com.bkahlert.hello.plugins.clickup.menu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.clickup.api.rest.AccessToken
import com.semanticui.compose.element.Icon
import com.semanticui.compose.element.IconHeader
import com.semanticui.compose.module.Actions
import com.semanticui.compose.module.ApproveButton
import com.semanticui.compose.module.Content
import com.semanticui.compose.module.DenyButton
import com.semanticui.compose.module.Modal
import com.semanticui.compose.module.onApprove
import com.semanticui.compose.module.onDeny
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
    onConnect: (AccessToken) -> Unit,
    onCancel: () -> Unit,
    defaultAccessToken: AccessToken? = null,
) {
    var accessTokenInput by remember { mutableStateOf(defaultAccessToken?.token ?: "") }
    val isValid by derivedStateOf { AccessToken.REGEX.matches(accessTokenInput) }

    Modal(Unit, {
        +Size.Tiny + Basic
        onApprove = { runCatching { AccessToken(accessTokenInput) }.onSuccess(onConnect).isSuccess }
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
                        required(true)
                        pattern(AccessToken.REGEX.pattern)
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
