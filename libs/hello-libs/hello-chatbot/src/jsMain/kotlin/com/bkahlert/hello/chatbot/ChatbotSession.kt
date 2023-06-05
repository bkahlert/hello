package com.bkahlert.hello.chatbot

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import dev.fritz2.core.Handler
import dev.fritz2.core.RootStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

public class ChatbotSession(
    initialMessages: List<ChatMessage> = emptyList(),
    private val api: OpenAI,
    public val model: ModelId? = null,
    public val user: String? = null,
) : RootStore<List<ChatMessage>>(initialMessages.map { it.copy(name = user) }) {

    public constructor(
        vararg initial: Pair<ChatRole, String>,
        api: OpenAI,
        model: ModelId? = null,
        user: String? = null,
    ) : this(initial.map { (role, message) -> ChatMessage(role = role, content = message) }, api, model, user)

    private val _response = MutableStateFlow<ChatbotChunks?>(null)
    public val response: Flow<ChatbotChunks?> = _response.asStateFlow()

    /**
     * Adds the specified message and its response to [data].
     * The [partial message deltas][ChatCompletionChunk] are handled as [response].
     */
    public val sendMessage: Handler<String> = handle { messages, message ->
        val request = ChatCompletionRequest(
            model = model ?: Chatbot.ChatGPT.defaultModel,
            messages = messages + ChatMessage(role = ChatRole.User, content = message, name = user)
        )

        val response = ChatbotChunks().apply {
            api.chatCompletions(request) handledBy addChunk
            addChunk handledBy addResponse
        }
        _response.update { response }

        request.messages
    }

    public val addResponse: Handler<ChatMessage> = handle { messages, message ->
        _response.update { null }
        messages + message
    }

    public companion object
}
