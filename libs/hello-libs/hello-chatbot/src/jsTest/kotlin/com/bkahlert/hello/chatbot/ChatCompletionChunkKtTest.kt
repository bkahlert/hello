package com.bkahlert.hello.chatbot

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ChatCompletionChunkKtTest {

    @Test
    fun instantiation() {
        ChatCompletionChunk(content = "test") should {
            it.firstChoice.delta?.content shouldBe "test"
            it.firstChoice.finishReason.shouldBeNull()
        }
        ChatCompletionChunk(content = null) should {
            it.firstChoice.delta?.content.shouldBeNull()
            it.firstChoice.finishReason shouldBe "stop"
        }
    }
}
