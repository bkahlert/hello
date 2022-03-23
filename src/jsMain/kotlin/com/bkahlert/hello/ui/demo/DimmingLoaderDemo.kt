package com.bkahlert.hello.ui.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.bkahlert.hello.ui.DimmingLoader
import com.semanticui.compose.element.Button
import org.jetbrains.compose.web.dom.Text

@Composable
fun DimmingLoaderDemo() {
    Demos("Dimming Loader") {
        Demo("Loader") {
            var loading by mutableStateOf(false)
            DimmingLoader({ loading })
            Button({
                +Emphasis.Primary
                onClick { loading = true }
            }) {
                Text("Press to load")
            }
        }
    }
}
