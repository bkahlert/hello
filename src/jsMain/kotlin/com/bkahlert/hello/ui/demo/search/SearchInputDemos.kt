package com.bkahlert.hello.ui.demo.search

import androidx.compose.runtime.Composable
import com.bkahlert.hello.search.MultiSearchInput
import com.bkahlert.hello.search.PasteHandlingMultiSearchInput
import com.bkahlert.hello.search.SearchEngine
import com.bkahlert.hello.search.SearchEngine.Bing
import com.bkahlert.hello.search.SearchEngine.Google
import com.bkahlert.hello.search.SearchInput
import com.bkahlert.hello.search.rememberMultiSearchInputState
import com.bkahlert.hello.search.rememberSearchEngineSelectState
import com.bkahlert.hello.ui.demo.Demo
import com.bkahlert.hello.ui.demo.Demos

@Composable
fun SearchInputDemos() {
    Demos("Search Input") {
        Demo("Plain") {
            SearchInput()
        }
        Demo("Multi: Empty") {
            MultiSearchInput(rememberMultiSearchInputState(searchEngineSelectState = rememberSearchEngineSelectState { false }))
        }
        Demo("Multi: Default") {
            MultiSearchInput()
        }
        Demo("Multi: Pre-selected") {
            MultiSearchInput(rememberMultiSearchInputState(
                searchEngineSelectState = rememberSearchEngineSelectState(
                    engines = SearchEngine.values().take(10).toTypedArray()) { listOf(Google, Bing).contains(it) },
            ))
        }
        Demo("Multi: All") {
            MultiSearchInput(rememberMultiSearchInputState(searchEngineSelectState = rememberSearchEngineSelectState { true }))
        }
        Demo("Paste-Handling") {
            PasteHandlingMultiSearchInput()
        }
    }
}
