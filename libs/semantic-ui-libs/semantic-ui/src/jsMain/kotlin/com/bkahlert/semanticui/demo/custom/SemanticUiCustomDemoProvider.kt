package com.bkahlert.semanticui.demo.custom

import com.bkahlert.semanticui.demo.DemoProvider

public val SemanticUiCustomDemoProvider: DemoProvider = DemoProvider(
    id = "semantic-ui-custom",
    name = "Custom",
    logo = "https://semantic-ui.com/images/logo.png",
    {
        DemoDemos()
        DemoViewDemos()
    },
    {
        ColorsDemos()
        DimmingLoaderDemos()
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
