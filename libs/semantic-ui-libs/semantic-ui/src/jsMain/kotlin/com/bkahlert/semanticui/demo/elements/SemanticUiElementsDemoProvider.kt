package com.bkahlert.semanticui.demo.elements

import com.bkahlert.semanticui.demo.Column
import com.bkahlert.semanticui.demo.DemoProvider
import com.bkahlert.semanticui.demo.Grid

public val SemanticUiElementsDemoProvider: DemoProvider = DemoProvider("semantic-ui-elements", "Semantic UI—Elements") {
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
