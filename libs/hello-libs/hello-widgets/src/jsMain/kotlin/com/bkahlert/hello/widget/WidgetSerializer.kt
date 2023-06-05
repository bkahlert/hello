package com.bkahlert.hello.widget

import com.bkahlert.hello.editor.mapJson
import com.bkahlert.hello.fritz2.lens
import com.bkahlert.hello.fritz2.mergeValidationMessages
import com.bkahlert.hello.icon.heroicons.SolidHeroIcons
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
import kotlinx.serialization.builtins.ListSerializer
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
import kotlinx.serialization.serializer
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement

public class WidgetSerializer(
    public val registration: WidgetRegistration,
) : KSerializer<Widget> {

    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor = buildSerialDescriptor("WidgetSerializer", PolymorphicKind.SEALED)

    public fun selectSerializer(value: Widget): SerializationStrategy<Widget> = when (value) {
        is UnknownWidget -> UnknownWidgetSerializer.unsafeCast<SerializationStrategy<Widget>>()
        else -> requireNotNull(registration.find(value)) { "The specified value is not handled by any registration." }.serializer
    }

    public fun selectDeserializer(element: JsonElement): DeserializationStrategy<Widget> =
        element.jsonObject["type"]?.jsonPrimitive?.contentOrNull?.let { registration[it] }?.serializer ?: UnknownWidgetSerializer

    override fun serialize(encoder: Encoder, value: Widget) {
        selectSerializer(value).serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): Widget {
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

public fun WidgetRegistration.serializer(): KSerializer<List<Widget>> =
    ListSerializer(WidgetSerializer(this))

private object UnknownWidgetSerializer : JsonTransformingSerializer<UnknownWidget>(serializer<UnknownWidget>()) {
    override fun transformSerialize(element: JsonElement): JsonElement = buildJsonObject {
        (element as? JsonObject)?.get("raw")?.let { (it as? JsonObject) }?.forEach { put(it.key, it.value) }
    }

    override fun transformDeserialize(element: JsonElement): JsonElement = buildJsonObject { put("raw", element) }
}

@Serializable
private data class UnknownWidget(
    val raw: JsonElement,
) : Widget {
    private fun get(key: String): String? =
        (raw as? JsonObject)?.get(key)?.let { it as? JsonPrimitive }?.contentOrNull

    override val id: String get() = get("id") ?: Widget.randomId()
    override val title: String get() = "Unknown" + get("title")?.let { ": $it" }.orEmpty()
    override val icon: Uri get() = get("icon")?.toUriOrNull() ?: SolidHeroIcons.question_mark_circle

    override fun render(renderContext: Tag<Element>): HtmlTag<HTMLDivElement> = renderContext.panel {
        pre("prose p-8 dark:prose-invert") {
            code {
                +LenientAndPrettyJson.encodeToString(raw)
            }
        }
    }

    override fun editor(isNew: Boolean): WidgetEditor<*> = UnknownWidgetEditor(isNew, this)

    companion object {
        fun raw(): Lens<UnknownWidget, JsonElement> = UnknownWidget::raw.lens({ it.raw }, { p, v -> p.copy(raw = v) })
    }
}

private class UnknownWidgetEditor(isNew: Boolean, widget: UnknownWidget) : WidgetEditor<UnknownWidget>(isNew, widget) {
    override fun RenderContext.renderFields() {
        textArea {
            val store = map(UnknownWidget.raw()).mapJson()
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
