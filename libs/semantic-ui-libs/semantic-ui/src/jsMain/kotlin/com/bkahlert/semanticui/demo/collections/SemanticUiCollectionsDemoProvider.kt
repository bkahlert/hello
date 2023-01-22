package com.bkahlert.semanticui.demo.collections

import com.bkahlert.semanticui.demo.Column
import com.bkahlert.semanticui.demo.DemoProvider
import com.bkahlert.semanticui.demo.Grid
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

public val SemanticUiCollectionsDemoProvider: DemoProvider = DemoProvider("semantic-ui-collections", "Semantic UI—Collections") {
    Grid {
        Column {
            MessageDemos()
        }
        Column {
            P { Text("—") }
        }
    }
}
