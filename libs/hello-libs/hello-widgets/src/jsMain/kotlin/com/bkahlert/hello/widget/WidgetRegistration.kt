package com.bkahlert.hello.widget

import com.bkahlert.kommons.json.LenientJson
import com.bkahlert.kommons.quoted
import com.bkahlert.kommons.uri.Uri
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

public class WidgetRegistration : AbstractMap<String, WidgetRegistration.Registration<out Widget>>() {
    private val registrations: MutableMap<String, Registration<out Widget>> = mutableMapOf()
    private val alternatives: MutableMap<String, String> = mutableMapOf()
    override val entries: Set<Map.Entry<String, Registration<out Widget>>> get() = registrations.entries

    public class Registration<T : Widget>(
        public val type: String,
        public val title: String,
        public val description: String,
        public val icon: Uri,
        public val clazz: KClass<T>,
        serializer: KSerializer<T>,
    ) {
        public val serializer: KSerializer<T> = object : JsonTransformingSerializer<T>(serializer) {
            override fun transformSerialize(element: JsonElement): JsonElement = buildJsonObject {
                element.jsonObject.entries.forEach { (key, value) -> put(key, value) }
                put("type", type)
            }
        }

        public fun create(id: String): T = LenientJson.decodeFromJsonElement(
            serializer,
            buildJsonObject {
                put("id", id)
                put("type", type)
            },
        )

        public fun duplicate(id: String, widget: T): T {
            val jsonElement = LenientJson.encodeToJsonElement(
                serializer,
                widget,
            )
            val jsonObject: JsonObject = jsonElement as? JsonObject ?: error("Widget unexpectedly not encoded to JsonObject but to $jsonElement")
            val duplicate: T = LenientJson.decodeFromJsonElement(
                serializer,
                buildJsonObject {
                    jsonObject.forEach { (key, value) -> put(key, value) }
                    put("id", id)
                    put("type", "Copy of ${widget.title}")
                })
            return duplicate
        }
    }

    public fun <T : Widget> register(
        type: String,
        vararg alternativeTypes: String,
        title: String,
        description: String,
        icon: Uri,
        clazz: KClass<T>,
        serializer: KSerializer<T>,
    ): Registration<T> = Registration(type, title, description, icon, clazz, serializer).also { registration ->
        val existingRegistration = registrations[type]
        if (existingRegistration != null) error("Type ${type.quoted} already registered: $existingRegistration")
        registrations[type] = registration
        alternativeTypes.forEach { alternativeType -> alternatives[alternativeType] = type }
    }

    public inline fun <reified T : Widget> register(
        type: String,
        vararg alternativeTypes: String,
        title: String,
        description: String,
        icon: Uri,
    ): Registration<T> = register(
        type,
        *alternativeTypes,
        title = title,
        description = description,
        icon = icon,
        clazz = T::class,
        serializer = serializer<T>(),
    )

    /** Returns the [Registration] for the given [type] or `null` if none is registered. */
    public fun find(type: String): Registration<out Widget>? = registrations[type] ?: registrations[alternatives[type]]

    /** Returns the [Registration] for the given [widget] or `null` if none is registered. */
    public fun find(widget: Widget): Registration<Widget>? =
        registrations.values.firstOrNull { it.clazz.isInstance(widget) }?.unsafeCast<Registration<Widget>>()

    /** Returns the [Registration] for the given [type] or throws an exception if none is registered. */
    public fun require(type: String): Registration<out Widget> = checkNotNull(find(type)) { "No registration found for type $type" }

    /** Returns the [Registration] for the given [widget] or throws an exception if none is registered. */
    public fun require(widget: Widget): Registration<Widget> = checkNotNull(find(widget)) { "No registration found for widget of type ${widget::class.js}" }

    public companion object
}
