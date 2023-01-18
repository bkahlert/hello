package com.bkahlert.hello.debug.semanticui

import com.bkahlert.hello.semanticui.core.Semantic
import androidx.compose.runtime.Composable
import com.bkahlert.hello.debug.Demo
import com.bkahlert.hello.debug.Demos
import com.bkahlert.hello.dom.ImageFixtures
import com.bkahlert.hello.dom.ImageFixtures.KommonsLogo
import com.bkahlert.hello.semanticui.core.attributes.Variation.Negative
import com.bkahlert.hello.semanticui.element.Button
import com.bkahlert.hello.semanticui.element.Content
import com.bkahlert.hello.semanticui.element.Description
import com.bkahlert.hello.semanticui.element.Header
import com.bkahlert.hello.semanticui.element.Icon
import com.bkahlert.hello.semanticui.element.Image
import com.bkahlert.hello.semanticui.element.ImageAnchor
import com.bkahlert.hello.semanticui.element.ImageHeader
import com.bkahlert.hello.semanticui.element.Input
import com.bkahlert.hello.semanticui.element.Item
import com.bkahlert.hello.semanticui.element.Line
import com.bkahlert.hello.semanticui.element.Loader
import com.bkahlert.hello.semanticui.element.Paragraph
import com.bkahlert.hello.semanticui.element.Placeholder
import com.bkahlert.hello.semanticui.element.TextLoader
import com.bkahlert.hello.semanticui.element.action
import com.bkahlert.hello.semanticui.element.avatar
import com.bkahlert.hello.semanticui.element.bordered
import com.bkahlert.hello.semanticui.element.circular
import com.bkahlert.hello.semanticui.element.disabled
import com.bkahlert.hello.semanticui.element.divided
import com.bkahlert.hello.semanticui.element.error
import com.bkahlert.hello.semanticui.element.floated
import com.bkahlert.hello.semanticui.element.focus
import com.bkahlert.hello.semanticui.element.horizontal
import com.bkahlert.hello.semanticui.element.icon
import com.bkahlert.hello.semanticui.element.labeled
import com.bkahlert.hello.semanticui.element.loading
import com.bkahlert.hello.semanticui.element.rounded
import com.bkahlert.hello.semanticui.element.size
import com.bkahlert.hello.semanticui.element.spaced
import com.bkahlert.hello.semanticui.element.verticallyAligned
import com.bkahlert.hello.semanticui.module.Dropdown
import com.bkahlert.hello.semanticui.module.Menu
import com.bkahlert.hello.semanticui.module.Text
import org.jetbrains.compose.web.attributes.InputType.Text
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Small
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import com.bkahlert.hello.semanticui.element.List as SemanticList

@Composable
fun ElementsDemos() {
    Demos("Elements") {

        Demo("Image") {
            SemanticList {
                Item {
                    Image(KommonsLogo, "Mini") { +size.Mini + spaced }
                    Image(KommonsLogo, "Tiny") { +size.Tiny + spaced }
                    Image(KommonsLogo, "Small") { +size.Small + spaced }
                }
                Item { Image(KommonsLogo, "Bordered") { +size.Tiny + bordered } }
                Item { Image(KommonsLogo, "Rounded") { +size.Tiny + rounded } }
                Item { Image(KommonsLogo, "Circular") { +size.Tiny + circular } }
                Item {
                    Content {
                        Image({ +avatar }) { Img(ImageFixtures.Avatar) }
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
                        Semantic("ui", "label") { Text("https://") }
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
                    ImageAnchor(href = null) { Icon("arrow", "up") { +Size.Huge } }
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
