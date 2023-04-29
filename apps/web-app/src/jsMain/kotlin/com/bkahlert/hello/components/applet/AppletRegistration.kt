package com.bkahlert.hello.components.applet

import com.bkahlert.kommons.json.LenientJson
import com.bkahlert.kommons.quoted
import com.bkahlert.kommons.uri.Uri
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

class AppletRegistration : AbstractMap<String, AppletRegistration.Registration<out Applet>>() {
    private val registrations: MutableMap<String, Registration<out Applet>> = mutableMapOf()
    private val alternatives: MutableMap<String, String> = mutableMapOf()
    override val entries: Set<Map.Entry<String, Registration<out Applet>>> get() = registrations.entries

    class Registration<T : Applet>(
        val type: String,
        val title: String,
        val description: String,
        val icon: Uri,
        val clazz: KClass<T>,
        val serializer: KSerializer<T>,
    ) {
        fun create(id: String): T = LenientJson.decodeFromJsonElement(
            serializer,
            buildJsonObject {
                put("id", id)
                put("type", type)
            },
        )
    }

    fun <T : Applet> register(
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

    inline fun <reified T : Applet> register(
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

    fun findByType(type: String): Registration<out Applet>? = registrations[type] ?: registrations[alternatives[type]]
    fun findByInstance(applet: Applet): Registration<out Applet>? = registrations.values.firstOrNull { registration ->
        registration.clazz.isInstance(applet)
    }
}
