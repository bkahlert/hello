package com.bkahlert.semanticui.demo.custom

import com.bkahlert.semanticui.demo.Column
import com.bkahlert.semanticui.demo.DemoProvider
import com.bkahlert.semanticui.demo.Grid

public val SemanticUiCustomDemoProvider: DemoProvider = DemoProvider(
    id = "semantic-ui-custom",
    name = "Custom",
    logo = "https://semantic-ui.com/images/logo.png",
) {
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
