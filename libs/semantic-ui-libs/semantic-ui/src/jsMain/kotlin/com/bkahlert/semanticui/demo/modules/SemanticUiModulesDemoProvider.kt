package com.bkahlert.semanticui.demo.modules

import com.bkahlert.semanticui.demo.DemoProvider

public val SemanticUiModulesDemoProvider: DemoProvider = DemoProvider(
    id = "semantic-ui-modules",
    name = "Modules",
    logo = "https://semantic-ui.com/images/logo.png",
    {
        CheckboxDemos()
    },
    {
        DimmerDemos()
    },
)
