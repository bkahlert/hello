package com.bkahlert.semanticui.demo.modules

import com.bkahlert.semanticui.demo.Column
import com.bkahlert.semanticui.demo.DemoProvider
import com.bkahlert.semanticui.demo.Grid

public val SemanticUiModulesDemoProvider: DemoProvider = DemoProvider("semantic-ui-modules", "Semantic UI—Modules") {
    Grid {
        Column {
            CheckboxDemos()
        }
        Column {
            DimmerDemos()
        }
    }
}
