package com.bkahlert.semanticui.demo.elements

import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Floated.Right
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Huge
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.VerticallyAligned.Top
import com.bkahlert.semanticui.core.attributes.raw
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.custom.ComponentType
import com.bkahlert.semanticui.demo.custom.SemanticDemo
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.Content
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.Variations
import com.bkahlert.semanticui.element.Content
import com.bkahlert.semanticui.element.Description
import com.bkahlert.semanticui.element.Header
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.ImageAnchor
import com.bkahlert.semanticui.element.Item
import com.bkahlert.semanticui.element.divided
import com.bkahlert.semanticui.element.floated
import com.bkahlert.semanticui.element.horizontal
import com.bkahlert.semanticui.element.verticallyAligned
import org.jetbrains.compose.web.dom.Small
import org.jetbrains.compose.web.dom.Text
import com.bkahlert.semanticui.element.List as SemanticList

public val ListDemos: SemanticDemo = SemanticDemo(
    ComponentType.Element,
    "List",
    Content {
        Demo("Item") {
            SemanticList {
                Item { Text("Text") }
                Item { Text("Other Text") }
            }
        }
        Demo("Header/Description") {
            SemanticList {
                Item {
                    Header { Text("Header") }
                    Description { Text("Description") }
                }
                Item {
                    Header { Text("Header") }
                    Description { Text("Description") }
                }
            }
        }
    },
    Variations {
        Demo("Floated") {
            SemanticList {
                Item {
                    Icon("help")
                    Content {
                        Header { Text("Header") }
                        Description { Text("Description") }
                    }
                }
                Item {
                    Content({ v.floated(Right) }) { Text("+ right floated") }
                    Icon("help")
                    Content {
                        Header { Text("Header") }
                        Description { Text("Description") }
                    }
                }
            }
        }
        Demo("Divided") {
            SemanticList({ v.horizontal().divided() }) {
                Item {
                    Text("horizontally divided")
                }
                Item {
                    ImageAnchor(href = null) { Icon("arrow", "up") { raw(Huge) } }
                    Content({ v.verticallyAligned(Top) }) { Small { Text("+ top aligned") } }
                }
            }
        }
    }
)
