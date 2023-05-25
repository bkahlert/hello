package com.bkahlert.hello.components.applet

import com.bkahlert.hello.fritz2.components.heroicons.SolidHeroIcons
import com.bkahlert.hello.fritz2.lens
import com.bkahlert.hello.fritz2.mapJson
import com.bkahlert.hello.fritz2.mergeValidationMessages
import com.bkahlert.kommons.json.LenientAndPrettyJson
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.toUriOrNull
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.Lens
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Tag
import dev.fritz2.core.values
import dev.fritz2.headless.components.textArea
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.serializer
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement

class AppletSerializer(val registration: AppletRegistration) : KSerializer<Applet> {

    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor = buildSerialDescriptor("AppletSerializer", PolymorphicKind.SEALED)

    fun selectSerializer(value: Applet): SerializationStrategy<Applet> = when (value) {
        is UnknownApplet -> UnknownAppletSerializer.unsafeCast<SerializationStrategy<Applet>>()
        else -> requireNotNull(registration.find(value)) { "The specified value is not handled by any registration." }.let { registration ->
            object : JsonTransformingSerializer<Applet>(registration.serializer.unsafeCast<KSerializer<Applet>>()) {
                override fun transformSerialize(element: JsonElement): JsonElement = buildJsonObject {
                    element.jsonObject.entries.forEach { (key, value) -> put(key, value) }
                    put("type", registration.type)
                }
            }
        }
    }

    fun selectDeserializer(element: JsonElement): DeserializationStrategy<Applet> =
        element.jsonObject["type"]?.jsonPrimitive?.contentOrNull?.let { registration.get(it) }?.serializer ?: UnknownAppletSerializer

    override fun serialize(encoder: Encoder, value: Applet) {
        selectSerializer(value).serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): Applet {
        val input = checkNotNull(decoder as? JsonDecoder) {
            listOf(
                "This serializer can be used only with Json format.",
                "Expected Decoder to be JsonDecoder, got ${decoder::class}",
            ).joinToString("\n")
        }
        val tree = input.decodeJsonElement()
        return input.json.decodeFromJsonElement(selectDeserializer(tree), tree)
    }
}

private object UnknownAppletSerializer : JsonTransformingSerializer<UnknownApplet>(serializer<UnknownApplet>()) {
    override fun transformSerialize(element: JsonElement): JsonElement = buildJsonObject {
        (element as? JsonObject)?.get("raw")?.let { (it as? JsonObject) }?.forEach { put(it.key, it.value) }
    }

    override fun transformDeserialize(element: JsonElement): JsonElement = buildJsonObject { put("raw", element) }
}

@Serializable
private data class UnknownApplet(
    val raw: JsonElement,
) : Applet {
    private fun get(key: String): String? =
        (raw as? JsonObject)?.get(key)?.let { it as? JsonPrimitive }?.contentOrNull

    override val id: String get() = get("id") ?: Applet.randomId()
    override val title: String get() = "Unknown" + get("title")?.let { ": $it" }.orEmpty()
    override val icon: Uri get() = get("icon")?.toUriOrNull() ?: SolidHeroIcons.question_mark_circle

    override fun editor(isNew: Boolean): AppletEditor<*> = UnknownAppletEditor(isNew, this)

    override fun render(renderContext: Tag<Element>): HtmlTag<HTMLDivElement> = renderContext.panel {
        pre("prose p-8 dark:prose-invert") {
            code {
                +LenientAndPrettyJson.encodeToString(raw)
            }
        }
    }

    companion object {
        fun raw(): Lens<UnknownApplet, JsonElement> = UnknownApplet::raw.lens({ it.raw }, { p, v -> p.copy(raw = v) })
    }
}

private class UnknownAppletEditor(isNew: Boolean, applet: UnknownApplet) : AppletEditor<UnknownApplet>(isNew, applet) {
    override fun RenderContext.renderFields() {
        textArea {
            val store = map(UnknownApplet.raw()).mapJson()
            value(store)
            textareaLabel {
                +"Raw"
                textareaTextfield("h-40") {
                    keyups.values() handledBy store.update
                }.also(::mergeValidationMessages)
            }
        }
    }
}
