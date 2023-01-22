package com.bkahlert.semanticui.demo

import com.bkahlert.semanticui.demo.collections.SemanticUiCollectionsDemoProvider
import com.bkahlert.semanticui.demo.custom.SemanticUiCustomDemoProvider
import com.bkahlert.semanticui.demo.elements.SemanticUiElementsDemoProvider
import com.bkahlert.semanticui.demo.modules.SemanticUiModulesDemoProvider
import com.bkahlert.semanticui.demo.views.SemanticUiViewsDemoProvider

public val SemanticUiDemoProviders: Array<DemoProvider> = arrayOf(
    SemanticUiElementsDemoProvider,
    SemanticUiCollectionsDemoProvider,
    SemanticUiModulesDemoProvider,
    SemanticUiViewsDemoProvider,
    SemanticUiCustomDemoProvider,
)
