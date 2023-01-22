package com.bkahlert.semanticui.demo.custom

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.bkahlert.semanticui.custom.DimmingLoader
import com.bkahlert.semanticui.demo.DEMO_BASE_DELAY
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.LoremIpsumParagraph
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.Types
import com.bkahlert.semanticui.element.Button
import com.bkahlert.semanticui.element.Segment
import com.bkahlert.semanticui.element.disabled
import com.bkahlert.semanticui.module.dimmable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import kotlin.time.times

public val DimmingLoaderDemos: SemanticDemo = SemanticDemo(
    "Dimming Loader",
    Types {
        Demo("Dimming Loader") {
            val delay = remember { 2 * DEMO_BASE_DELAY }
            var active by mutableStateOf(true)
            val coroutineScope = rememberCoroutineScope()
            Segment({ v.dimmable() }) {
                LoremIpsumParagraph()
                Button({
                    if (active) s.disabled()
                    else onClick { active = true }
                }) {
                    Text("Load for $delay ...")
                }
                DimmingLoader(active = active)
                if (active) {
                    coroutineScope.launch {
                        delay(delay)
                        active = false
                    }
                }
            }
        }
        Demo("Dimming Text Loader") {
            val delay = remember { 2 * DEMO_BASE_DELAY }
            var active by mutableStateOf(true)
            val coroutineScope = rememberCoroutineScope()
            Segment({ v.dimmable() }) {
                LoremIpsumParagraph()
                Button({
                    if (active) s.disabled()
                    else onClick { active = true }
                }) {
                    Text("Load for $delay ...")
                }
                DimmingLoader(active = active) {
                    P { Text("Loading for $delay ...") }
                }
                if (active) {
                    coroutineScope.launch {
                        delay(delay)
                        active = false
                    }
                }
            }
        }
    },
)
