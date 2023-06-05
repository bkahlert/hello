package com.bkahlert.hello.chatbot

import com.aallam.openai.api.chat.ChatChunk
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.bkahlert.kommons.json.LenientAndPrettyJson
import com.bkahlert.kommons.quoted
import com.bkahlert.kommons.string
import kotlin.random.Random

// Constructor as only provided constructor is internal...
public fun ChatCompletionChunk(
    id: String = Random.string(),
    model: ModelId = ModelId("gpt-3.5-turbo"),
    role: ChatRole? = ChatRole.Assistant,
    content: String?,
    finishReason: String? = if (content == null) "stop" else null,
): ChatCompletionChunk = LenientAndPrettyJson.decodeFromString(
    ChatCompletionChunk.serializer(),
    """
        {
          "id": "$id",
          "model": ${model.quoted},
          "object": "chat.completion",
          "created": 1677652288,
          "choices": [{
            "index": 0,
            "delta": {
              "role": ${role?.role?.quoted},
              "content": ${content?.quoted}
            },
            "finish_reason": ${finishReason?.quoted}
          }],
          "usage": {
            "prompt_tokens": 9,
            "completion_tokens": 12,
            "total_tokens": 21
          }
        }
    """
)

public val ChatCompletionChunk.firstChoice: ChatChunk
    get() = choices.firstOrNull() ?: error("${ChatCompletionChunk::class.simpleName} has no choices: $this")

public fun Collection<ChatCompletionChunk>.toMessage(): ChatMessage? {
    val role: ChatRole? = firstNotNullOfOrNull { it.firstChoice.delta?.role }
    return if (role != null) {
        val content: String = mapNotNull { it.firstChoice.delta?.content }.joinToString("")
        val name: String? = firstNotNullOfOrNull { it.firstChoice.delta?.name }
        ChatMessage(role = role, content = content, name = name)
    } else {
        null
    }
}
