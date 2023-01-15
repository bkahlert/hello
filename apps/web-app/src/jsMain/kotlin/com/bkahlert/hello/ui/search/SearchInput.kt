package com.bkahlert.hello.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.semanticui.core.SemanticUI
import com.bkahlert.hello.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.hello.semanticui.element.Icon
import com.bkahlert.hello.semanticui.element.Input
import com.bkahlert.hello.semanticui.element.InputElement
import com.bkahlert.hello.semanticui.element.fluid
import com.bkahlert.hello.semanticui.element.icon
import com.bkahlert.hello.url.URL
import com.bkahlert.hello.url.toURLOrNull
import com.bkahlert.kommons.dom.openInNewTab
import com.bkahlert.kommons.dom.openInSameTab
import io.ktor.http.Url
import kotlinx.browser.window
import org.jetbrains.compose.web.attributes.InputType.Search
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.maxWidth
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.position
import org.jetbrains.compose.web.css.right
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input

@Stable
interface SearchInputState {
    var query: String
}

class SearchInputStateImpl(
    query: String = "",
) : SearchInputState {
    override var query by mutableStateOf(query)
}

@Composable
fun rememberSearchInputState(
    query: String = "",
): SearchInputState = remember(query) { SearchInputStateImpl(query) }

@Composable
fun SearchInput(
    state: SearchInputState = rememberSearchInputState(),
    attrs: SemanticAttrBuilderContext<InputElement>? = null,
    onSearch: ((String) -> Unit)? = { console.info("onSearch($it)") },
    onPaste: (((String) -> String?) -> Unit)? = { console.log("onPaste(${it("text/plain")})") },
    content: SemanticContentBuilder<InputElement>? = null,
) {
    SemanticUI("search") {
        Input({
            attrs?.invoke(this)
            +Position.Left + icon
        }) {
            Icon("search")
            Input(Search) {
                classes("prompt")
                placeholder("Search...")
                value(state.query)

                onKeyDown { event ->
                    when (event.code) {
                        "Enter", "NumpadEnter" -> onSearch?.invoke(state.query)
                    }
                }
                onInput { event -> state.query = event.value }
                onPaste { event -> onPaste?.invoke { event.getData(it) } }
            }
            content?.invoke(this)
        }
    }
}


@Stable
interface MultiSearchInputState : SearchInputState, SearchEngineSelectState {
    fun prev() {
        selection = selection.map { it.prev(values) }
    }

    fun next() {
        selection = selection.map { it.next(values) }
    }
}

class MultiSearchInputStateImpl(
    searchInputState: SearchInputState,
    searchEngineSelectState: SearchEngineSelectState,
) : MultiSearchInputState, SearchInputState by searchInputState, SearchEngineSelectState by searchEngineSelectState

@Composable
fun rememberMultiSearchInputState(
    searchInputState: SearchInputState = rememberSearchInputState(),
    searchEngineSelectState: SearchEngineSelectState = rememberSearchEngineSelectState(),
): MultiSearchInputState = remember(searchInputState, searchEngineSelectState) {
    MultiSearchInputStateImpl(
        searchInputState = searchInputState,
        searchEngineSelectState = searchEngineSelectState,
    )
}

@Composable
fun MultiSearchInput(
    state: MultiSearchInputState = rememberMultiSearchInputState(),
    attrs: SemanticAttrBuilderContext<InputElement>? = null,
    onSearch: ((String, List<SearchEngine>) -> Unit)? = { query, engines -> console.log("onSearch($query; $engines)") },
    onPaste: (((String) -> String?) -> Unit)? = { console.log("onPaste(${it("text/plain")})") },
    content: SemanticContentBuilder<InputElement>? = null,
) {
    SearchInput(
        state, {
            attrs?.invoke(this)
            +fluid
            title("Press ↑ or ↓ to switch the search engine")
            onKeyDown { event ->
                when (event.code) {
                    "ArrowUp" -> {
                        if (!event.defaultPrevented) state.prev()
                    }

                    "ArrowDown" -> if (!event.defaultPrevented) state.next()
                    "OSLeft", "OSRight" -> if (!event.defaultPrevented) state.allValues = true
                }
            }
            onKeyUp { event ->
                when (event.code) {
                    "OSLeft", "OSRight" -> if (!event.defaultPrevented) state.allValues = false
                }
            }
            onBlur {
                // TODO
                state.allValues = false
            }
        },
        onSearch = { query -> onSearch?.invoke(query, state.selection) },
        onPaste = onPaste
    ) {
        content?.invoke(this)

        Div({
            style {
                position(Position.Absolute)
                right(0.em)
                padding(0.em, 1.em, 0.em, 1.em)
                maxWidth(50.percent)
                height(100.percent)
                display(DisplayStyle.Flex)
                alignItems(AlignItems.Center)
            }
        }) {
            SearchEngineSelect(state) { +Size.Mini }
        }
    }
}

@Composable
fun PasteHandlingMultiSearchInput(
    onSearch: (String, List<URL>) -> Unit = { _, urls ->
        if (urls.size == 1) window.openInSameTab(org.w3c.dom.url.URL(urls.first().toString()))
        else urls.forEach { window.openInNewTab(org.w3c.dom.url.URL(it.toString())) }
    },
) {
    MultiSearchInput(
        onSearch = { query, engines: List<SearchEngine> ->
            onSearch(query, engines.map { it.url(query) })
        },
        onPaste = {
            it("text/plain")?.also {
                val url = it.toURLOrNull()
                if (url != null && Url(url.toString()).port != 0) {
                    window.openInSameTab(org.w3c.dom.url.URL(url.toString()))
                }
            }
        },
    )
}
