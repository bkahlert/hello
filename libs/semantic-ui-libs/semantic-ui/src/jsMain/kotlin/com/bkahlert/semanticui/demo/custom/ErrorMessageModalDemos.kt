package com.bkahlert.semanticui.demo.custom

import androidx.compose.runtime.rememberCoroutineScope
import com.bkahlert.kommons.js.groupCollapsed
import com.bkahlert.kommons.js.groupEnd
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Colored.Red
import com.bkahlert.semanticui.custom.ErrorMessageModal
import com.bkahlert.semanticui.custom.ErrorMessageModalCoroutineExceptionHandler
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.Types
import com.bkahlert.semanticui.element.Button
import com.bkahlert.semanticui.element.colored
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.dom.Text

public val ErrorMessageModalDemos: SemanticDemo = SemanticDemo(
    null,
    "Error Message Modal",
    Types {
        Demo("Error Message Modal") {
            Button({
                v.colored(Red)
                onClick { ErrorMessageModal("Something went wrong", exception) }
            }) {
                Text("Trigger modal")
            }
        }

        Demo("Error Message Modal Coroutine Exception Handler") {
            val coroutineScope = rememberCoroutineScope {
                CoroutineExceptionHandler { context, exception ->
                    console.groupCollapsed("Error Message Modal Coroutine Exception Handler")
                    console.info(
                        "%c ↓ ↓ ↓ THE ERROR BELOW IS JUST A TEST ↓ ↓ ↓ ",
                        "border: 1px solid cyan; color: cyan; display: inline-block; padding: 0.1em",
                    )
                    ErrorMessageModalCoroutineExceptionHandler.handleException(context, exception)
                    console.info(
                        "%c ↑ ↑ ↑ THE ERROR ABOVE IS JUST A TEST ↑ ↑ ↑ ",
                        "border: 1px solid cyan; color: cyan; display: inline-block; padding: 0.1em",
                    )
                    console.groupEnd()
                }
            }

            Button({
                v.colored(Red)
                onClick {
                    coroutineScope.launch(CoroutineName("Coroutine with error")) {
                        error("Something went wrong")
                    }
                }
            }) {
                Text("Trigger modal")
            }
        }
    },
)
