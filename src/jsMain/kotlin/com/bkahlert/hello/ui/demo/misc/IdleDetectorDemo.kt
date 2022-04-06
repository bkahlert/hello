package com.bkahlert.hello.ui.demo.misc

import androidx.compose.runtime.Composable
import com.bkahlert.hello.ui.demo.Demo
import com.bkahlert.hello.ui.demo.Demos
import org.jetbrains.compose.web.dom.Text
import kotlin.js.Json
import kotlin.js.Promise

external class IdleDetector {
    fun requestPermission(): Promise<String>
    fun addEventListener(name: String, listener: () -> Unit)
    fun start(json: Json)
    val userState: Any?
    val screenState: Any?
}

fun testIdleDetector() {
    val idleDetector = IdleDetector()
    idleDetector.requestPermission().then {
        console.log("response: $it")
        if (it == "granted") {
            idleDetector.addEventListener("change") {
                val userState = idleDetector.userState
                val screenState = idleDetector.screenState
                console.log("Idle change: ${userState}, ${screenState}.")
            }
        }
    }
}

@Composable
fun IdleDetectoryDemo() {
    Demos("Idle Detector") {
        Demo("Idle Detector") {
            Text("// testIdleDetector")
        }
    }
}
