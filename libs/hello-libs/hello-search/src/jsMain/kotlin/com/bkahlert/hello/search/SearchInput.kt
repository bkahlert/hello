package com.bkahlert.hello.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.kommons.dom.openInNewTab
import com.bkahlert.kommons.dom.openInSameTab
import com.bkahlert.kommons.js.console
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.port
import com.bkahlert.kommons.uri.toUriOrNull
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.attributes.Modifier
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Mini
import com.bkahlert.semanticui.core.attributes.raw
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.Input
import com.bkahlert.semanticui.element.InputElement
import com.bkahlert.semanticui.element.fluid
import com.bkahlert.semanticui.element.icon
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
public interface SearchInputState {
    public var query: String
}

public class SearchInputStateImpl(
    query: String = "",
) : SearchInputState {
    override var query: String by mutableStateOf(query)
}

@Composable
public fun rememberSearchInputState(
    query: String = "",
): SearchInputState = remember(query) { SearchInputStateImpl(query) }

@Composable
public fun SearchInput(
    state: SearchInputState = rememberSearchInputState(),
    attrs: SemanticAttrBuilderContext<InputElement>? = null,
    onSearch: ((String) -> Unit)? = { console.debug("SearchInput: onSearch($it)") },
    onPaste: (((String) -> String?) -> Unit)? = { console.debug("SearchInput: onPaste(${it("text/plain")})") },
    content: SemanticContentBuilder<InputElement>? = null,
) {
    S("ui", "search") {
        Input({
            attrs?.invoke(this)
            v.icon(Modifier.Variation.Icon.Left)
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
public interface MultiSearchInputState : SearchInputState, SearchEngineSelectState {
    public fun prev() {
        selection = selection.map { it.prev(values) }
    }

    public fun next() {
        selection = selection.map { it.next(values) }
    }
}

public class MultiSearchInputStateImpl(
    searchInputState: SearchInputState,
    searchEngineSelectState: SearchEngineSelectState,
) : MultiSearchInputState, SearchInputState by searchInputState, SearchEngineSelectState by searchEngineSelectState

@Composable
public fun rememberMultiSearchInputState(
    searchInputState: SearchInputState = rememberSearchInputState(),
    searchEngineSelectState: SearchEngineSelectState = rememberSearchEngineSelectState(),
): MultiSearchInputState = remember(searchInputState, searchEngineSelectState) {
    MultiSearchInputStateImpl(
        searchInputState = searchInputState,
        searchEngineSelectState = searchEngineSelectState,
    )
}

@Composable
public fun MultiSearchInput(
    state: MultiSearchInputState = rememberMultiSearchInputState(),
    attrs: SemanticAttrBuilderContext<InputElement>? = null,
    onSearch: ((String, List<SearchEngine>) -> Unit)? = { query, engines -> console.debug("MultiSearchInput: onSearch($query; $engines)") },
    onPaste: (((String) -> String?) -> Unit)? = { console.debug("MultiSearchInput: onPaste(${it("text/plain")})") },
    content: SemanticContentBuilder<InputElement>? = null,
) {
    SearchInput(
        state, {
            attrs?.invoke(this)
            v.fluid()
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
            SearchEngineSelect(state) { raw(Mini) }
        }
    }
}

@Composable
public fun PasteHandlingMultiSearchInput(
    onSearch: (String, List<Uri>) -> Unit = { _, urls ->
        if (urls.size == 1) window.openInSameTab(urls.first())
        else urls.forEach { window.openInNewTab(it) }
    },
) {
    MultiSearchInput(
        onSearch = { query, engines: List<SearchEngine> ->
            onSearch(query, engines.map { it.url(query) })
        },
        onPaste = {
            it("text/plain")?.also {
                val uri = it.toUriOrNull()
                if (uri != null && uri.port != 0) {
                    window.openInSameTab(uri)
                }
            }
        },
    )
}
