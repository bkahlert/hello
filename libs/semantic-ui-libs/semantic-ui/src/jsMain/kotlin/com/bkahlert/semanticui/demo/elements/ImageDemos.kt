package com.bkahlert.semanticui.demo.elements

import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.Demos
import com.bkahlert.semanticui.demo.SemanticUiImageFixtures.JohnDoeWithBackground
import com.bkahlert.semanticui.demo.custom.ComponentType
import com.bkahlert.semanticui.demo.custom.SemanticDemo
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.Types
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.Variations
import com.bkahlert.semanticui.element.Image
import com.bkahlert.semanticui.element.avatar
import com.bkahlert.semanticui.element.bordered
import com.bkahlert.semanticui.element.circular
import com.bkahlert.semanticui.element.rounded
import com.bkahlert.semanticui.element.size
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

public val ImageDemos: SemanticDemo = SemanticDemo(
    ComponentType.Element,
    "Image",
    Types {
        Demo("Image") { Image(JohnDoeWithBackground) }
    },
    Variations {
        Demos("Size") {
            Size.take(4).forEach { size ->
                Demo("$size") { Image(JohnDoeWithBackground, "$size") { v.size(size) } }
            }
        }
        Demo("Bordered") { Image(JohnDoeWithBackground, "Bordered") { v.bordered() } }
        Demo("Rounded") { Image(JohnDoeWithBackground, "Rounded") { v.rounded() } }
        Demo("Circular") { Image(JohnDoeWithBackground, "Circular") { v.circular() } }
        Demo("Avatar") {
            Image({ v.avatar() }) { Img(JohnDoeWithBackground.toString()) }
            Span { Text("Avatar") }
        }
    },
)
