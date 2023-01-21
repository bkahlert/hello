package com.bkahlert.hello.clickup.client.http

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.clickup.view.Configurer
import com.bkahlert.kommons.dom.ScopedStorage.Companion.scoped
import com.bkahlert.kommons.dom.Storage
import com.bkahlert.kommons.dom.clear
import com.bkahlert.semanticui.collection.Header
import com.bkahlert.semanticui.collection.Message
import com.bkahlert.semanticui.collection.info
import com.bkahlert.semanticui.collection.size
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.attributes.Modifier
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Tiny
import com.bkahlert.semanticui.core.dom.SemanticElement
import com.bkahlert.semanticui.core.dom.SemanticElementScope
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.PrimaryButton
import com.bkahlert.semanticui.element.disabled
import kotlinx.browser.localStorage
import org.jetbrains.compose.web.attributes.ATarget.Blank
import org.jetbrains.compose.web.attributes.InputType.Password
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.attributes.pattern
import org.jetbrains.compose.web.attributes.required
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement

public class ClickUpHttpClientConfigurer : Configurer<ClickUpHttpClient> {
    override val name: String = "Personal Access"
    override val icon: Array<String> = arrayOf("grey", "key")
    override val content: @Composable SemanticElementScope<SemanticElement<HTMLDivElement>>.(onComplete: (ClickUpHttpClient) -> Unit) -> Unit = { onComplete ->

        var accessTokenInput by remember { mutableStateOf("") }
        val isValid by derivedStateOf { PersonalAccessToken.REGEX.matches(accessTokenInput) }

        S("field") {
            Message({
                v.info()
                v.size(Tiny)
            }) {
                Header { Text("OAuth2 not supported yet") }
                P {
                    Text("To access your data, your ")
                    A("https://clickup.com/api", { target(Blank) }) {
                        Text("personal ClickUp API token")
                        Text(" ")
                        Icon("external", "alternate")
                    }
                    Text(" is required.")
                }
            }

            Label { Text("Access Token") }
            Input(Password) {
                name("clickup-access-token")
                required()
                pattern(PersonalAccessToken.REGEX.pattern)
                value(accessTokenInput)
                onInput { accessTokenInput = it.value }
            }
        }

        PrimaryButton({
            raw(Modifier.Variation.Inverted)
            if (!isValid) s.disabled()
            onClick {
                onComplete(
                    ClickUpHttpClient(
                        accessToken = PersonalAccessToken(accessTokenInput),
                        cacheStorage = Storage.of(localStorage).scoped("clickup").apply { clear() },
                    )
                )
            }
        }) { Text("Connect") }
    }
}
