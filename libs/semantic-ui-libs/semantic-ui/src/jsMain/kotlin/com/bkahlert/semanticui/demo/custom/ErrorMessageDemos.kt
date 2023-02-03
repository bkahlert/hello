package com.bkahlert.semanticui.demo.custom

import com.bkahlert.semanticui.custom.ErrorMessage
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.Types
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.Variations
import org.jetbrains.compose.web.css.textAlign

public val ErrorMessageDemos: SemanticDemo = SemanticDemo(
    null,
    "Error Message",
    Types {
        Demo("Error Message") {
            ErrorMessage(exception)
        }
    },
    Variations {
        Demo("Empty") {
            ErrorMessage()
        }
        Demo("Centered", {
            style { textAlign("center") }
        }) {
            ErrorMessage(exception)
        }
    },
)

private val exception: Throwable
    get() = checkNotNull(runCatching { error("This is just a test") }.exceptionOrNull())
