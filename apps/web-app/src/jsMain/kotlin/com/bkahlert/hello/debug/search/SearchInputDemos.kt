package com.bkahlert.hello.debug.search

import androidx.compose.runtime.Composable
import com.bkahlert.hello.ui.search.MultiSearchInput
import com.bkahlert.hello.ui.search.PasteHandlingMultiSearchInput
import com.bkahlert.hello.ui.search.SearchEngine
import com.bkahlert.hello.ui.search.SearchEngine.Bing
import com.bkahlert.hello.ui.search.SearchEngine.Google
import com.bkahlert.hello.ui.search.SearchInput
import com.bkahlert.hello.ui.search.rememberMultiSearchInputState
import com.bkahlert.hello.ui.search.rememberSearchEngineSelectState
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.Demos

@Composable
fun SearchInputDemos() {
    Demos("Search Input") {
        Demo("Plain") {
            SearchInput()
        }
        Demo("Multi: Empty") {
            MultiSearchInput(rememberMultiSearchInputState(searchEngineSelectState = rememberSearchEngineSelectState(selected = { false })))
        }
        Demo("Multi: Default") {
            MultiSearchInput()
        }
        Demo("Multi: Pre-selected") {
            MultiSearchInput(
                rememberMultiSearchInputState(
                    searchEngineSelectState = rememberSearchEngineSelectState(
                        engines = SearchEngine.values().take(10).toTypedArray(), selected = { listOf(Google, Bing).contains(it) }),
                )
            )
        }
        Demo("Multi: All") {
            MultiSearchInput(rememberMultiSearchInputState(searchEngineSelectState = rememberSearchEngineSelectState(selected = { true })))
        }
        Demo("Paste-Handling") {
            PasteHandlingMultiSearchInput()
        }
    }
}
