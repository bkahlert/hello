package com.bkahlert.semanticui.demo.custom

import com.bkahlert.semanticui.custom.IFrame
import com.bkahlert.semanticui.custom.src
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.Variations

public val IFrameDemos: SemanticDemo = SemanticDemo(
    null,
    "IFrame",
    Variations {
        Demo("Without Text") {
            IFrame { src("https://httpbin.org/delay/3") }
        }
        Demo("With Text") {
            IFrame("Loading httpbin.org/delay/3") { src("https://httpbin.org/delay/3") }
        }
    },
)
