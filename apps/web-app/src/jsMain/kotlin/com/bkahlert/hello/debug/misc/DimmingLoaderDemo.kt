package com.bkahlert.hello.debug.misc

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.bkahlert.hello.debug.Demo
import com.bkahlert.hello.debug.Demos
import com.bkahlert.hello.semanticui.element.Button
import com.bkahlert.hello.semanticui.custom.DimmingLoader
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
