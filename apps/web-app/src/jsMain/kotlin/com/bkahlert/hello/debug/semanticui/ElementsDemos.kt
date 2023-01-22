package com.bkahlert.hello.debug.semanticui

import androidx.compose.runtime.Composable
import com.bkahlert.hello.clickup.model.fixtures.ImageFixtures.JohnDoe
import com.bkahlert.hello.clickup.model.fixtures.ImageFixtures.KommonsLogo
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.attributes.Modifier.State.Active
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Action.Right
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Floated
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Floating
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Fluid
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Icon.Left
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Inverted
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.LineLength.Full
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.LineLength.Long
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.LineLength.Medium
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.LineLength.Short
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.LineLength.VeryLong
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.LineLength.VeryShort
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Huge
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Mini
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Tiny
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.VerticallyAligned.Top
import com.bkahlert.semanticui.custom.Demo
import com.bkahlert.semanticui.custom.Demos
import com.bkahlert.semanticui.element.BasicButton
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
import com.bkahlert.semanticui.element.lineLength
import com.bkahlert.semanticui.element.loading
import com.bkahlert.semanticui.element.negative
import com.bkahlert.semanticui.element.rounded
import com.bkahlert.semanticui.element.size
import com.bkahlert.semanticui.element.spaced
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
                    Image(KommonsLogo, "Mini") { v.size(Mini).spaced() }
                    Image(KommonsLogo, "Tiny") { v.size(Tiny).spaced() }
                    Image(KommonsLogo, "Small") { v.size(Size.Small).spaced() }
                }
                Item { Image(KommonsLogo, "Bordered") { v.size(Tiny).bordered() } }
                Item { Image(KommonsLogo, "Rounded") { v.size(Tiny).rounded() } }
                Item { Image(KommonsLogo, "Circular") { v.size(Tiny).circular() } }
                Item {
                    Content {
                        Image({ v.avatar() }) { Img(JohnDoe.toString()) }
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
                Item { Input({ s.focus() }) { AnyInput("Input in focus state") } }
                Item { Input({ s.disabled() }) { AnyInput("Input in disabled state") } }
                Item { Input({ s.error() }) { AnyInput("Input in error state") } }
                Item {
                    Input({ v.icon() }) {
                        AnyInput("Input with icon")
                        Icon("circular", "search", "link")
                    }
                }
                Item {
                    Input({
                        s.loading()
                        v.icon()
                    }) {
                        AnyInput("Input with loading icon")
                        Icon("search", "link")
                    }
                }

                Item {
                    Input({ v.labeled() }) {
                        S("ui", "label") { Text("https://") }
                        AnyInput("example.com")
                    }
                }
                Item {
                    Input({ v.action(Right).icon(Left) }) {
                        Icon("search")
                        AnyInput("Search...")
                        Dropdown({ classes("basic"); raw(Floating); classes("button") }) {
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
                    Content({ v.floated(Floated.Right) }) { Text("+ right floated") }
                    Icon("help")
                    Content {
                        Header { Text("Header") }
                        Description { Text("Description") }
                    }
                }
            }
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

        Demo("Placeholder") {
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

        Demo("Buttons") {
            SemanticList {
                Item { Button({ }) { Text("Button") } }
                Item { BasicButton({ }) { Text("Basic Button") } }
                Item { Button({ v.negative() }) { Text("Negative Button") } }
                Item { BasicButton({ v.negative() }) { Text("Negative Basic Button") } }
            }
        }

        Demo("Loader", { classes(*Inverted.classNames) }) {
            Placeholder({ raw(Inverted, Fluid) }) {
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
            Loader({ raw(Active) })
        }

        Demo("Text Loader", { classes(*Inverted.classNames) }) {
            Placeholder({ raw(Inverted, Fluid) }) {
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
            TextLoader({ raw(Active) }) { Text("Loading...") }
        }

    }
}
