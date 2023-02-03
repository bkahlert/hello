package com.bkahlert.hello.search.demos

import com.bkahlert.semanticui.demo.DemoProvider

public val SearchDemoProvider: DemoProvider = DemoProvider(
    id = "hello-search",
    name = "Search",
    {
        SearchInputDemos()
    },
    {
        SearchEngineSelectDemos()
        SearchEngineDropdownDemos()
    },
)
