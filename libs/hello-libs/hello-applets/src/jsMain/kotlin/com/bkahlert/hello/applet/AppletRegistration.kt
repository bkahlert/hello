package com.bkahlert.hello.applet

import com.bkahlert.kommons.json.LenientJson
import com.bkahlert.kommons.quoted
import com.bkahlert.kommons.uri.Uri
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

public class AppletRegistration : AbstractMap<String, AppletRegistration.Registration<out Applet>>() {
    private val registrations: MutableMap<String, Registration<out Applet>> = mutableMapOf()
    private val alternatives: MutableMap<String, String> = mutableMapOf()
    override val entries: Set<Map.Entry<String, Registration<out Applet>>> get() = registrations.entries

    public class Registration<T : Applet>(
        public val type: String,
        public val title: String,
        public val description: String,
        public val icon: Uri,
        public val clazz: KClass<T>,
        public val serializer: KSerializer<T>,
    ) {
        public fun create(id: String): T = LenientJson.decodeFromJsonElement(
            serializer,
            buildJsonObject {
                put("id", id)
                put("type", type)
            },
        )

        public fun duplicate(id: String, applet: T): T {
            val jsonElement = LenientJson.encodeToJsonElement(
                serializer,
                applet,
            )
            val jsonObject: JsonObject = jsonElement as? JsonObject ?: error("Applet unexpectedly not encoded to JsonObject but to $jsonElement")
            val duplicate: T = LenientJson.decodeFromJsonElement(
                serializer,
                buildJsonObject {
                    jsonObject.forEach { (key, value) -> put(key, value) }
                    put("id", id)
                    put("type", "Copy of ${applet.title}")
                })
            return duplicate
        }
    }

    public fun <T : Applet> register(
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

    public inline fun <reified T : Applet> register(
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
    public fun find(type: String): Registration<out Applet>? = registrations[type] ?: registrations[alternatives[type]]

    /** Returns the [Registration] for the given [applet] or `null` if none is registered. */
    public fun find(applet: Applet): Registration<Applet>? =
        registrations.values.firstOrNull { it.clazz.isInstance(applet) }?.unsafeCast<Registration<Applet>>()

    /** Returns the [Registration] for the given [type] or throws an exception if none is registered. */
    public fun require(type: String): Registration<out Applet> = checkNotNull(find(type)) { "No registration found for type $type" }

    /** Returns the [Registration] for the given [applet] or throws an exception if none is registered. */
    public fun require(applet: Applet): Registration<Applet> = checkNotNull(find(applet)) { "No registration found for applet of type ${applet::class.js}" }

    public companion object
}
