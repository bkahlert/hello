package com.bkahlert.hello.debug

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.debug.DebugModeState.Active
import com.bkahlert.hello.debug.DebugModeState.Inactive
import com.bkahlert.hello.debug.clickup.ActivityDropdownDemo
import com.bkahlert.hello.debug.clickup.ClickUpMenuDemo
import com.bkahlert.hello.debug.clickup.PomodoroStarterDemo
import com.bkahlert.hello.debug.clickup.PomodoroTimerDemo
import com.bkahlert.hello.debug.kommons.ModifiedColorDemo
import com.bkahlert.hello.debug.kommons.RandomColorsDemo
import com.bkahlert.hello.debug.kommons.SiteColorsDemo
import com.bkahlert.hello.debug.misc.DimmingLoaderDemo
import com.bkahlert.hello.debug.misc.IdleDetectoryDemo
import com.bkahlert.hello.debug.misc.MutableFlowStateDemo
import com.bkahlert.hello.debug.misc.ViewModelDemo
import com.bkahlert.hello.debug.search.SearchEngineDropdownDemos
import com.bkahlert.hello.debug.search.SearchEngineSelectDemos
import com.bkahlert.hello.debug.search.SearchInputDemos
import com.bkahlert.hello.debug.semanticui.CollectionsDemos
import com.bkahlert.hello.debug.semanticui.ElementsDemos
import com.bkahlert.hello.debug.semanticui.ModulesDemos
import com.bkahlert.hello.debug.semanticui.ViewsDemos
import com.bkahlert.kommons.dom.div
import com.bkahlert.kommons.dom.fragment
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticElement
import com.bkahlert.semanticui.core.jQuery
import com.bkahlert.semanticui.module.Content
import com.bkahlert.semanticui.module.Modal
import com.bkahlert.semanticui.module.autofocus
import com.bkahlert.semanticui.module.blurring
import com.bkahlert.semanticui.module.centered
import kotlinx.browser.window
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement

typealias TabContentBuilder = SemanticContentBuilder<SemanticElement<HTMLDivElement>>

@Composable
private fun TabMenu(
    vararg tabs: Pair<String, TabContentBuilder>,
    activeTab: Int = 0,
    firstContent: SemanticContentBuilder<SemanticElement<HTMLDivElement>>? = null,
    lastContent: SemanticContentBuilder<SemanticElement<HTMLDivElement>>? = null,
    onChange: (Int, String) -> Unit = { tabIndex, name -> console.log("onChange($tabIndex, $name)") },
) {
    val validActiveTab = activeTab.coerceIn(0, tabs.size - 1)
    S("ui", "pointing", "menu") {
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
    S("ui", "inverted", "segment") {
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
    attrs: SemanticAttrBuilderContext<SemanticElement<HTMLDivElement>>? = null,
    content: SemanticContentBuilder<SemanticElement<HTMLDivElement>>? = null,
) = S("ui", "two", "column", "doubling", "grid", "container", attrs = attrs, content = content)

@Composable
private fun Column(
    attrs: SemanticAttrBuilderContext<SemanticElement<HTMLDivElement>>? = null,
    content: SemanticContentBuilder<SemanticElement<HTMLDivElement>>? = null,
) = S("column", attrs = attrs, content = content)

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
            "Kommons" to {
                Grid {
                    Column {
                        SiteColorsDemo()
                    }
                    Column {
                        RandomColorsDemo()
                        ModifiedColorDemo()
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
            S("header", "item") { Text("UI Demos") }
        },
        onChange = onChange,
    )
}

/**
 * Renders a `F4` key trigger-able [DebugMode] demonstrating various UI elements
 * and the optional [focusContent].
 */
val renderDebugMode: Unit get() = renderDebugMode()


/**
 * Renders a `F4` key trigger-able [DebugMode] demonstrating various UI elements
 * and the optional [focusContent].
 */
fun renderDebugMode(
    focusContent: TabContentBuilder? = null,
) {

    val defaultTab = -1
    var debug: List<Int>? by window.location::fragment / Json

    DebugMode(
        active = debug != null,
        onStateChange = { state ->
            debug = when (state) {
                is Active -> debug
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
                var activeTab by remember { mutableStateOf(debug?.firstOrNull() ?: defaultTab) }
                DebugUI(
                    activeTab = activeTab,
                    onChange = { index, _ -> activeTab = index.also { debug = listOf(it) } },
                    focusContent = focusContent,
                )
            }
        }
    }
}
