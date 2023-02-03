package com.bkahlert.semanticui.demo.elements

import com.bkahlert.semanticui.core.attributes.Modifier.Variation.LineLength.Full
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.LineLength.Long
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.LineLength.Medium
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.LineLength.Short
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.LineLength.VeryLong
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.LineLength.VeryShort
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.custom.SemanticDemo
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.Content
import com.bkahlert.semanticui.demo.custom.SemanticType
import com.bkahlert.semanticui.element.ImageHeader
import com.bkahlert.semanticui.element.Line
import com.bkahlert.semanticui.element.Paragraph
import com.bkahlert.semanticui.element.Placeholder
import com.bkahlert.semanticui.element.lineLength

public val PlaceholderDemos: SemanticDemo = SemanticDemo(
    SemanticType.Element,
    "Placeholder",
    Content {
        Demo("Line") {
            Placeholder {
                ImageHeader {
                    Line()
                    Line()
                }
                Paragraph {
                    Line({ v.lineLength(Full) })
                    Line({ v.lineLength(VeryLong) })
                    Line({ v.lineLength(Long) })
                    Line({ v.lineLength(Medium) })
                    Line({ v.lineLength(Short) })
                    Line({ v.lineLength(VeryShort) })
                }
            }
        }
    }
)
