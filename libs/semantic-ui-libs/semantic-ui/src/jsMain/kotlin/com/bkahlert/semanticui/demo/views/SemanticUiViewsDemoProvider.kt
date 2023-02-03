package com.bkahlert.semanticui.demo.views

import com.bkahlert.semanticui.demo.Column
import com.bkahlert.semanticui.demo.DemoProvider
import com.bkahlert.semanticui.demo.Grid
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

public val SemanticUiViewsDemoProvider: DemoProvider = DemoProvider(
    id = "semantic-ui-views",
    name = "Views",
    logo = "https://semantic-ui.com/images/logo.png",
) {
    Grid {
        Column {
            P { Text("—") }
        }
        Column {
            P { Text("—") }
        }
    }
}
