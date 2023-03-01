package com.bkahlert.semanticui.demo.collections

import com.bkahlert.semanticui.core.SemanticUiLogo
import com.bkahlert.semanticui.demo.DemoProvider

public val SemanticUiCollectionsDemoProvider: DemoProvider = DemoProvider(
    id = "semantic-ui-collections",
    name = "Collections",
    logo = SemanticUiLogo,
    {
        MessageDemos()
    },
)
