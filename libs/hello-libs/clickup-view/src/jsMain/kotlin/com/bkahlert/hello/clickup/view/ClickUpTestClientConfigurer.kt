package com.bkahlert.hello.clickup.view

import androidx.compose.runtime.Composable
import com.bkahlert.hello.clickup.model.fixtures.ClickUpTestClient
import com.bkahlert.semanticui.core.dom.SemanticElement
import com.bkahlert.semanticui.core.dom.SemanticElementScope
import com.bkahlert.semanticui.element.Button
import com.bkahlert.semanticui.element.primary
import com.bkahlert.semanticui.module.inverted
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement

public class ClickUpTestClientConfigurer(
    override val name: String = "Demo Access",
    override val icon: Array<String> = arrayOf("grey", "key"),
    private val provide: () -> ClickUpTestClient = { ClickUpTestClient() },
) : Configurer<ClickUpTestClient> {
    override val content: @Composable SemanticElementScope<SemanticElement<HTMLDivElement>>.(onComplete: (ClickUpTestClient) -> Unit) -> Unit = { onComplete ->
        Button({
            v.primary().inverted()
            onClick {
                onComplete(provide())
            }
        }) { Text("Try out") }
    }
}
