package com.bkahlert.hello.integration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.bkahlert.Brand
import com.bkahlert.hello.ProfileState
import com.bkahlert.hello.ProfileState.Disconnected
import com.bkahlert.hello.ProfileState.Failed
import com.bkahlert.hello.ProfileState.Loading
import com.bkahlert.hello.ProfileState.Ready
import com.bkahlert.hello.center
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.textAlign
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.B
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Option
import org.jetbrains.compose.web.dom.Select
import org.jetbrains.compose.web.dom.Small
import org.jetbrains.compose.web.dom.Text

@Composable
fun ClickUp(
    profileState: ProfileState,
    onConfig: () -> Unit = {},
) {
    Style(OptionsStyleSheet)
    val coroutineScope = rememberCoroutineScope()
    Div({
        style {
            center()
        }
    }) {
        when (profileState) {
            Disconnected -> B({
                classes(OptionsStyleSheet.header)
            }) {
                Connect(onConfig = onConfig)
            }

            Loading -> B({
                classes(OptionsStyleSheet.header)
            }) {
                Text("Loading")
            }

            is Ready -> Profile(profileState)
            is Failed -> B({
                classes(OptionsStyleSheet.header)
                style {
                    color(Brand.colors.red)
                }
            }) {
                Text(profileState.message)
                Br()
                Small { Connect("Try again...", onConfig = onConfig) }
            }

        }
    }
}

@Composable
fun Connect(text: String = "Connect...", onConfig: () -> Unit) {
    A("#", {
        onClick {
            onConfig()
            it.preventDefault()
        }
    }) {
        Text(text)
    }
}

@Composable
fun Profile(profileState: Ready) {
    B { Text("Hi, ${profileState.user.username}") }
    Select {
        profileState.teams.forEach {
            Option(it.id.toString(), {
                style { backgroundColor(it.color) }
            }) {
                Text(it.name)
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
