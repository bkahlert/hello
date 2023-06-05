package com.bkahlert.hello.chatbot

import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.bkahlert.kommons.uri.Uri
import dev.fritz2.core.EmittingHandler
import dev.fritz2.core.RootStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

public class ChatbotChunks : RootStore<List<ChatCompletionChunk>>(emptyList()) {

    public val addChunk: EmittingHandler<ChatCompletionChunk, ChatMessage> = handleAndEmit<ChatCompletionChunk, ChatMessage> { chunks, chunk ->
        delay(100)
        if (chunk.firstChoice.finishReason != null) {
            chunks.toMessage()?.also { emit(it) }
            emptyList()
        } else {
            chunks + chunk
        }
    }

    public val accumulatedMessage: Flow<ChatMessage?> = data.map { chunks -> chunks.toMessage() }
        .distinctUntilChanged { old, new ->
            old?.role == new?.role && old?.name == new?.name
        }
    public val author: Flow<Pair<ChatRole, Uri>?> = accumulatedMessage.map { msg ->
        msg?.role?.let { it to msg.icon }
    }
}
