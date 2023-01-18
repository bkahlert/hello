package com.bkahlert.hello.clickup.client.http

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.semanticui.core.Semantic
import com.bkahlert.hello.semanticui.core.dom.SemanticElement
import com.bkahlert.hello.semanticui.core.dom.SemanticElementScope
import com.bkahlert.hello.semanticui.custom.Configurer
import com.bkahlert.hello.semanticui.element.Button
import com.bkahlert.kommons.dom.ScopedStorage.Companion.scoped
import com.bkahlert.kommons.dom.Storage
import com.bkahlert.kommons.dom.clear
import kotlinx.browser.localStorage
import org.jetbrains.compose.web.attributes.InputType.Password
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.attributes.pattern
import org.jetbrains.compose.web.attributes.required
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement

public class ClickUpHttpClientConfigurer : Configurer<ClickUpHttpClient> {
    override val name: String = "Personal Access"
    override val icon: Array<String> = arrayOf("grey", "key")

    @Composable override fun SemanticElementScope<SemanticElement<HTMLDivElement>>.Content(
        onComplete: (ClickUpHttpClient) -> Unit,
    ) {
        var accessTokenInput by remember { mutableStateOf("") }
        val isValid by derivedStateOf { PersonalAccessToken.REGEX.matches(accessTokenInput) }

        Semantic("field") {
            Label { Text("Access Token") }
            Input(Password) {
                name("clickup-access-token")
                required()
                pattern(PersonalAccessToken.REGEX.pattern)
                value(accessTokenInput)
                onInput { accessTokenInput = it.value }
            }
        }

        Button({
            +Emphasis.Primary + Inverted
            if (!isValid) +Disabled
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
