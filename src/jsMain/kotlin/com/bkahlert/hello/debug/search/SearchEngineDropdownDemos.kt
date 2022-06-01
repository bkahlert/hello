package com.bkahlert.hello.debug.search

import androidx.compose.runtime.Composable
import com.bkahlert.hello.debug.Demo
import com.bkahlert.hello.debug.Demos
import com.bkahlert.hello.ui.search.SearchEngine
import com.bkahlert.hello.ui.search.SearchEngine.Bing
import com.bkahlert.hello.ui.search.SearchEngine.Google
import com.bkahlert.hello.ui.search.SearchEngineDropdown
import com.bkahlert.hello.ui.search.rememberSearchEngineSelectState

@Composable
fun SearchEngineDropdownDemos() {
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
