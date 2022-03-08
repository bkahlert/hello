package com.bkahlert.hello.integration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.bkahlert.hello.center
import com.bkahlert.hello.clickup.ClickUpApiClient
import com.bkahlert.hello.clickup.User
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.textAlign
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLHeadingElement


@Composable
fun ClickUp(
    user: User? = null,
    attrs: AttrBuilderContext<HTMLHeadingElement>? = null,
) {
    Style(OptionsStyleSheet)
    val coroutineScope = rememberCoroutineScope()
    Div({
        style {
            center()
        }
    }) {
        H1({
            classes(OptionsStyleSheet.header)
            attrs?.invoke(this)
        }) {
            A("#", {
                onClick {
                    coroutineScope.launch {
                        ClickUpApiClient.login()
                    }
                    it.preventDefault()
                }
            }) {
                user?.also { Text("Logged in as ${it.username}") }
                    ?: Text("Login")
            }
        }
    }
}

object OptionsStyleSheet : StyleSheet() {

    val header by style {
        textAlign("center")
        fontSize(1.em)
    }
}
