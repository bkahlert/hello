package com.bkahlert.hello.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.kommons.compose.data
import com.bkahlert.kommons.dom.data
import com.semanticui.compose.Semantic
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.UI
import com.semanticui.compose.dropdown
import com.semanticui.compose.element.Button
import com.semanticui.compose.element.Icon
import com.semanticui.compose.element.ListElement
import com.semanticui.compose.element.horizontal
import com.semanticui.compose.jQuery
import com.semanticui.compose.module.Checkbox
import com.semanticui.compose.module.CheckboxElementType.Toggle
import com.semanticui.compose.toJsonArray
import com.semanticui.compose.view.Item
import org.jetbrains.compose.web.attributes.InputType.Checkbox
import org.jetbrains.compose.web.attributes.InputType.Hidden
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.flex
import org.jetbrains.compose.web.css.fontWeight
import org.jetbrains.compose.web.css.left
import org.jetbrains.compose.web.css.lineHeight
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.marginLeft
import org.jetbrains.compose.web.css.marginRight
import org.jetbrains.compose.web.css.maxWidth
import org.jetbrains.compose.web.css.minWidth
import org.jetbrains.compose.web.css.paddingBottom
import org.jetbrains.compose.web.css.paddingRight
import org.jetbrains.compose.web.css.paddingTop
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.whiteSpace
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement
import kotlin.js.json
import com.semanticui.compose.element.List as SemanticList

@Stable
interface SearchEngineSelectState {
    var selectedEngines: List<SearchEngine>
    val availableEngines: List<SearchEngine>

    var selectedEngineNames: Array<String>
        get() = selectedEngines.toJsonArray { it.name }
        set(value) {
            selectedEngines = value.map { SearchEngine.valueOf(it) }
        }

    var selectedEnginesValue: String
        get() = selectedEngines.joinToString(",") { it.name }
        set(value) {
            selectedEngines = value.split(",").mapNotNull { name ->
                availableEngines.firstOrNull { it.name.equals(name, ignoreCase = true) }
            }
        }

    val noEngines: Boolean get() = selectedEngines.isEmpty()
    var allEngines: Boolean
}

class SearchEngineSelectStateImpl(
    selectedEngines: List<SearchEngine>,
    availableEngines: List<SearchEngine> = emptyList(),
) : SearchEngineSelectState {
    private var _selectedEngines by mutableStateOf(selectedEngines)
    override var selectedEngines: List<SearchEngine>
        get() = if (_allEngines) availableEngines else _selectedEngines
        set(value) {
            _selectedEngines = value
            if (!availableEngines.all { _selectedEngines.contains(it) }) {
                _allEngines = false
            }
        }
    override val availableEngines by mutableStateOf(availableEngines)

    private var _allEngines by mutableStateOf(false)
    override var allEngines: Boolean
        get() = _allEngines || availableEngines.all { _selectedEngines.contains(it) }
        set(value) {
            _allEngines = value && !availableEngines.all { _selectedEngines.contains(it) }
        }
}

@Composable
fun rememberSearchEngineSelectState(
    vararg engines: SearchEngine = SearchEngine.values(),
    selected: (SearchEngine) -> Boolean = { it == SearchEngine.Default },
): SearchEngineSelectState {
    val selectedEngines = engines.filter(selected)
    val availableEngines = engines.toList()
    return remember(selectedEngines, availableEngines) {
        SearchEngineSelectStateImpl(
            selectedEngines = selectedEngines,
            availableEngines = availableEngines,
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
        state.selectedEngines.forEach { engine ->
            Item({
                style {
                    flex("0 1 auto")
                    minWidth(0.px)
                    marginLeft(0.px)
                }
            }) {
                ClosableSearchEngineButton(engine) { state.selectedEngines -= engine }
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
    UI("floating", "inline", "multiple", "dropdown", attrs = {
        style {
            paddingRight(.35714286.em)
        }
    }) {
        Input(Hidden) { name("engine");value(state.selectedEnginesValue) }
        Semantic("text", "default", attrs = { style { display(DisplayStyle.None) } }) { Text("no engines selected") }
        Semantic(attrs = {
            style { // = text class
                display(DisplayStyle.InlineBlock)
                fontWeight(700)
                margin(.45238095.em, 0.em, .45238095.em, .64285714.em)
                lineHeight(1.21428571.em)
            }
        }) {
            when (val num = state.selectedEngines.size) {
                0 -> Text("no engines selected")
                state.availableEngines.size -> Text("all engines selected")
                else -> Text("$num/${state.availableEngines.size} engines selected")
            }
        }
        Icon("dropdown")
        Semantic("menu") {
            Checkbox(Toggle, { classes("input") }) {
                Input(Checkbox) {
                    tabIndex(0)
                    checked(state.allEngines)
                    onChange { state.allEngines = !state.allEngines }
                }
                Label { Text("All Engines at Once") }
            }
            Semantic("divider")
            Semantic("header") {
                Icon("search")
                Text("Select Search Engines")
            }
            Semantic("scrolling", "menu") {
                state.availableEngines.forEach { engine ->
                    Item({
                        data("value", engine.name)
                    }) {
                        Icon(*engine.icon) { style { color(engine.color) } }
                        Text(engine.name)
                    }
                }
            }
        }

        DisposableEffect(state) {
            jQuery(scopeElement)
                .dropdown(
                    "message" to json("count" to "{count}/${state.availableEngines.size} engine(s) selected"),
                    "onChange" to fun(value: String) {
                        if (scopeElement.data("muted") == null) {
                            if (debug) console.log("selection changed by dropdown to $value")
                            state.selectedEnginesValue = value
                        }
                    },
                    "placeholder" to "no engine selected",
                    "useLabels" to false,
                )
            onDispose { }
        }
        DisposableEffect(state.selectedEngines) {
            jQuery(scopeElement)
                .attr("data-muted", true)
                .dropdown("set exactly", state.selectedEngineNames)
                .attr("data-muted", null)
            onDispose { }
        }
    }

    if (debug) {
        state.availableEngines.forEach { engine ->
            Checkbox {
                Input(Checkbox) {
                    onChange {
                        console.log("selection switched by checkbox of $engine to ${it.value}")
                        if (it.value) {
                            state.selectedEngines += engine
                        } else {
                            state.selectedEngines -= engine
                        }
                    }
                    checked(state.selectedEngines.contains(engine))
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
