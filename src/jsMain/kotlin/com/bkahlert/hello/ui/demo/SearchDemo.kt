package com.bkahlert.hello.ui.demo

import androidx.compose.runtime.Composable
import com.bkahlert.hello.search.MultiSearchInput
import com.bkahlert.hello.search.PasteHandlingMultiSearchInput
import com.bkahlert.hello.search.SearchEngine
import com.bkahlert.hello.search.SearchEngine.Bing
import com.bkahlert.hello.search.SearchEngine.Google
import com.bkahlert.hello.search.SearchEngineDropdown
import com.bkahlert.hello.search.SearchEngineSelect
import com.bkahlert.hello.search.SearchInput
import com.bkahlert.hello.search.SearchThing
import com.bkahlert.hello.search.rememberMultiSearchInputState
import com.bkahlert.hello.search.rememberSearchEngineSelectState

@Composable
fun SearchDemo() {
    Demos("Search Engine Dropdown") {
        Demo("Empty") {
            SearchEngineDropdown(rememberSearchEngineSelectState { false })
        }
        Demo("Default") {
            SearchEngineDropdown()
        }
        Demo("Pre-selected") {
            SearchEngineDropdown(rememberSearchEngineSelectState(engines = SearchEngine.values().take(10).toTypedArray()) { listOf(Google, Bing).contains(it) })
        }
        Demo("All") {
            SearchEngineDropdown(rememberSearchEngineSelectState { true })
        }
    }
    Demos("Search Engine Select") {
        Demo("Empty") {
            SearchEngineSelect(rememberSearchEngineSelectState { false })
        }
        Demo("Default") {
            SearchEngineSelect()
        }
        Demo("Pre-selected") {
            SearchEngineSelect(rememberSearchEngineSelectState(engines = SearchEngine.values().take(10).toTypedArray()) { listOf(Google, Bing).contains(it) })
        }
        Demo("All") {
            SearchEngineSelect(rememberSearchEngineSelectState { true })
        }
    }
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
    if (false) {
        Demos("Search") {
            Demo("not empty + full search + focus") {
                SearchThing(searchEngine = SearchEngine.Default, allAtOnce = true, query = "all engines (focussed)")
            }
            Demo("not empty + full search") {
                SearchThing(searchEngine = SearchEngine.Default, allAtOnce = true, query = "all engines")
            }
            Demo("not empty") {
                SearchThing(searchEngine = SearchEngine.Default, query = "single engine")
            }
            Demo("empty + full search") {
                SearchThing(searchEngine = SearchEngine.Default, allAtOnce = true)
            }
            Demo("empty") {
                SearchThing(searchEngine = SearchEngine.Default, allAtOnce = false)
            }
        }
    }
}
