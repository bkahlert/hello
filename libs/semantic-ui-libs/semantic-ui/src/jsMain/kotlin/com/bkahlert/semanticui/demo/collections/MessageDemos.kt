package com.bkahlert.semanticui.demo.collections

import com.bkahlert.semanticui.collection.Header
import com.bkahlert.semanticui.collection.IconMessage
import com.bkahlert.semanticui.collection.Message
import com.bkahlert.semanticui.collection.attached
import com.bkahlert.semanticui.collection.colored
import com.bkahlert.semanticui.collection.compact
import com.bkahlert.semanticui.collection.error
import com.bkahlert.semanticui.collection.floating
import com.bkahlert.semanticui.collection.hidden
import com.bkahlert.semanticui.collection.info
import com.bkahlert.semanticui.collection.negative
import com.bkahlert.semanticui.collection.positive
import com.bkahlert.semanticui.collection.size
import com.bkahlert.semanticui.collection.success
import com.bkahlert.semanticui.collection.visible
import com.bkahlert.semanticui.collection.warning
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Attached.Bottom
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Colored
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.Demos
import com.bkahlert.semanticui.demo.LoremIpsumParagraph
import com.bkahlert.semanticui.demo.custom.SemanticDemo
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.States
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.Types
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.Variations
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.Segment
import com.bkahlert.semanticui.element.attached
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.B
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Ul

public val MessageDemos: SemanticDemo = SemanticDemo(
    "Message",
    Types {
        Demo("Message") {
            Message {
                Header { Text("Changes in Service") }
                P { Text("We just updated our privacy policy here to better service our customers. We recommend reviewing the changes.") }
            }
        }
        Demo("List Message") {
            Message {
                Header { Text("New Site Features") }
                Ul({ classes("list") }) {
                    Li { Text("You can now have cover images on blog pages") }
                    Li { Text("Drafts will now auto-save while writing") }
                }
            }
        }
        Demo("Icon Message") {
            IconMessage("inbox") {
                Header { Text("Have you heard about our mailing list?") }
                P { Text("Get the best news in your e-mail every day.") }
            }
        }
        Demo("Dismissible Block Message", warning = "Not implemented") {
            Message {
                Icon("close")
                Header { Text("Welcome back!") }
                P { Text("This is a special notification which you can dismiss if you're bored with it.") }
            }
        }
    },
    States {
        Demo("Hidden") {
            Message({ s.hidden() }) {
                P { Text("You can't see me") }
            }
        }
        Demo("Visible") {
            Message({ s.visible() }) {
                P { Text("You can always see me") }
            }
        }
    },
    Variations {
        Demo("Floating") {
            Message({ v.floating() }) {
                P { Text("Way to go!") }
            }
        }
        Demo("Compact") {
            Message({ v.compact() }) {
                P { Text("Get all the best inventions in your e-mail every day. Sign up now!") }
            }
        }
        Demo("Attached") {
            Message({ v.attached() }) {
                Header { Text("Welcome to our site!") }
                P { Text("Fill out the form below to sign-up for a new account") }
            }
            Segment({ v.attached() }) { LoremIpsumParagraph() }
            Message({ v.attached(Bottom).warning() }) {
                Icon("help")
                Text("Already signed up? ")
                A { Text("Login here") }
                Text(" instead.")
            }
        }
        Demo("Warning") {
            Message({ v.warning() }) {
                Icon("close")
                Header { Text("You must register before you can do that!") }
                P { Text("Visit our registration page, then try again ") }
            }
        }
        Demo("Info") {
            Message({ v.info() }) {
                Icon("close")
                Header { Text("Was this what you wanted?") }
                Ul({ classes("list") }) {
                    Li { Text("It's good to see you again.") }
                    Li { Text("Did you know it's been a while?") }
                }
            }
        }
        Demos("Positive / Success") {
            Demo("Positive") {
                Message({ v.positive() }) {
                    Icon("close")
                    Header { Text("You are eligible for a reward") }
                    P { Text("Go to your "); B { Text("special offers") }; Text(" page to see now.") }
                }
            }
            Demo("Success") {
                Message({ v.success() }) {
                    Icon("close")
                    Header { Text("Your user registration was successful. ") }
                    P { Text("You may now log-in with the username you have chosen") }
                }
            }
        }
        Demos("Negative / Error") {
            Demo("Negative") {
                Message({ v.negative() }) {
                    Icon("close")
                    Header { Text("We're sorry we can't apply that discount") }
                    P { Text("That offer has expired") }
                }
            }
            Demo("Error") {
                Message({ v.error() }) {
                    Icon("close")
                    Header { Text("There were some errors with your submission") }
                    Ul({ classes("list") }) {
                        Li { Text("You must include both a upper and lower case letters in your password.") }
                        Li { Text("You need to select your home country.") }
                    }
                }
            }
        }
        Demo("Colored") {
            Colored.forEach {
                Message({ v.colored(it) }) { P { Text("$it") } }
            }
        }
        Demo("Size") {
            Size.forEach {
                Message({ v.size(it) }) { P { Text("$it") } }
            }
        }
    }
)
