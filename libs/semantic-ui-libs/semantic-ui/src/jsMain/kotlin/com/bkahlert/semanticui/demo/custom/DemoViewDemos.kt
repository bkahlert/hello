package com.bkahlert.semanticui.demo.custom

import com.bkahlert.kommons.dom.LocationFragmentParameters
import com.bkahlert.kommons.dom.fragmentParameters
import com.bkahlert.kommons.uri.build
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Floated
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Mini
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.DemoProvider
import com.bkahlert.semanticui.demo.DemoView
import com.bkahlert.semanticui.demo.DemoViewState
import com.bkahlert.semanticui.demo.LoremIpsumParagraph
import com.bkahlert.semanticui.demo.PlaceholderImageAndLines
import com.bkahlert.semanticui.demo.PlaceholderParagraph
import com.bkahlert.semanticui.demo.asDemoViewState
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.Variations
import com.bkahlert.semanticui.element.BasicButton
import com.bkahlert.semanticui.element.Container
import com.bkahlert.semanticui.element.floated
import com.bkahlert.semanticui.element.primary
import com.bkahlert.semanticui.element.size
import io.ktor.util.StringValues
import kotlinx.browser.window
import org.jetbrains.compose.web.dom.Text

public val DemoViewDemos: SemanticDemo = SemanticDemo(
    null,
    "DemoView",
    Variations {
        Demo("Empty") {
            DemoView()
        }
        Demo("One Provider") {
            DemoView(
                DemoProvider("foo", "Foo", { Demo("Foo Demo") { LoremIpsumParagraph() } }),
                DemoProvider("bar", "Bar", { Demo("Bar Demo") { PlaceholderParagraph() } }),
            )
        }
        Demo("One Provider + Trash") {
            DemoView(
                DemoProvider("foo", "Foo", { Demo("Foo Demo") { LoremIpsumParagraph() } }),
            ) { Demo("Trash Demo") { PlaceholderImageAndLines() } }
        }

        val fragmentName = "demo-view-demo"
        Demo("Bound to #$fragmentName") {

            val demoViewSate: DemoViewState = LocationFragmentParameters(window).asDemoViewState(fragmentName)

            Container({ classes("center", "aligned") }) {
                BasicButton({
                    v.size(Mini).floated(Floated.Right).primary()
                    onClick { demoViewSate.onActivate("bar") }
                }) { Text("Set demoViewSate=bar") }

                BasicButton({
                    v.size(Mini).floated(Floated.Left).primary()
                    onClick {
                        window.location.fragmentParameters = StringValues.build(window.location.fragmentParameters) {
                            this[fragmentName] = "foo"
                        }
                    }
                }) { Text("Set #$fragmentName=foo") }

                S("ui", "label") {
                    Text("State")
                    S("detail") { Text(demoViewSate.activeId ?: "—") }
                }
            }

            DemoView(
                DemoProvider("foo", "Foo", { Demo("Foo Demo") { LoremIpsumParagraph() } }),
                DemoProvider("bar", "Bar", { Demo("Bar Demo") { PlaceholderParagraph() } }),
                state = demoViewSate,
            )
        }
    },
)
