package com.bkahlert.hello.debug.search

import com.bkahlert.semanticui.demo.Column
import com.bkahlert.semanticui.demo.DemoProvider
import com.bkahlert.semanticui.demo.Grid

val SearchDemos: DemoProvider = DemoProvider("multi-engine-search", "Search") {
    Grid {
        Column {
            SearchEngineDropdownDemos()
        }
        Column {
            SearchEngineSelectDemos()
        }
        Column {
            SearchInputDemos()
        }
    }
}
