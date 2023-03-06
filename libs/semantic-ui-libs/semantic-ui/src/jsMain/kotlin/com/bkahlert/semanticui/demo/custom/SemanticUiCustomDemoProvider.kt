package com.bkahlert.semanticui.demo.custom

import com.bkahlert.semanticui.core.SemanticUiLogo
import com.bkahlert.semanticui.demo.DemoProvider

public val SemanticUiCustomDemoProvider: DemoProvider = DemoProvider(
    id = "semantic-ui-custom",
    name = "Custom",
    logo = SemanticUiLogo,
    {
        DemoDemos()
        DemoViewDemos()
    },
    {
        ColorsDemos()
        ErrorMessageModalDemos()
        ErrorMessageDemos()
    },
    {
        IFrameDemos()
        LoadingStateDemos()
        OptionsDemos()
        TabMenuDemos()
    },
)
