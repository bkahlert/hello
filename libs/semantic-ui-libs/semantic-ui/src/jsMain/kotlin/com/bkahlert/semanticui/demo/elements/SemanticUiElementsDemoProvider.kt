package com.bkahlert.semanticui.demo.elements

import com.bkahlert.semanticui.demo.Column
import com.bkahlert.semanticui.demo.DemoProvider
import com.bkahlert.semanticui.demo.Grid

public val SemanticUiElementsDemoProvider: DemoProvider = DemoProvider(
    id = "semantic-ui-elements",
    name = "Elements",
    logo = "https://semantic-ui.com/images/logo.png",
) {
    Grid {
        Column {
            ButtonDemos()
            ImageDemos()
            InputDemos()
            ListDemos()
        }
        Column {
            LoaderDemos()
            PlaceholderDemos()
            SegmentDemos()
        }
    }
}
