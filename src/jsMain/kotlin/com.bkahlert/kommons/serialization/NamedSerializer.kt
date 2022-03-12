package com.bkahlert.kommons.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.mapSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlin.reflect.KClass

class NamedSerializer<T>(private val dataSerializer: KSerializer<T>) : KSerializer<Named<T>> {

    override val descriptor: SerialDescriptor = mapSerialDescriptor(
        String.serializer().descriptor,
        dataSerializer.descriptor,
    )

    override fun serialize(encoder: Encoder, value: Named<T>) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.name)
            encodeSerializableElement(descriptor, 1, dataSerializer, value.value)
        }
    }

    override fun deserialize(decoder: Decoder): Named<T> {
        val composite = decoder.beginStructure(descriptor)
        var key: String? = null
        var value: T? = null
        while (true) {
            when (val index = composite.decodeElementIndex(descriptor)) {
                0 -> key = composite.decodeStringElement(descriptor, 0)
                1 -> value = composite.decodeSerializableElement(descriptor, 1, dataSerializer)
                DECODE_DONE -> break
                else -> error("Unexpected index: $index")
            }
        }
        composite.endStructure(descriptor)
        require(key != null)
        @Suppress("UNCHECKED_CAST")
        return Named(key, value as T)
    }
}

/**
 * Named representation of any [value]
 * as an object that will get serialized as:
 * ```json
 * {
 *     "$name": $value
 * }
 * ```
 */
@Serializable(with = NamedSerializer::class)
@SerialName("Names")
data class Named<T>(
    val name: String,
    val value: T,
) {
    companion object {
        inline val <T : Any> KClass<T>.derivedName
            get() = simpleName?.lowercase() ?: throw IllegalStateException("unable to derive name for $this")

        fun <T> data(value: T? = null): Named<T?> =
            Named("data", value)

        inline fun <reified T : Any> ofSingle(single: T): Named<T> =
            Named(T::class.derivedName, single)

        inline fun <reified T : Any> ofMultiple(multiple: List<T>): Named<List<T>> =
            Named("${T::class.derivedName}s", multiple)

        inline fun <reified T : Any> ofMultiple(multiple: Array<T>): Named<Array<T>> =
            Named("${T::class.derivedName}s", multiple)
    }
}
