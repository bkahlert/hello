package com.bkahlert.semanticui.demo.collections

import com.bkahlert.semanticui.demo.DemoProvider

public val SemanticUiCollectionsDemoProvider: DemoProvider = DemoProvider(
    id = "semantic-ui-collections",
    name = "Collections",
    logo = "https://semantic-ui.com/images/logo.png",
    {
        MessageDemos()
    },
)
