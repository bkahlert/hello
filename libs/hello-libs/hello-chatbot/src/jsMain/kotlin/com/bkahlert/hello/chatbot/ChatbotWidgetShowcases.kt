package com.bkahlert.hello.chatbot

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.audio.Transcription
import com.aallam.openai.api.audio.TranscriptionRequest
import com.aallam.openai.api.audio.Translation
import com.aallam.openai.api.audio.TranslationRequest
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.completion.TextCompletion
import com.aallam.openai.api.edits.Edit
import com.aallam.openai.api.edits.EditsRequest
import com.aallam.openai.api.embedding.EmbeddingRequest
import com.aallam.openai.api.embedding.EmbeddingResponse
import com.aallam.openai.api.file.File
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.file.FileUpload
import com.aallam.openai.api.finetune.FineTune
import com.aallam.openai.api.finetune.FineTuneEvent
import com.aallam.openai.api.finetune.FineTuneId
import com.aallam.openai.api.finetune.FineTuneRequest
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageEdit
import com.aallam.openai.api.image.ImageJSON
import com.aallam.openai.api.image.ImageURL
import com.aallam.openai.api.image.ImageVariation
import com.aallam.openai.api.model.Model
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.moderation.ModerationRequest
import com.aallam.openai.api.moderation.TextModeration
import com.aallam.openai.client.Audio
import com.aallam.openai.client.Chat
import com.aallam.openai.client.Completions
import com.aallam.openai.client.Edits
import com.aallam.openai.client.Embeddings
import com.aallam.openai.client.Files
import com.aallam.openai.client.FineTunes
import com.aallam.openai.client.Images
import com.aallam.openai.client.Models
import com.aallam.openai.client.Moderations
import com.aallam.openai.client.OpenAI
import com.bkahlert.hello.icon.heroicons.HeroIcons
import com.bkahlert.hello.icon.heroicons.SolidHeroIcons
import com.bkahlert.hello.page.SimplePage
import com.bkahlert.hello.showcase.showcase
import com.bkahlert.hello.showcase.showcases
import com.bkahlert.kommons.md5
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emptyFlow

public val ChatbotWidgetShowcases: SimplePage = SimplePage(
    "chatbot-widget",
    "Chatbot Widget",
    "Chatbot widget showcases",
    heroIcon = HeroIcons::chat_bubble_left_right,
) {
    showcases("ChatMessage", SolidHeroIcons.chat_bubble_left) {
        val roles = listOf(
            ChatRole.System,
            ChatRole.User,
            ChatRole.Assistant,
            ChatRole("unknown"),
        )
        val message = "This is a test message"

        showcase("With user") {
            chat(
                ChatbotSession(
                    initialMessages = roles.map { ChatMessage(role = it, content = message) },
                    api = TestOpenAI(),
                    user = md5("john.doe@example.com"),
                )
            )
        }
        showcase("Without user") {
            chat(
                ChatbotSession(
                    initialMessages = roles.map { ChatMessage(role = it, content = message) },
                    api = TestOpenAI(),
                )
            )
        }
    }

    hr { }

    showcases("Chat", SolidHeroIcons.chat_bubble_left_right) {
        showcase("Initial") {
            chat(
                ChatbotSession(
                    api = TestOpenAI(),
                )
            )
        }
        showcase("Sending") {
            chat(
                ChatbotSession(
                    api = TestOpenAI(),
                ).apply {
                    sendMessage("Hi")
                }
            )
        }
        showcase("Response stream") {
            chat(
                ChatbotSession(
                    api = TestOpenAI(chat = TestChat("Hi,", " how", " can", " I", " assist")),
                ).apply {
                    sendMessage("Hi")
                }
            )
        }
        showcase("Response stream completion") {
            chat(
                ChatbotSession(
                    api = TestOpenAI(chat = TestChat("Hi,", " how", " can", " I", " assist", " you?", null)),
                ).apply {
                    sendMessage("Hi")
                }
            )
        }
        showcase("Idle") {
            chat(
                ChatbotSession(
                    ChatRole.User to "Hi",
                    ChatRole.Assistant to "Hi, how can I assist you?",
                    api = TestOpenAI(),
                )
            )
        }
    }
}

public class TestOpenAI(
    public val completions: Completions = TestCompletions(),
    @JsName("filesImpl")
    public val files: Files = TestFiles(),
    public val edits: Edits = TestEdits(),
    public val embeddings: Embeddings = TestEmbeddings(),
    @JsName("modelsImpl")
    public val models: Models = TestModels(),
    public val moderations: Moderations = TestModerations(),
    @JsName("fineTunesImpl")
    public val fineTunes: FineTunes = TestFineTunes(),
    public val images: Images = TestImages(),
    public val chat: Chat = TestChat(),
    public val audio: Audio = TestAudio(),
) : OpenAI,
    Completions by completions,
    Files by files,
    Edits by edits,
    Embeddings by embeddings,
    Models by models,
    Moderations by moderations,
    FineTunes by fineTunes,
    Images by images,
    Chat by chat,
    Audio by audio {
    override fun close() {}
}

public class TestCompletions : Completions {
    override suspend fun completion(request: CompletionRequest): TextCompletion = TODO("Not yet implemented")
    override fun completions(request: CompletionRequest): Flow<TextCompletion> = emptyFlow()
}

public class TestFiles : Files {
    override suspend fun delete(fileId: FileId): Boolean = false
    override suspend fun download(fileId: FileId): ByteArray = ByteArray(0)
    override suspend fun file(fileId: FileId): File? = null
    override suspend fun file(request: FileUpload): File = TODO("Not yet implemented")
    override suspend fun files(): List<File> = emptyList()
}

public class TestEdits : Edits {
    override suspend fun edit(request: EditsRequest): Edit = TODO("Not yet implemented")
}

public class TestEmbeddings : Embeddings {
    override suspend fun embeddings(request: EmbeddingRequest): EmbeddingResponse = TODO("Not yet implemented")
}

public class TestModels : Models {
    override suspend fun model(modelId: ModelId): Model = TODO("Not yet implemented")
    override suspend fun models(): List<Model> = emptyList()
}

public class TestModerations : Moderations {
    override suspend fun moderations(request: ModerationRequest): TextModeration = TODO("Not yet implemented")
}

public class TestFineTunes : FineTunes {
    override suspend fun cancel(fineTuneId: FineTuneId): FineTune = TODO("Not yet implemented")
    override suspend fun delete(fineTuneModel: ModelId): Boolean = false
    override suspend fun fineTune(fineTuneId: FineTuneId): FineTune? = null
    override suspend fun fineTune(request: FineTuneRequest): FineTune = TODO("Not yet implemented")
    override suspend fun fineTuneEvents(fineTuneId: FineTuneId): List<FineTuneEvent> = emptyList()
    override fun fineTuneEventsFlow(fineTuneId: FineTuneId): Flow<FineTuneEvent> = emptyFlow()
    override suspend fun fineTunes(): List<FineTune> = emptyList()
}

public class TestImages : Images {
    @BetaOpenAI
    override suspend fun imageJSON(creation: ImageCreation): List<ImageJSON> = emptyList()

    @BetaOpenAI
    override suspend fun imageJSON(edit: ImageEdit): List<ImageJSON> = emptyList()

    @BetaOpenAI
    override suspend fun imageJSON(variation: ImageVariation): List<ImageJSON> = emptyList()

    @BetaOpenAI
    override suspend fun imageURL(creation: ImageCreation): List<ImageURL> = emptyList()

    @BetaOpenAI
    override suspend fun imageURL(edit: ImageEdit): List<ImageURL> = emptyList()

    @BetaOpenAI
    override suspend fun imageURL(variation: ImageVariation): List<ImageURL> = emptyList()
}

/**
 * A chat that returns the specified [chunks] as a completion.
 *
 * The specified [chunks] must either be a `null`-terminated list of strings (otherwise the completion never completes)
 * or a single string.
 */
public class TestChat(
    public val chunks: List<String?>,
    public val role: ChatRole? = ChatRole.Assistant,
) : Chat {

    public constructor(
        vararg chunks: String?,
        role: ChatRole? = ChatRole.Assistant,
    ) : this(
        chunks = chunks
            .singleOrNull()
            ?.split(' ')
            ?.mapIndexed { i, s -> if (i == 0) s else " $s" }
            ?.plus(null)
            ?: chunks.asList(),
        role = role,
    )

    @BetaOpenAI
    override suspend fun chatCompletion(request: ChatCompletionRequest): ChatCompletion = TODO("Not yet implemented")

    @BetaOpenAI
    override fun chatCompletions(request: ChatCompletionRequest): Flow<ChatCompletionChunk> = chunks
        .mapIndexed { index, content -> ChatCompletionChunk(id = "chatcmpl-$index", model = request.model, role = role, content = content) }
        .asFlow()
}

public class TestAudio : Audio {
    @BetaOpenAI
    override suspend fun transcription(request: TranscriptionRequest): Transcription = TODO("Not yet implemented")

    @BetaOpenAI
    override suspend fun translation(request: TranslationRequest): Translation = TODO("Not yet implemented")
}
