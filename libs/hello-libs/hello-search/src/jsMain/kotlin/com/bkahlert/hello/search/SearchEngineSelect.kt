package com.bkahlert.hello.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Tiny
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.custom.color
import com.bkahlert.semanticui.custom.data
import com.bkahlert.semanticui.element.BasicButton
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.Item
import com.bkahlert.semanticui.element.ListElement
import com.bkahlert.semanticui.element.compact
import com.bkahlert.semanticui.element.horizontal
import com.bkahlert.semanticui.element.size
import com.bkahlert.semanticui.module.Checkbox
import com.bkahlert.semanticui.module.CheckboxElementType.Toggle
import com.bkahlert.semanticui.module.Divider
import com.bkahlert.semanticui.module.Header
import com.bkahlert.semanticui.module.InlineMultipleDropdown
import com.bkahlert.semanticui.module.Item
import com.bkahlert.semanticui.module.Menu
import com.bkahlert.semanticui.module.MultipleDropdownState
import com.bkahlert.semanticui.module.MultipleDropdownStateImpl
import com.bkahlert.semanticui.module.Text
import com.bkahlert.semanticui.module.scrolling
import org.jetbrains.compose.web.attributes.InputType.Checkbox
import org.jetbrains.compose.web.attributes.InputType.Hidden
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.backgroundImage
import org.jetbrains.compose.web.css.backgroundRepeat
import org.jetbrains.compose.web.css.backgroundSize
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
import kotlin.js.json
import com.bkahlert.semanticui.element.List as SemanticList

@Stable
public interface SearchEngineSelectState : MultipleDropdownState<SearchEngine>

public class SearchEngineSelectStateImpl(
    engines: List<SearchEngine> = emptyList(),
    selection: List<SearchEngine> = emptyList(),
    onSelect: (old: List<SearchEngine>, new: List<SearchEngine>) -> Unit,
    options: Map<String, Any?>,
    serializer: (SearchEngine) -> String = { it.name },
    deserializer: (String) -> SearchEngine = run {
        val mappings: Map<String, SearchEngine> = engines.associateBy { it.name }
        ({ mappings.getValue(it) })
    },
) : SearchEngineSelectState, MultipleDropdownState<SearchEngine> by MultipleDropdownStateImpl<SearchEngine>(
    options,
    engines,
    selection,
    onSelect, serializer, deserializer,
)

@Composable
public fun rememberSearchEngineSelectState(
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
        "message" to json("count" to "{count}/${engines.size}"),
        "placeholder" to "no search engine",
        "useLabels" to false,
    )
    return remember(enginesSelection, engines) {
        SearchEngineSelectStateImpl(
            engines = engines.toList(),
            selection = enginesSelection,
            onSelect = onEngineSelect,
            options = options,
        )
    }
}

@Composable
public fun SearchEngineSelect(
    state: SearchEngineSelectState = rememberSearchEngineSelectState(),
    attrs: SemanticAttrBuilderContext<ListElement>? = null,
) {
    SemanticList({
        v.horizontal()
        style {
            display(DisplayStyle.Flex)
            alignItems(AlignItems.Center)
            maxWidth(100.percent)
            whiteSpace("nowrap")
        }
        attrs?.invoke(this)
    }) {
        if (state.allValues) {
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
            state.selection.forEach { engine ->
                Item({
                    style {
                        flex("0 1 auto")
                        minWidth(0.px)
                        marginLeft(0.px)
                    }
                }) {
                    ClosableSearchEngineButton(engine) { state.selection -= engine }
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
public fun SearchEngineDropdown(
    state: SearchEngineSelectState = rememberSearchEngineSelectState(),
    debug: Boolean = false,
) {
    InlineMultipleDropdown(state) {
        Input(Hidden) { name("engine");value(state.selectionString) }
        // it's virtually impossible to provide a custom text its always updated by SemanticUI, even with a custom action
        Text({ classes("default");style { display(DisplayStyle.None) } }) { Text("no search engine") }
        Div({
            style { // = text class
                display(DisplayStyle.InlineBlock)
                fontWeight(700)
                margin(.45238095.em, 0.em, .45238095.em, .64285714.em)
                lineHeight(1.21428571.em)
            }
        }) {
            when (val num = state.selection.size) {
                0 -> Text("no search engine")
                state.values.size -> Text("all")
                else -> Text("$num/${state.values.size}")
            }
        }
        Icon("dropdown")
        Menu {
            Checkbox(Toggle, { classes("input") }) {
                Input(Checkbox) {
                    tabIndex(0)
                    checked(state.allValues)
                    onChange { state.allValues = !state.allValues }
                }
                Label { Text("All Engines") }
            }
            Divider()
            Header {
                Icon("search")
                Text("Select Search Engines")
            }
            Menu({ v.scrolling() }) {
                state.values.forEach { engine ->
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
        state.values.forEach { engine ->
            Checkbox {
                Input(Checkbox) {
                    onChange {
                        console.log("selection switched by checkbox of $engine to ${it.value}")
                        if (it.value) {
                            state.selection += engine
                        } else {
                            state.selection -= engine
                        }
                    }
                    checked(state.selection.contains(engine))
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
public fun ClosableSearchEngineButton(
    searchEngine: SearchEngine,
    onClose: () -> Unit = { console.log("onClose($searchEngine)") },
) {
    BasicButton({
        classes("animated", "fade")
        v.size(Tiny).compact()
        classes("icon")
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
