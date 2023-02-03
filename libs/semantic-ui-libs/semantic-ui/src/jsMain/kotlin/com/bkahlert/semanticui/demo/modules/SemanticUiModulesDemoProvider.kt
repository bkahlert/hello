package com.bkahlert.semanticui.demo.modules

import com.bkahlert.semanticui.demo.Column
import com.bkahlert.semanticui.demo.DemoProvider
import com.bkahlert.semanticui.demo.Grid

public val SemanticUiModulesDemoProvider: DemoProvider = DemoProvider(
    id = "semantic-ui-modules",
    name = "Modules",
    logo = "https://semantic-ui.com/images/logo.png",
) {
    Grid {
        Column {
            CheckboxDemos()
        }
        Column {
            DimmerDemos()
        }
    }
}
