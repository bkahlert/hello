package com.bkahlert.hello.ui.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.DebugMode
import com.bkahlert.hello.DebugModeState.Active
import com.bkahlert.hello.DebugModeState.Inactive
import com.bkahlert.hello.ui.demo.clickup.ActivityDropdownDemo
import com.bkahlert.hello.ui.demo.clickup.ClickUpMenuDemo
import com.bkahlert.hello.ui.demo.clickup.PomodoroStarterDemo
import com.bkahlert.hello.ui.demo.clickup.PomodoroTimerDemo
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
import com.bkahlert.kommons.dom.defaults
import com.semanticui.compose.Semantic
import com.semanticui.compose.SemanticUI
import com.semanticui.compose.jQuery
import com.semanticui.compose.module.Content
import com.semanticui.compose.module.Modal
import com.semanticui.compose.module.autofocus
import com.semanticui.compose.module.blurring
import com.semanticui.compose.module.centered
import kotlinx.browser.window
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.DOMScope
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

typealias TabContentBuilder = @Composable DOMScope<HTMLElement>.() -> Unit

@Composable
private fun TabMenu(
    vararg tabs: Pair<String, TabContentBuilder>,
    activeTab: Int = 0,
    firstContent: ContentBuilder<HTMLDivElement>? = null,
    lastContent: ContentBuilder<HTMLDivElement>? = null,
    onChange: (Int, String) -> Unit = { tabIndex, name -> console.log("onChange($tabIndex, $name)") },
) {
    val validActiveTab = activeTab.coerceIn(0, tabs.size - 1)
    SemanticUI("pointing", "menu") {
        firstContent?.invoke(this)
        tabs.forEachIndexed { index, (name, _) ->
            A(null, {
                classes("item")
                if (index == validActiveTab) classes("active")
                onClick { onChange(index, name) }
            }) { Text(name) }
        }
        lastContent?.invoke(this)
    }
    SemanticUI("inverted", "segment") {
        tabs.getOrNull(validActiveTab)?.also { tab ->
            tab.second(this)
            DisposableEffect(validActiveTab) {
                jQuery(".modal").modal("refresh")
                onDispose { }
            }
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
private fun DebugUI(
    activeTab: Int = 0,
    onChange: (Int, String) -> Unit,
    focusContent: TabContentBuilder? = null,
) {
    TabMenu(
        tabs = listOfNotNull<Pair<String, TabContentBuilder>>(
            focusContent?.let { "Focus" to it },
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
        ).toTypedArray(),
        activeTab = activeTab,
        firstContent = {
            Semantic("header", "item") { Text("UI Demos") }
        },
        onChange = onChange,
    )
}

/**
 * Renders a `F4` key trigger-able [DebugMode] demonstrating various UI elements
 * and the optional [focusContent].
 */
fun renderDebugMode(
    focusContent: TabContentBuilder? = null,
) {

    val defaultTab = -1
    var debug by window.location defaults (defaultTab to null)

    DebugMode(
        active = debug != null,
        onStateChange = { state ->
            debug = when (state) {
                is Active -> debug ?: defaultTab
                is Inactive -> null
            }
        }
    ) {
        Modal({
            +Fullscreen
            +Long
            blurring = false // true will blur popups inside the debug mode, too
            autofocus = false
            centered = false
        }) {
            Content {
                var activeTab by remember { mutableStateOf(debug ?: defaultTab) }
                DebugUI(
                    activeTab = activeTab,
                    onChange = { index, _ -> activeTab = index.also { debug = it } },
                    focusContent = focusContent,
                )
            }
        }
    }
}
