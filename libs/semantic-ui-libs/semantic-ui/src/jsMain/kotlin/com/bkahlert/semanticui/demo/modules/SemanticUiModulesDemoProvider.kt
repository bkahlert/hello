package com.bkahlert.semanticui.demo.modules

import com.bkahlert.semanticui.core.SemanticUiLogo
import com.bkahlert.semanticui.demo.DemoProvider

public val SemanticUiModulesDemoProvider: DemoProvider = DemoProvider(
    id = "semantic-ui-modules",
    name = "Modules",
    logo = SemanticUiLogo,
    {
        CheckboxDemos()
    },
    {
        DimmerDemos()
    },
)
