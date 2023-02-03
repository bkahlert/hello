package com.bkahlert.semanticui.demo.elements

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Action.Right
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Floating
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Icon.Left
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.custom.SemanticDemo
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.States
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.Types
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.Variations
import com.bkahlert.semanticui.demo.custom.SemanticType
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.Input
import com.bkahlert.semanticui.element.action
import com.bkahlert.semanticui.element.disabled
import com.bkahlert.semanticui.element.error
import com.bkahlert.semanticui.element.focus
import com.bkahlert.semanticui.element.icon
import com.bkahlert.semanticui.element.labeled
import com.bkahlert.semanticui.element.loading
import com.bkahlert.semanticui.module.Dropdown
import com.bkahlert.semanticui.module.Item
import com.bkahlert.semanticui.module.Menu
import com.bkahlert.semanticui.module.Text
import org.jetbrains.compose.web.attributes.InputType.Text
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Text

public val InputDemos: SemanticDemo = SemanticDemo(
    SemanticType.Element,
    "Input",
    Types {
        Demo("Input") {
            Input { AnyInput("Input") }
        }
    },
    States {
        Demo("Focus") {
            Input({ s.focus() }) { AnyInput("Input in focus state") }
        }
        Demo("Disabled") {
            Input({ s.disabled() }) { AnyInput("Input in disabled state") }
        }
        Demo("Error") {
            Input({ s.error() }) { AnyInput("Input in error state") }
        }
    },
    Variations {
        Demo("Icon") {
            Input({ v.icon() }) {
                AnyInput("Input with icon")
                Icon("circular", "search", "link")
            }
        }
        Demo("Loading") {
            Input({
                s.loading()
                v.icon()
            }) {
                AnyInput("Input with loading icon")
                Icon("search", "link")
            }
        }
        Demo("Labeled") {
            Input({ v.labeled() }) {
                S("ui", "label") { Text("https://") }
                AnyInput("example.com")
            }
        }
        Demo("Action") {
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
    },
)

@Suppress("NOTHING_TO_INLINE")
@Composable
private inline fun AnyInput(placeholder: String) {
    Input(Text) { placeholder(placeholder) }
}
