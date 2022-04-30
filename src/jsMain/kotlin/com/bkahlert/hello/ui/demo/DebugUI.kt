package com.bkahlert.hello.ui.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.plugins.clickup.ClickUpMenu
import com.bkahlert.hello.ui.demo.clickup.ActivityDropdownDemo
import com.bkahlert.hello.ui.demo.clickup.ClickUpFixtures
import com.bkahlert.hello.ui.demo.clickup.ClickUpFixtures.running
import com.bkahlert.hello.ui.demo.clickup.ClickUpMenuDemo
import com.bkahlert.hello.ui.demo.clickup.ClickUpTestClient
import com.bkahlert.hello.ui.demo.clickup.PomodoroStarterDemo
import com.bkahlert.hello.ui.demo.clickup.PomodoroTimerDemo
import com.bkahlert.hello.ui.demo.clickup.rememberClickUpMenuTestViewModel
import com.bkahlert.hello.ui.demo.misc.DimmingLoaderDemo
import com.bkahlert.hello.ui.demo.misc.IdleDetectoryDemo
import com.bkahlert.hello.ui.demo.misc.MutableFlowStateDemo
import com.bkahlert.hello.ui.demo.misc.ViewModelDemo
import com.bkahlert.hello.ui.demo.search.SearchEngineDropdownDemos
import com.bkahlert.hello.ui.demo.search.SearchEngineSelectDemos
import com.bkahlert.hello.ui.demo.search.SearchInputDemos
import com.bkahlert.hello.ui.demo.semanticui.CollectionsDemos
import com.bkahlert.hello.ui.demo.semanticui.ElementsDemos
import com.bkahlert.hello.ui.demo.semanticui.ModulesDemos
import com.bkahlert.hello.ui.demo.semanticui.ViewsDemos
import com.bkahlert.kommons.dom.InMemoryStorage
import com.bkahlert.kommons.dom.Storage
import com.bkahlert.kommons.dom.default
import com.semanticui.compose.Semantic
import com.semanticui.compose.SemanticUI
import com.semanticui.compose.jQuery
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.DOMScope
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

@Composable
private fun TabMenu(
    vararg tabs: Pair<String, @Composable DOMScope<HTMLElement>.() -> Unit>,
    activeTab: Int = 0,
    firstContent: ContentBuilder<HTMLDivElement>? = null,
    lastContent: ContentBuilder<HTMLDivElement>? = null,
    onChange: (Int, String) -> Unit = { tabIndex, name -> console.log("onChange($tabIndex, $name)") },
) {
    SemanticUI("pointing", "menu") {
        firstContent?.invoke(this)
        tabs.forEachIndexed { index, (name, _) ->
            A(null, {
                classes("item")
                if (index == activeTab) classes("active")
                onClick { onChange(index, name) }
            }) { Text(name) }
        }
        lastContent?.invoke(this)
    }
    SemanticUI("inverted", "segment") {
        tabs[activeTab].second(this)
        DisposableEffect(activeTab) {
            jQuery(".modal").modal("refresh")
            onDispose { }
        }
    }
}

@Composable
private fun Grid(
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: ContentBuilder<HTMLDivElement>? = null,
) = SemanticUI("two", "column", "doubling", "grid", "container", attrs = attrs, content = content)

@Composable
private fun Column(
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: ContentBuilder<HTMLDivElement>? = null,
) = Semantic("column", attrs = attrs, content = content)

@Composable
fun DebugUI(
    storage: Storage = InMemoryStorage(),
) {
    var initialTab by storage default 0
    var activeTab by remember { mutableStateOf(initialTab) }

    TabMenu(
        "ClickUp" to {
            Grid {
                Column {
                    ActivityDropdownDemo()
                    PomodoroStarterDemo()
                    PomodoroTimerDemo()
                }
                Column {
                    ClickUpMenuDemo()
                }
            }
        },
        "Search" to {
            Grid {
                Column {
                    SearchEngineDropdownDemos()
                }
                Column {
                    SearchEngineSelectDemos()
                }
                Column {
                    SearchInputDemos()
                }
            }
        },
        "Semantic UI" to {
            Grid {
                Column {
                    ElementsDemos()
                    CollectionsDemos()
                }
                Column {
                    ViewsDemos()
                    ModulesDemos()
                }
            }
        },
        "Misc" to {
            Grid {
                Column {
                    DimmingLoaderDemo()
                    ViewModelDemo()
                }
                Column {
                    MutableFlowStateDemo()
                    IdleDetectoryDemo()
                }
            }
        },
        "Focus" to {

            Demos("ClickUp Menu (Synchronized)") {
                val client = ClickUpTestClient(
                    initialRunningTimeEntry = ClickUpFixtures.TimeEntry.running())
                Demo("Browser 1") {
                    ClickUpMenu(rememberClickUpMenuTestViewModel(client) { toFullyLoaded() })
                }
            }
        },
        activeTab = activeTab,
        firstContent = {
            Semantic("header", "item") { Text("UI Demos") }
        },
    ) { tabIndex, _ -> activeTab = tabIndex.also { initialTab = it } }
}
