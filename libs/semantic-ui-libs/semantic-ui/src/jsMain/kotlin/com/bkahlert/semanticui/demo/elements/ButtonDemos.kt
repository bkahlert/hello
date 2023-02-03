package com.bkahlert.semanticui.demo.elements

import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.custom.SemanticDemo
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.Types
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.Variations
import com.bkahlert.semanticui.demo.custom.SemanticType
import com.bkahlert.semanticui.element.BasicButton
import com.bkahlert.semanticui.element.Button
import com.bkahlert.semanticui.element.negative
import org.jetbrains.compose.web.dom.Text

public val ButtonDemos: SemanticDemo = SemanticDemo(
    SemanticType.Element,
    "Button",
    Types {
        Demo("Button") {
            Button({ }) { Text("Button") }
        }
        Demo("Basic Button") {
            BasicButton({ }) { Text("Basic Button") }
        }
    },
    Variations {
        Demo("Negative Button") {
            Button({ v.negative() }) { Text("Negative Button") }
        }
        Demo("Negative Basic Button") {
            BasicButton({ v.negative() }) { Text("Negative Basic Button") }
        }
    }
)
