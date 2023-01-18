package com.bkahlert.hello.clickup.view

import androidx.compose.runtime.Composable
import com.bkahlert.hello.clickup.model.fixtures.ClickUpTestClient
import com.bkahlert.hello.semanticui.core.dom.SemanticElement
import com.bkahlert.hello.semanticui.core.dom.SemanticElementScope
import com.bkahlert.hello.semanticui.custom.Configurer
import com.bkahlert.hello.semanticui.element.Button
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement

public class ClickUpTestClientConfigurer(
    override val name: String = "Demo Access",
    override val icon: Array<String> = arrayOf("grey", "key"),
    private val provide: () -> ClickUpTestClient = { ClickUpTestClient() },
) : Configurer<ClickUpTestClient> {
    @Composable override fun SemanticElementScope<SemanticElement<HTMLDivElement>>.Content(
        onComplete: (ClickUpTestClient) -> Unit,
    ) {
        Button({
            +Emphasis.Primary + Inverted
            onClick {
                onComplete(provide())
            }
        }) { Text("Try out") }
    }
}
