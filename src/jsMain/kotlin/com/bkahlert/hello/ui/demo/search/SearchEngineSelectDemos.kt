package com.bkahlert.hello.ui.demo.search

import androidx.compose.runtime.Composable
import com.bkahlert.hello.search.SearchEngine
import com.bkahlert.hello.search.SearchEngine.Bing
import com.bkahlert.hello.search.SearchEngine.Google
import com.bkahlert.hello.search.SearchEngineSelect
import com.bkahlert.hello.search.rememberSearchEngineSelectState
import com.bkahlert.hello.ui.demo.Demo
import com.bkahlert.hello.ui.demo.Demos

@Composable
fun SearchEngineSelectDemos() {
    Demos("Search Engine Select") {
        Demo("Empty") {
            SearchEngineSelect(rememberSearchEngineSelectState { false })
        }
        Demo("Default") {
            SearchEngineSelect()
        }
        Demo("Pre-selected") {
            SearchEngineSelect(rememberSearchEngineSelectState(engines = SearchEngine.values().take(10).toTypedArray()) {
                listOf(Google,
                    Bing).contains(it)
            })
        }
        Demo("All") {
            SearchEngineSelect(rememberSearchEngineSelectState { true })
        }
    }
}
