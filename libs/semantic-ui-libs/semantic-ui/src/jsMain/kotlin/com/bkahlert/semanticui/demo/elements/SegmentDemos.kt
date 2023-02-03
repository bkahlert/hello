package com.bkahlert.semanticui.demo.elements

import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Attached
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Attached.Bottom
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Colored
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Emphasis
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Floated
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.TextAlignment
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.TextAlignment.Center
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.Demos
import com.bkahlert.semanticui.demo.LoremIpsumParagraph
import com.bkahlert.semanticui.demo.custom.SemanticDemo
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.Groups
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.States
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.Types
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.Variations
import com.bkahlert.semanticui.demo.custom.SemanticType
import com.bkahlert.semanticui.element.Button
import com.bkahlert.semanticui.element.PiledSegment
import com.bkahlert.semanticui.element.PiledSegments
import com.bkahlert.semanticui.element.PlaceholderSegment
import com.bkahlert.semanticui.element.RaisedSegment
import com.bkahlert.semanticui.element.RaisedSegments
import com.bkahlert.semanticui.element.Segment
import com.bkahlert.semanticui.element.Segments
import com.bkahlert.semanticui.element.StackedSegment
import com.bkahlert.semanticui.element.StackedSegments
import com.bkahlert.semanticui.element.VerticalSegment
import com.bkahlert.semanticui.element.aligned
import com.bkahlert.semanticui.element.attached
import com.bkahlert.semanticui.element.circular
import com.bkahlert.semanticui.element.clearing
import com.bkahlert.semanticui.element.colored
import com.bkahlert.semanticui.element.compact
import com.bkahlert.semanticui.element.disabled
import com.bkahlert.semanticui.element.emphasis
import com.bkahlert.semanticui.element.floated
import com.bkahlert.semanticui.element.horizontal
import com.bkahlert.semanticui.element.inverted
import com.bkahlert.semanticui.element.loading
import com.bkahlert.semanticui.element.padded
import com.bkahlert.semanticui.element.veryPadded
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

public val SegmentDemos: SemanticDemo = SemanticDemo(
    SemanticType.Element,
    "Segment",
    Types {
        Demo("Segment") {
            Segment { LoremIpsumParagraph() }
        }
        Demo("Placeholder") {
            PlaceholderSegment { LoremIpsumParagraph() }
        }
        Demo("Raised") {
            RaisedSegment { LoremIpsumParagraph() }
        }
        Demo("Stacked") {
            StackedSegment { LoremIpsumParagraph() }
        }
        Demo("Piled") {
            PiledSegment { LoremIpsumParagraph() }
        }
        Demo("Vertical") {
            VerticalSegment { LoremIpsumParagraph() }
            VerticalSegment { LoremIpsumParagraph() }
        }
    },
    Groups {
        Demo("Segments") {
            Segments {
                Segment { LoremIpsumParagraph() }
                Segment { LoremIpsumParagraph() }
            }
        }
        Demo("Nested Segments") {
            Segments {
                Segment {
                    P { Text("Top") }
                    Segments {
                        Segment { P { Text("Nested Top") } }
                        Segment { P { Text("Nested Bottom") } }
                    }
                }
                Segments({ v.horizontal() }) {
                    Segment { P { Text("Top (Horizontal)") } }
                    Segment { P { Text("Bottom (Horizontal)") } }
                }
            }
        }
        Demo("Horizontal") {
            Segments({ v.horizontal() }) {
                Segment { LoremIpsumParagraph() }
                Segment { LoremIpsumParagraph() }
            }
        }
        Demo("Raised") {
            RaisedSegments {
                Segment { LoremIpsumParagraph() }
                Segment { LoremIpsumParagraph() }
            }
        }
        Demo("Stacked") {
            StackedSegments {
                Segment { LoremIpsumParagraph() }
                Segment { LoremIpsumParagraph() }
            }
        }
        Demo("Piled") {
            PiledSegments {
                Segment { LoremIpsumParagraph() }
                Segment { LoremIpsumParagraph() }
            }
        }
    },
    States {
        Demo("Disabled") {
            Segment({ s.disabled() }) { LoremIpsumParagraph() }
        }
        Demo("Loading") {
            Segment({ s.loading() }) { LoremIpsumParagraph() }
        }
    },
    Variations {
        Demo("Inverted") {
            Segment({ v.inverted() }) { LoremIpsumParagraph() }
        }
        Demo("Attached") {
            Segment({ v.attached(Attached.Top) }) { P { Text("Top Attached") } }
            Segment({ v.attached() }) { P { Text("Attached") } }
            Segment({ v.attached(Bottom) }) { P { Text("Bottom Attached") } }
        }
        Demos("Padded") {
            Demo("Padded") {
                Segment({ v.padded() }) { LoremIpsumParagraph() }
            }
            Demo("Very Padded") {
                Segment({ v.veryPadded() }) { LoremIpsumParagraph() }
            }
        }
        Demo("Compact") {
            Segment({ v.compact() }) { LoremIpsumParagraph() }
        }
        Demo("Colored") {
            Colored.forEach {
                Segment({ v.colored(it) }) { P { Text("$it") } }
            }
        }
        Demo("Emphasis") {
            Emphasis.forEach {
                Segment({ v.emphasis(it) }) { P { Text("$it") } }
            }
        }
        Demo("Circular") {
            Segment({ v.circular() }) { LoremIpsumParagraph() }
        }
        Demo("Clearing") {
            Segment({ v.clearing() }) {
                Button({ v.floated(Floated.Right) }) { Text("Floated") }
            }
        }
        Demo("Floated", { v.clearing() }) {
            Segment({ v.floated(Floated.Left) }) { P { Text("Floated Left") } }
            Segment({ v.floated(Floated.Right) }) { P { Text("Floated Right") } }
        }
        Demo("Text Alignment") {
            Segment({ v.aligned(TextAlignment.Right) }) { P { Text("Right") } }
            Segment({ v.aligned(TextAlignment.Left) }) { P { Text("Left") } }
            Segment({ v.aligned(Center) }) { P { Text("Center") } }
        }
    },
)
