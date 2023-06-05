package com.bkahlert.hello.chatbot

import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.bkahlert.hello.editor.selectField
import com.bkahlert.hello.fritz2.mergeValidationMessages
import com.bkahlert.hello.widget.WidgetEditor
import dev.fritz2.core.RenderContext
import dev.fritz2.core.lensOf
import dev.fritz2.core.placeholder
import dev.fritz2.core.required
import dev.fritz2.core.type
import dev.fritz2.core.values
import dev.fritz2.headless.components.inputField
import dev.fritz2.headless.foundation.setInitialFocus
import kotlinx.coroutines.flow.map

public class ChatbotWidgetEditor(isNew: Boolean, widget: ChatbotWidget) : WidgetEditor<ChatbotWidget>(isNew, widget) {

    override fun RenderContext.renderFields() {
        val token = map(ChatbotWidget.token())
        inputField {
            value(token)
            inputLabel {
                +"Token"
                inputTextfield {
                    type("text")
                    placeholder("sk-abc123...")
                    required(true)
                    setInitialFocus()
                    focuss handledBy { domNode.select() }
                }.also(::mergeValidationMessages)
            }
        }

        token.data.map { it ->
            it.takeUnless { it.isBlank() }?.run { OpenAI(token = this, logging = LoggingConfig(logLevel = LogLevel.None)).models() }
        }.render { models ->
            if (models != null) {
                val modelIds = models.map { it.id.id }.sorted()
                selectField(
                    store = map(ChatbotWidget.model()).map(
                        lensOf(
                            "",
                            { it.takeIf { modelIds.contains(it) } ?: modelIds.firstOrNull().orEmpty() },
                            { p, v -> v.takeIf { modelIds.contains(it) } ?: p })
                    ),
                    label = "Model",
                    options = modelIds,
                    itemTitle = { it },
                )
            } else {
                inputField {
                    value(map(ChatbotWidget.model()))
                    inputLabel {
                        +"Model"
                        inputTextfield {
                            type("text")
                            placeholder(Chatbot.ChatGPT.defaultModel.id)
                            required(false)
                        }.also(::mergeValidationMessages)
                    }
                }
            }
        }

        selectField(
            store = map(ChatbotWidget.logLevel()),
            label = "Log level",
        )
        inputField {
            val store = map(ChatbotWidget.title())
            value(store)
            inputLabel {
                +"Title"
                inputTextfield {
                    type("text")
                    keyups.values() handledBy store.update
                }.also(::mergeValidationMessages)
            }
        }
    }
}
