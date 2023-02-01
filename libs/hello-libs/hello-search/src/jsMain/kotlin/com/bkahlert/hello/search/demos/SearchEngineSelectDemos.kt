package com.bkahlert.hello.search.demos

import androidx.compose.runtime.Composable
import com.bkahlert.hello.search.SearchEngine
import com.bkahlert.hello.search.SearchEngine.Bing
import com.bkahlert.hello.search.SearchEngine.Google
import com.bkahlert.hello.search.SearchEngineSelect
import com.bkahlert.hello.search.rememberSearchEngineSelectState
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.Demos

@Composable
public fun SearchEngineSelectDemos() {
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
