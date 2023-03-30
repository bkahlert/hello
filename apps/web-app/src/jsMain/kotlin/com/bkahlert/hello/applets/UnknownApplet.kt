package com.bkahlert.hello.applets

import com.bkahlert.hello.fritz2.components.heroicons.SolidHeroIcons
import com.bkahlert.hello.fritz2.inputEditor
import com.bkahlert.hello.fritz2.mapValidating
import com.bkahlert.kommons.randomString
import com.bkahlert.kommons.uri.Uri
import dev.fritz2.core.RenderContext
import dev.fritz2.core.lensOf
import dev.fritz2.core.storeOf
import dev.fritz2.validation.ValidationMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable(with = UnknownAppletSerializer::class)
data class UnknownApplet(
    override val id: String,
    override val name: String,
    val details: Map<String, JsonElement>,
) : Applet {

    override val icon: Uri get() = UnknownApplet.icon

    override fun duplicate(): Applet = copy(id = randomString())

    override fun render(renderContext: RenderContext) {
        renderContext.window(name) {
            pre {
                code {
                    +details.toString()
                }
            }
        }
    }

    override fun renderEditor(renderContext: RenderContext, contributeMessages: (Flow<List<ValidationMessage>>) -> Unit): Flow<Applet> {
        val store = storeOf(this)
        renderContext.div("flex flex-col sm:flex-row gap-8 justify-center") {
            div("flex-grow flex flex-col gap-2") {
                label {
                    +"Name"
                    inputEditor(null, store.mapValidating(lensOf("name", { it.name }, { p, v ->
                        require(v.isNotBlank()) { "Name must not be blank" }
                        p.copy(name = v)
                    })).also { contributeMessages(it.messages) })
                }
            }
        }
        return store.data
    }

    companion object : AppletType<UnknownApplet> {
        override val name: String = "Unknown"
        override val description: String = "Unrecognized applet"
        override val icon: Uri = SolidHeroIcons.question_mark_circle
        override val default get() = error("Unknown applet cannot be created")
    }
}

object UnknownAppletSerializer : KSerializer<UnknownApplet> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("UnknownApplet") {
        element<String>("id")
        element<String>("name")
        element<JsonElement>("details")
    }

    override fun serialize(encoder: Encoder, value: UnknownApplet) {
        error("Serialization is not supported")
    }

    override fun deserialize(decoder: Decoder): UnknownApplet {
        val jsonDecoder = decoder as? JsonDecoder ?: error("Can be deserialized only by JSON")
        val json = jsonDecoder.decodeJsonElement().jsonObject
        val id = json.getValue("name").jsonPrimitive.content
        val name = json.getValue("name").jsonPrimitive.content
        val details = json.toMutableMap()
        details.remove("id")
        details.remove("name")
        return UnknownApplet(id, name, JsonObject(details))
    }
}
