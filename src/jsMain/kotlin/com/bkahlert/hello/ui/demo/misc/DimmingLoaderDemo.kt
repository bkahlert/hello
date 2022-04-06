package com.bkahlert.hello.ui.demo.misc

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.bkahlert.hello.ui.DimmingLoader
import com.bkahlert.hello.ui.demo.Demo
import com.bkahlert.hello.ui.demo.Demos
import com.semanticui.compose.element.Button
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.dom.Text
import kotlin.time.Duration.Companion.seconds

@Composable
fun DimmingLoaderDemo() {
    Demos("Dimming Loader") {
        Demo("Loader") {
            var loading by remember { mutableStateOf(false) }
            val coroutineScope = rememberCoroutineScope()
            console.warn("loading $loading")
            DimmingLoader(loading)
            Button({
                +Emphasis.Primary
                onClick {
                    loading = true
                    coroutineScope.launch {
                        delay(2.5.seconds)
                        loading = false
                    }
                }
            }) {
                Text("Press to load")
            }
        }
    }
}
