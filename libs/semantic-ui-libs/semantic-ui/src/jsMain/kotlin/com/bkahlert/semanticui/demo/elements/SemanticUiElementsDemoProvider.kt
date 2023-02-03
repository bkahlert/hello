package com.bkahlert.semanticui.demo.elements

import com.bkahlert.semanticui.demo.DemoProvider

public val SemanticUiElementsDemoProvider: DemoProvider = DemoProvider(
    id = "semantic-ui-elements",
    name = "Elements",
    logo = "https://semantic-ui.com/images/logo.png",
    {
        ButtonDemos()
        ImageDemos()
        InputDemos()
        ListDemos()
    },
    {
        LoaderDemos()
        PlaceholderDemos()
        SegmentDemos()
    },
)
