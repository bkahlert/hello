package com.bkahlert.hello.ui.demo.semanticui

import androidx.compose.runtime.Composable
import com.bkahlert.hello.ui.demo.Demo
import com.bkahlert.hello.ui.demo.Demos
import com.bkahlert.hello.ui.demo.ImageFixtures
import com.bkahlert.hello.ui.demo.ImageFixtures.KommonsLogo
import com.semanticui.compose.SemanticUI
import com.semanticui.compose.element.Content
import com.semanticui.compose.element.Description
import com.semanticui.compose.element.Header
import com.semanticui.compose.element.Icon
import com.semanticui.compose.element.Image
import com.semanticui.compose.element.ImageHeader
import com.semanticui.compose.element.Input
import com.semanticui.compose.element.Item
import com.semanticui.compose.element.Line
import com.semanticui.compose.element.Loader
import com.semanticui.compose.element.Paragraph
import com.semanticui.compose.element.Placeholder
import com.semanticui.compose.element.TextLoader
import com.semanticui.compose.element.action
import com.semanticui.compose.element.avatar
import com.semanticui.compose.element.bordered
import com.semanticui.compose.element.circular
import com.semanticui.compose.element.disabled
import com.semanticui.compose.element.divided
import com.semanticui.compose.element.error
import com.semanticui.compose.element.floated
import com.semanticui.compose.element.focus
import com.semanticui.compose.element.horizontal
import com.semanticui.compose.element.icon
import com.semanticui.compose.element.labeled
import com.semanticui.compose.element.loading
import com.semanticui.compose.element.rounded
import com.semanticui.compose.element.size
import com.semanticui.compose.element.spaced
import com.semanticui.compose.element.verticallyAligned
import com.semanticui.compose.module.Dropdown
import com.semanticui.compose.module.Menu
import com.semanticui.compose.module.Text
import org.jetbrains.compose.web.attributes.InputType.Text
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Small
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import com.semanticui.compose.element.List as SemanticList

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
                        SemanticUI("label") { Text("https://") }
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
                    Image { Icon("arrow", "up") { +Size.Huge } }
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
