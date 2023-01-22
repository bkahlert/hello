package com.bkahlert.semanticui.demo.custom

import com.bkahlert.semanticui.demo.Column
import com.bkahlert.semanticui.demo.DemoProvider
import com.bkahlert.semanticui.demo.Grid

public val SemanticUiCustomDemoProvider: DemoProvider = DemoProvider("semantic-ui-custom", "Semantic UI—Custom") {
    Grid {
        Column {
            DemoDemos()
        }
        Column {
            ColorsDemos()
            DimmingLoaderDemos()
            ErrorMessageDemos()
            OptionsDemos()
        }
    }
}
