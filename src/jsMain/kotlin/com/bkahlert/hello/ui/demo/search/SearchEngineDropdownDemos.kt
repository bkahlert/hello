package com.bkahlert.hello.ui.demo.search

import androidx.compose.runtime.Composable
import com.bkahlert.hello.search.SearchEngine
import com.bkahlert.hello.search.SearchEngine.Bing
import com.bkahlert.hello.search.SearchEngine.Google
import com.bkahlert.hello.search.SearchEngineDropdown
import com.bkahlert.hello.search.rememberSearchEngineSelectState
import com.bkahlert.hello.ui.demo.Demo
import com.bkahlert.hello.ui.demo.Demos

@Composable
fun SearchEngineDropdownDemos() {
    Demos("Search Engine Dropdown") {
        Demo("Empty") {
            SearchEngineDropdown(rememberSearchEngineSelectState { false })
        }
        Demo("Default") {
            SearchEngineDropdown()
        }
        Demo("Pre-selected") {
            SearchEngineDropdown(rememberSearchEngineSelectState(engines = SearchEngine.values().take(10).toTypedArray()) {
                listOf(Google,
                    Bing).contains(it)
            })
        }
        Demo("All") {
            SearchEngineDropdown(rememberSearchEngineSelectState { true })
        }
    }
}
