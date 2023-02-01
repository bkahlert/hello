package com.bkahlert.hello.search.demos

import com.bkahlert.semanticui.demo.Column
import com.bkahlert.semanticui.demo.DemoProvider
import com.bkahlert.semanticui.demo.Grid

public val SearchDemos: DemoProvider = DemoProvider("hello-search", "Search") {
    Grid {
        Column {
            SearchInputDemos()
        }
        Column {
            SearchEngineSelectDemos()
            SearchEngineDropdownDemos()
        }
    }
}
