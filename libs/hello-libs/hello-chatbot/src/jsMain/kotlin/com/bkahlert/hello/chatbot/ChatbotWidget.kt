package com.bkahlert.hello.chatbot

import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.bkahlert.hello.fritz2.lens
import com.bkahlert.hello.fritz2.orEmpty
import com.bkahlert.hello.widget.AspectRatio
import com.bkahlert.hello.widget.Widget
import com.bkahlert.hello.widget.WidgetEditor
import com.bkahlert.hello.widget.panel
import com.bkahlert.kommons.uri.Uri
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.Lens
import dev.fritz2.core.Tag
import kotlinx.serialization.Serializable
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement

@Serializable
public data class ChatbotWidget(
    override val id: String,
    override val title: String? = null,
    val token: String? = null,
    val model: String? = null,
    val logLevel: LogLevel? = LogLevel.None,
    val user: String? = null,
) : Widget {
    override val icon: Uri get() = Chatbot.ChatGPT.logo

    override fun render(renderContext: Tag<Element>): HtmlTag<HTMLDivElement> = renderContext.panel(AspectRatio.stretch) {
        val missing = listOf(::token).filter { it.get().isNullOrBlank() }
        if (missing.isNotEmpty()) {
            renderConfigurationMissing(missing)
        } else {
            chat(ChatbotSession(
                api = logLevel?.let { OpenAI(token = token.orEmpty(), logging = LoggingConfig(it)) } ?: OpenAI(token = token.orEmpty()),
                model = model?.let { ModelId(it) },
                user = user,
            )
            )
        }
    }

    override fun editor(isNew: Boolean): WidgetEditor<*> = ChatbotWidgetEditor(isNew, this)

    public companion object {
        public fun title(): Lens<ChatbotWidget, String> =
            ChatbotWidget::title.lens({ it.title }) { p, v -> p.copy(title = v) }.orEmpty()

        public fun token(): Lens<ChatbotWidget, String> =
            ChatbotWidget::token.lens({ it.token }, { p, v -> p.copy(token = v) }).orEmpty()

        public fun model(): Lens<ChatbotWidget, String> =
            ChatbotWidget::model.lens({ it.model }, { p, v -> p.copy(model = v) }).orEmpty()

        public fun logLevel(): Lens<ChatbotWidget, LogLevel?> =
            ChatbotWidget::logLevel.lens({ it.logLevel }, { p, v -> p.copy(logLevel = v) })
    }
}
