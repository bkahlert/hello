package com.bkahlert.hello.debug.search

import androidx.compose.runtime.Composable
import com.bkahlert.hello.ui.search.SearchEngine
import com.bkahlert.hello.ui.search.SearchEngine.Bing
import com.bkahlert.hello.ui.search.SearchEngine.Google
import com.bkahlert.hello.ui.search.SearchEngineSelect
import com.bkahlert.hello.ui.search.rememberSearchEngineSelectState
import com.bkahlert.semanticui.custom.Demo
import com.bkahlert.semanticui.custom.Demos

@Composable
fun SearchEngineSelectDemos() {
    Demos("Search Engine Select") {
        Demo("Empty") {
            SearchEngineSelect(rememberSearchEngineSelectState(selected = { false }))
        }
        Demo("Default") {
            SearchEngineSelect()
        }
        Demo("Pre-selected") {
            SearchEngineSelect(rememberSearchEngineSelectState(engines = SearchEngine.values().take(10).toTypedArray(), selected = {
                listOf(Google, Bing).contains(it)
            }))
        }
        Demo("All") {
            SearchEngineSelect(rememberSearchEngineSelectState(selected = { true }))
        }
    }
}
