package com.bkahlert.hello.search.demos

import androidx.compose.runtime.Composable
import com.bkahlert.hello.search.SearchEngine
import com.bkahlert.hello.search.SearchEngine.Bing
import com.bkahlert.hello.search.SearchEngine.Google
import com.bkahlert.hello.search.SearchEngineDropdown
import com.bkahlert.hello.search.rememberSearchEngineSelectState
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.Demos

@Composable
public fun SearchEngineDropdownDemos() {
    Demos("Search Engine Dropdown") {
        Demo("Empty") {
            SearchEngineDropdown(rememberSearchEngineSelectState(selected = { false }))
        }
        Demo("Default") {
            SearchEngineDropdown()
        }
        Demo("Pre-selected") {
            SearchEngineDropdown(rememberSearchEngineSelectState(engines = SearchEngine.values().take(10).toTypedArray(), selected = {
                listOf(Google, Bing).contains(it)
            }))
        }
        Demo("All") {
            SearchEngineDropdown(rememberSearchEngineSelectState(selected = { true }))
        }
    }
}
