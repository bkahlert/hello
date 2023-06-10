package com.bkahlert.hello.chatbot

import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.bkahlert.hello.chat.chat
import com.bkahlert.hello.chat.chatMessage
import com.bkahlert.hello.fritz2.ContentBuilder
import com.bkahlert.hello.fritz2.srOnly
import com.bkahlert.hello.icon.heroicons.SolidHeroIcons
import com.bkahlert.hello.icon.icon
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Store
import dev.fritz2.core.Tag
import dev.fritz2.core.placeholder
import dev.fritz2.core.storeOf
import dev.fritz2.core.type
import dev.fritz2.core.value
import dev.fritz2.core.values
import dev.fritz2.headless.components.inputField
import kotlinx.coroutines.flow.map
import org.w3c.dom.HTMLDivElement

public fun RenderContext.chat(chatbotSession: ChatbotSession): HtmlTag<HTMLDivElement> = chat(
    history = {
        chatMeta(chatbotSession)
        chatHistory(chatbotSession)
    },
    prompt = {
        chatPrompt(chatbotSession)
    },
)

private fun RenderContext.chatMeta(chatbotSession: ChatbotSession) = div("w-full h-full grid place-items-center") {
    className(chatbotSession.data.map { if (it.isEmpty()) "min-h-[10rem]" else "hidden" })
    chatbotSession.data.render(this) {
        div("flex gap-2 items-center") {
            icon("w-8", Chatbot.ChatGPT.logo)
            strong { +(chatbotSession.model ?: Chatbot.ChatGPT.defaultModel).id }
        }
    }
}

private fun RenderContext.chatHistory(chatbotSession: ChatbotSession): HtmlTag<HTMLDivElement> = div {
    chatbotSession.data.renderEach {
        chatMessage(it)
    }
    chatbotSession.response.render {
        chatMessage(it)
    }
}


private fun RenderContext.chatMessage(message: ChatMessage): HtmlTag<HTMLDivElement> = chatMessage(
    image = { icon("w-6 rounded-full", message.icon) },
    end = message.role == ChatRole.User,
) {
    chatMessageContent(message.role) { +message.content }
}

private fun RenderContext.chatMessage(chunks: ChatbotChunks?) = when (chunks) {
    null -> {}
    else -> chunks.author.render { author ->
        when (author) {
            null -> {
                chatMessage(
                    image = { icon("w-6 rounded-full", SolidHeroIcons.ellipsis_horizontal) },
                )
            }

            else -> {
                val (role, icon) = author
                chatMessage(
                    image = { icon("w-6 rounded-full animate-spin", icon) },
                    end = role == ChatRole.User,
                ) {
                    chatMessageContent(author.first) {
                        chunks.data.renderEach({ it.id }) { span { +it.firstChoice.delta?.content.orEmpty() } }
                    }
                }
            }
        }
    }
}

private fun RenderContext.chatMessageContent(role: ChatRole, content: ContentBuilder<HTMLDivElement>): HtmlTag<HTMLDivElement> = when (role) {
    ChatRole.User -> div("flex flex-col items-start gap-4 whitespace-pre-wrap break-words") { content() }
    else -> div("markdown break-words prose dark:prose-invert") { content() }
}


private fun RenderContext.chatPrompt(
    chatBotSession: ChatbotSession,
    store: Store<String> = storeOf(""),
): Tag<HTMLDivElement> = inputField {
    inputLabel {
        value(store)
        srOnly { +"You" }
        inputTextfield {
            type("text")
            placeholder("Send a message...")
            changes.values() handledBy {
                chatBotSession.sendMessage(it)
                value("")
            }
        }
    }
}
