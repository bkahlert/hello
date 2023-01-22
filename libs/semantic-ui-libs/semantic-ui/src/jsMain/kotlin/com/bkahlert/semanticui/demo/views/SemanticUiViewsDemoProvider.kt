package com.bkahlert.semanticui.demo.views

import com.bkahlert.semanticui.demo.Column
import com.bkahlert.semanticui.demo.DemoProvider
import com.bkahlert.semanticui.demo.Grid
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

public val SemanticUiViewsDemoProvider: DemoProvider = DemoProvider("semantic-ui-views", "Semantic UI—Views") {
    Grid {
        Column {
            P { Text("—") }
        }
        Column {
            P { Text("—") }
        }
    }
}
