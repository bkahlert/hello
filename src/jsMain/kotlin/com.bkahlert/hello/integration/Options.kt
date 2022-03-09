package com.bkahlert.hello.integration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.bkahlert.Brand
import com.bkahlert.hello.ProfileState
import com.bkahlert.hello.ProfileState.Data
import com.bkahlert.hello.ProfileState.Error
import com.bkahlert.hello.ProfileState.Loading
import com.bkahlert.hello.center
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.textAlign
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.B
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Small
import org.jetbrains.compose.web.dom.Text

@Composable
fun ClickUp(
    profile: ProfileState?,
    onConfig: () -> Unit = {},
) {
    Style(OptionsStyleSheet)
    val coroutineScope = rememberCoroutineScope()
    Div({
        style {
            center()
        }
    }) {
        when (profile) {
            null -> B({
                classes(OptionsStyleSheet.header)
            }) {
                Configure(onConfig = onConfig)
            }

            Loading -> B({
                classes(OptionsStyleSheet.header)
            }) {
                Text("Loading")
            }

            is Data -> B { Text("Hi, ${profile.user.username}") }
            is Error -> B({
                classes(OptionsStyleSheet.header)
                style {
                    color(Brand.colors.red)
                }
            }) {
                Text(profile.message)
                Br()
                Small { Configure("Try again...", onConfig = onConfig) }
            }

        }
    }
}

@Composable
fun Configure(text: String = "Configure...", onConfig: () -> Unit) {
    A("#", {
        onClick {
            onConfig()
            it.preventDefault()
        }
    }) {
        Text(text)
    }
}

object OptionsStyleSheet : StyleSheet() {

    val header by style {
        textAlign("center")
        fontSize(1.em)
    }
}
