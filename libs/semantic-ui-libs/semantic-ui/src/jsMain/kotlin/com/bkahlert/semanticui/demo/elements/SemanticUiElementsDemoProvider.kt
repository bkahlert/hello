package com.bkahlert.semanticui.demo.elements

import com.bkahlert.semanticui.core.SemanticUiLogo
import com.bkahlert.semanticui.demo.DemoProvider

public val SemanticUiElementsDemoProvider: DemoProvider = DemoProvider(
    id = "semantic-ui-elements",
    name = "Elements",
    logo = SemanticUiLogo,
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
