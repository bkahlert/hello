package com.bkahlert.hello.debug.semanticui

import androidx.compose.runtime.Composable
import com.bkahlert.hello.clickup.model.fixtures.ImageFixtures.JohnDoe
import com.bkahlert.hello.clickup.model.fixtures.ImageFixtures.KommonsLogo
import com.bkahlert.hello.debug.Demo
import com.bkahlert.hello.debug.Demos
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.attributes.Variation.Negative
import com.bkahlert.semanticui.core.attributes.Variation.Size
import com.bkahlert.semanticui.core.attributes.Variation.Size.Huge
import com.bkahlert.semanticui.element.Button
import com.bkahlert.semanticui.element.Content
import com.bkahlert.semanticui.element.Description
import com.bkahlert.semanticui.element.Header
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.Image
import com.bkahlert.semanticui.element.ImageAnchor
import com.bkahlert.semanticui.element.ImageHeader
import com.bkahlert.semanticui.element.Input
import com.bkahlert.semanticui.element.Item
import com.bkahlert.semanticui.element.Line
import com.bkahlert.semanticui.element.Loader
import com.bkahlert.semanticui.element.Paragraph
import com.bkahlert.semanticui.element.Placeholder
import com.bkahlert.semanticui.element.TextLoader
import com.bkahlert.semanticui.element.action
import com.bkahlert.semanticui.element.avatar
import com.bkahlert.semanticui.element.bordered
import com.bkahlert.semanticui.element.circular
import com.bkahlert.semanticui.element.disabled
import com.bkahlert.semanticui.element.divided
import com.bkahlert.semanticui.element.error
import com.bkahlert.semanticui.element.floated
import com.bkahlert.semanticui.element.focus
import com.bkahlert.semanticui.element.horizontal
import com.bkahlert.semanticui.element.icon
import com.bkahlert.semanticui.element.labeled
import com.bkahlert.semanticui.element.loading
import com.bkahlert.semanticui.element.rounded
import com.bkahlert.semanticui.element.spaced
import com.bkahlert.semanticui.element.v
import com.bkahlert.semanticui.element.verticallyAligned
import com.bkahlert.semanticui.module.Dropdown
import com.bkahlert.semanticui.module.Menu
import com.bkahlert.semanticui.module.Text
import org.jetbrains.compose.web.attributes.InputType.Text
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Small
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import com.bkahlert.semanticui.element.List as SemanticList

@Composable
fun ElementsDemos() {
    Demos("Elements") {

        Demo("Image") {
            SemanticList {
                Item {
                    Image(KommonsLogo, "Mini") { v(Size.Mini); +spaced }
                    Image(KommonsLogo, "Tiny") { v(Size.Tiny); +spaced }
                    Image(KommonsLogo, "Small") { v(Size.Small); +spaced }
                }
                Item { Image(KommonsLogo, "Bordered") { v(Size.Tiny); +bordered } }
                Item { Image(KommonsLogo, "Rounded") { v(Size.Tiny); +rounded } }
                Item { Image(KommonsLogo, "Circular") { v(Size.Tiny); +circular } }
                Item {
                    Content {
                        Image({ +avatar }) { Img(JohnDoe.toString()) }
                        Span { Text("Avatar") }
                    }
                }
            }
        }

        Demo("Input") {
            @Composable
            fun AnyInput(placeholder: String) {
                Input(Text) { placeholder(placeholder) }
            }
            SemanticList {
                Item { Input { AnyInput("Input") } }
                Item { Input({ +focus }) { AnyInput("Input in focus state") } }
                Item { Input({ +disabled }) { AnyInput("Input in disabled state") } }
                Item { Input({ +error }) { AnyInput("Input in error state") } }
                Item {
                    Input({ +icon }) {
                        AnyInput("Input with icon")
                        Icon("circular", "search", "link")
                    }
                }
                Item {
                    Input({
                        +icon
                        +loading
                    }) {
                        AnyInput("Input with loading icon")
                        Icon("search", "link")
                    }
                }

                Item {
                    Input({ +labeled }) {
                        S("ui", "label") { Text("https://") }
                        AnyInput("example.com")
                    }
                }
                Item {
                    Input({ +Position.Right + action + Position.Left + icon }) {
                        Icon("search")
                        AnyInput("Search...")
                        Dropdown({ +Basic + Floating; classes("button") }) {
                            Text { Text("This Page") }
                            Icon("dropdown")
                            Menu {
                                Item { Text("This Organization") }
                                Item { Text("Entire Size") }
                            }
                        }
                    }
                }
            }
        }

        Demo("List") {
            SemanticList {
                Item { Text("Text") }
                Item { Text("Other Text") }
            }
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
            SemanticList {
                Item {
                    Icon("help")
                    Content {
                        Header { Text("Header") }
                        Description { Text("Description") }
                    }
                }
                Item {
                    Content({ +floated.Right }) { Text("+ right floated") }
                    Icon("help")
                    Content {
                        Header { Text("Header") }
                        Description { Text("Description") }
                    }
                }
            }
            SemanticList({ +horizontal + divided }) {
                Item {
                    Text("horizontally divided")
                }
                Item {
                    ImageAnchor(href = null) { Icon("arrow", "up") { +Huge } }
                    Content({ +verticallyAligned.Top }) { Small { Text("+ top aligned") } }
                }
            }
        }

        Demo("Placeholder") {
            Placeholder {
                ImageHeader {
                    Line()
                    Line()
                }
                Paragraph {
                    Line({ +Length.Full })
                    Line({ +Length.VeryLong })
                    Line({ +Length.Long })
                    Line({ +Length.Medium })
                    Line({ +Length.Short })
                    Line({ +Length.VeryShort })
                }
            }
        }

        Demo("Buttons") {
            SemanticList {
                Item { Button({ }) { Text("Button") } }
                Item { Button({ +Basic }) { Text("Basic Button") } }
                Item { Button({ +Negative }) { Text("Negative Button") } }
                Item { Button({ +Negative + Basic }) { Text("Negative Basic Button") } }
            }
        }

        Demo("Loader", { classes("inverted") }) {
            Placeholder({ +Inverted + Fluid }) {
                ImageHeader {
                    Line()
                    Line()
                }
                Paragraph {
                    Line()
                    Line()
                    Line()
                    Line()
                }
            }
            Loader({ +Active })
        }

        Demo("Text Loader", { classes("inverted") }) {
            Placeholder({ +Inverted + Fluid }) {
                ImageHeader {
                    Line()
                    Line()
                }
                Paragraph {
                    Line()
                    Line()
                    Line()
                    Line()
                    Line()
                }
            }
            TextLoader({ +Active }) { Text("Loading...") }
        }

    }
}
