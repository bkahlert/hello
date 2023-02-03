package com.bkahlert.semanticui.demo.custom

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.custom.SimpleTab
import com.bkahlert.semanticui.custom.TabMenu
import com.bkahlert.semanticui.custom.rememberTabMenuState
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.PlaceholderImageAndLines
import com.bkahlert.semanticui.demo.PlaceholderParagraph
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.Variations
import com.bkahlert.semanticui.element.Item
import com.bkahlert.semanticui.element.Segment
import com.bkahlert.semanticui.element.divided
import com.bkahlert.semanticui.module.inverted
import org.jetbrains.compose.web.dom.Text
import com.bkahlert.semanticui.element.List as SList

public val TabMenuDemos: SemanticDemo = SemanticDemo(
    null,
    "TabMenu",
    Variations {
        Demo("Empty") {
            TabMenu(rememberTabMenuState(), { v.inverted(); classes("yellow") })
        }
        Demo("Simple Tabs") {
            TabMenu(
                rememberTabMenuState(
                    listOf(
                        SimpleTab("Foo") { PlaceholderImageAndLines() },
                        SimpleTab("Bar") { PlaceholderParagraph() },
                    )
                ),
                { v.inverted();classes("yellow") },
            )
        }
        Demo("Hoisted State") {

            var hoistedState: String? by mutableStateOf("Foo")

            SList({ v.divided() }) {
                Item {
                    S("ui", "horizontal", "green", "label") { Text("HoistedState") }
                    Text(hoistedState ?: "—")
                }
            }

            Segment({ classes("centered", "two", "column", "grid") }) {
                S("column") {
                    val tabMenuState1 = rememberTabMenuState(
                        tabs = listOf(
                            SimpleTab("Foo") { PlaceholderImageAndLines() },
                            SimpleTab("Bar") { PlaceholderParagraph() },
                        ),
                        active = hoistedState,
                    )

                    SList({ v.divided() }) {
                        Item {
                            S("ui", "horizontal", "olive", "label") { Text("TabMenuState-1") }
                            Text(tabMenuState1.active ?: "—")
                        }
                    }

                    TabMenu(tabMenuState1, { v.inverted();classes("olive") })

                    LaunchedEffect(tabMenuState1) {
                        snapshotFlow { tabMenuState1.active }
                            .collect {
                                hoistedState = tabMenuState1.active
                            }
                    }
                }
                S("column") {
                    val tabMenuState2 = rememberTabMenuState(
                        tabs = listOf(
                            SimpleTab("Foo") { PlaceholderImageAndLines() },
                            SimpleTab("Bar") { PlaceholderParagraph() },
                        ),
                        active = hoistedState,
                    )

                    SList({ v.divided() }) {
                        Item {
                            S("ui", "horizontal", "teal", "label") { Text("TabMenuState-2") }
                            Text(tabMenuState2.active ?: "—")
                        }
                    }

                    TabMenu(tabMenuState2, { v.inverted();classes("teal") })

                    LaunchedEffect(tabMenuState2) {
                        snapshotFlow { tabMenuState2.active }
                            .collect {
                                hoistedState = tabMenuState2.active
                            }
                    }
                }
            }
        }
    },
)
