package com.bkahlert.hello.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.bkahlert.kommons.compose.data
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.element.Button
import com.semanticui.compose.element.Icon
import com.semanticui.compose.element.Item
import com.semanticui.compose.element.ListElement
import com.semanticui.compose.element.horizontal
import com.semanticui.compose.module.Checkbox
import com.semanticui.compose.module.CheckboxElementType.Toggle
import com.semanticui.compose.module.Divider
import com.semanticui.compose.module.Header
import com.semanticui.compose.module.InlineMultipleDropdown
import com.semanticui.compose.module.Item
import com.semanticui.compose.module.Menu
import com.semanticui.compose.module.MultipleDropdownState
import com.semanticui.compose.module.MultipleDropdownStateImpl
import com.semanticui.compose.module.Text
import com.semanticui.compose.module.scrolling
import com.semanticui.compose.toJsonArray
import org.jetbrains.compose.web.attributes.InputType.Checkbox
import org.jetbrains.compose.web.attributes.InputType.Hidden
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.backgroundImage
import org.jetbrains.compose.web.css.backgroundRepeat
import org.jetbrains.compose.web.css.backgroundSize
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.flex
import org.jetbrains.compose.web.css.fontWeight
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.left
import org.jetbrains.compose.web.css.lineHeight
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.marginLeft
import org.jetbrains.compose.web.css.marginRight
import org.jetbrains.compose.web.css.maxWidth
import org.jetbrains.compose.web.css.minWidth
import org.jetbrains.compose.web.css.paddingBottom
import org.jetbrains.compose.web.css.paddingTop
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.whiteSpace
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement
import kotlin.js.json
import com.semanticui.compose.element.List as SemanticList

@Stable
interface SearchEngineSelectState : MultipleDropdownState {
    val engines: List<SearchEngine>
    var enginesSelection: List<SearchEngine>

    var enginesSelectionNames: Array<String>
        get() = enginesSelection.toJsonArray { it.name }
        set(value) {
            enginesSelection = value.map { SearchEngine.valueOf(it) }
        }

    var enginesSelectionString: String
        get() = enginesSelection.joinToString(",") { it.name }
        set(value) {
            enginesSelection = value.split(",").mapNotNull { name ->
                engines.firstOrNull { it.name.equals(name, ignoreCase = true) }
            }
        }

    val noEngines: Boolean get() = enginesSelection.isEmpty()
    var allEngines: Boolean
}

class SearchEngineSelectStateImpl(
    engines: List<SearchEngine> = emptyList(),
    enginesSelection: List<SearchEngine> = emptyList(),
    onEngineSelect: (old: List<SearchEngine>, new: List<SearchEngine>) -> Unit,
    options: Map<String, Any?>,
    private val toString: (SearchEngine) -> String = { it.name },
    private val fromString: (String) -> SearchEngine = run {
        val mappings: Map<String, SearchEngine> = engines.associateBy { it.name }
        ({ mappings.getValue(it) })
    },
) : SearchEngineSelectState, MultipleDropdownState by MultipleDropdownStateImpl(
    engines.map { toString(it) },
    enginesSelection.map { toString(it) },
    { old, new -> onEngineSelect(old.map(fromString), new.map(fromString)) },
    options,
) {
    override var enginesSelection: List<SearchEngine>
        get() = selection.map { fromString(it) }
        set(value) {
            selection = value.map { toString(it) }
        }

    override val engines: List<SearchEngine>
        get() = values.map { fromString(it) }

    override var allEngines: Boolean by ::allValues
}

@Composable
fun rememberSearchEngineSelectState(
    vararg engines: SearchEngine = SearchEngine.values(),
    selected: (SearchEngine) -> Boolean = { it == SearchEngine.Default },
    onEngineSelect: (old: List<SearchEngine>, new: List<SearchEngine>) -> Unit = { old, new ->
        console.log("selection changed from $old to $new")
    },
    debug: Boolean = false,
): SearchEngineSelectState {
    val enginesSelection = engines.filter(selected)
    val options = mapOf(
        "debug" to debug,
        "message" to json("count" to "{count}/${engines.size} engine(s) selected"),
        "placeholder" to "no engine selected",
        "useLabels" to false,
    )
    return remember(enginesSelection, engines) {
        SearchEngineSelectStateImpl(
            engines = engines.toList(),
            enginesSelection = enginesSelection,
            onEngineSelect = onEngineSelect,
            options = options,
        )
    }
}

@Composable
fun SearchEngineSelect(
    state: SearchEngineSelectState = rememberSearchEngineSelectState(),
    attrs: SemanticAttrBuilder<ListElement, HTMLDivElement>? = null,
) {
    SemanticList({
        +horizontal
        style {
            display(DisplayStyle.Flex)
            alignItems(AlignItems.Center)
            maxWidth(100.percent)
            whiteSpace("nowrap")
        }
        attrs?.invoke(this)
    }) {
        if (state.allEngines) {
            Item {
                Div({
                    style {
                        backgroundImage("url(rainbow-gradient.svg)");
                        backgroundRepeat("no-repeat")
                        backgroundSize("contain")
                        width(2.em)
                        height(2.em)
                    }
                })
            }
        } else {
            state.enginesSelection.forEach { engine ->
                Item({
                    style {
                        flex("0 1 auto")
                        minWidth(0.px)
                        marginLeft(0.px)
                    }
                }) {
                    ClosableSearchEngineButton(engine) { state.enginesSelection -= engine }
                }
            }
        }
        Item({
            style {
                marginLeft(0.px)
            }
        }) {
            SearchEngineDropdown(state)
        }
    }
}

@Composable
fun SearchEngineDropdown(
    state: SearchEngineSelectState = rememberSearchEngineSelectState(),
    debug: Boolean = false,
) {
    InlineMultipleDropdown(state) {
        Input(Hidden) { name("engine");value(state.enginesSelectionString) }
        // it's virtually impossible to provide a custom text its always updated by SemanticUI, even with a custom action
        Text({ classes("default");style { display(DisplayStyle.None) } }) { Text("no engines selected") }
        Div({
            style { // = text class
                display(DisplayStyle.InlineBlock)
                fontWeight(700)
                margin(.45238095.em, 0.em, .45238095.em, .64285714.em)
                lineHeight(1.21428571.em)
            }
        }) {
            when (val num = state.enginesSelection.size) {
                0 -> Text("no engines selected")
                state.engines.size -> Text("all engines selected")
                else -> Text("$num/${state.engines.size} engines selected")
            }
        }
        Icon("dropdown")
        Menu {
            Checkbox(Toggle, { classes("input") }) {
                Input(Checkbox) {
                    tabIndex(0)
                    checked(state.allEngines)
                    onChange { state.allEngines = !state.allEngines }
                }
                Label { Text("All Engines") }
            }
            Divider()
            Header {
                Icon("search")
                Text("Select Search Engines")
            }
            Menu({ +scrolling }) {
                state.engines.forEach { engine ->
                    Item({
                        data("value", engine.name)
                    }) {
                        Icon(*engine.icon) { style { color(engine.color) } }
                        Text(engine.name)
                    }
                }
            }
        }

    }

    if (debug) {
        state.engines.forEach { engine ->
            Checkbox {
                Input(Checkbox) {
                    onChange {
                        console.log("selection switched by checkbox of $engine to ${it.value}")
                        if (it.value) {
                            state.enginesSelection += engine
                        } else {
                            state.enginesSelection -= engine
                        }
                    }
                    checked(state.enginesSelection.contains(engine))
                }
                Label {
                    Icon(*engine.icon) { style { color(engine.color) } }
                    Text(engine.name)
                }
            }
        }
    }
}

/**
 * A borderless [searchEngine] representing button
 * that transforms into a close button.
 */
@Composable
fun ClosableSearchEngineButton(
    searchEngine: SearchEngine,
    onClose: () -> Unit = { console.log("onClose($searchEngine)") },
) {
    Button({
        classes("animated", "fade")
        +Size.Tiny + Compact + Basic + Icon
        style {
            property("box-shadow", "none")
            marginLeft((-.5).em)
            paddingTop(0.em)
            paddingBottom(.2.em)
        }

        onClick { onClose() }
    }) {
        Div({
            classes("visible", "content")
            style { marginRight(0.25.em) }
        }) {
            Icon(*searchEngine.icon) {
                style { color(searchEngine.color) }
            }
        }
        Div({
            classes("hidden", "content")
            style { left(0.125.em) }
        }) {
            Icon("close")
        }
    }
}
