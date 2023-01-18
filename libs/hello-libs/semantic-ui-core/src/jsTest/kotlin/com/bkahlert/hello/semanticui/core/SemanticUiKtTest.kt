@file:Suppress("LongLine")

package com.bkahlert.hello.semanticui.core

import com.bkahlert.hello.semanticui.test.JQueryLibrary
import com.bkahlert.hello.semanticui.test.SemanticUiLibrary
import com.bkahlert.hello.semanticui.test.compositionWith
import com.bkahlert.hello.semanticui.test.root
import io.kotest.matchers.shouldBe
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.testutils.runTest
import kotlin.test.Test

class SemanticUiKtTest {

    @Test
    fun semantic() = runTest {
        compositionWith(JQueryLibrary, SemanticUiLibrary) {
            Semantic("foo", "bar") {
                Text("baz")
            }
        }
        root { it.innerHTML shouldBe "<div class=\"foo bar\">baz</div>" }
    }
}

/*
com.bkahlert.hello.semanticui.custom/Configurer.content.<get-content>|-5885839151301060177[0]
Existed declaration
 FUN IR_EXTERNAL_DECLARATION_STUB name:<get-content> visibility:public modality:ABSTRACT <> (
    $this:com.bkahlert.hello.semanticui.custom.Configurer<T of com.bkahlert.hello.semanticui.custom.Configurer>)
        returnType:@[Composable] @[ExtensionFunctionType] kotlin.Function2<com.bkahlert.hello.semanticui.core.dom.SemanticElementScope<com.bkahlert.hello.semanticui.core.dom.SemanticElement<org.w3c.dom.HTMLDivElement>>, @[ParameterName(name = 'onComplete')] kotlin.Function1<T of com.bkahlert.hello.semanticui.custom.Configurer, kotlin.Unit>, kotlin.Unit>
clashed with new
 FUN IR_EXTERNAL_DECLARATION_STUB name:<get-content> visibility:public modality:ABSTRACT <> (
    $this:com.bkahlert.hello.semanticui.custom.Configurer<T of com.bkahlert.hello.semanticui.custom.Configurer>)
        returnType:@[ExtensionFunctionType] kotlin.Function4<com.bkahlert.hello.semanticui.core.dom.SemanticElementScope<com.bkahlert.hello.semanticui.core.dom.SemanticElement<org.w3c.dom.HTMLDivElement>>, @[ParameterName(name = 'onComplete')] kotlin.Function1<T of com.bkahlert.hello.semanticui.custom.Configurer, kotlin.Unit>, androidx.compose.runtime.Composer, kotlin.Int, kotlin.Unit
 */
