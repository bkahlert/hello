package com.bkahlert.hello.search.demos

import androidx.compose.runtime.Composable
import com.bkahlert.hello.search.MultiSearchInput
import com.bkahlert.hello.search.PasteHandlingMultiSearchInput
import com.bkahlert.hello.search.SearchEngine
import com.bkahlert.hello.search.SearchEngine.Bing
import com.bkahlert.hello.search.SearchEngine.Google
import com.bkahlert.hello.search.SearchInput
import com.bkahlert.hello.search.rememberMultiSearchInputState
import com.bkahlert.hello.search.rememberSearchEngineSelectState
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.Demos

@Composable
public fun SearchInputDemos() {
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
