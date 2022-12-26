package com.bkahlert.kommons.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.js.Date

@Serializer(Date::class)
object DateAsMillisecondsSerializer : KSerializer<Date> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "com.bkahlert.kommons.serialization.DateAsMillisecondsSerializer", STRING)

    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeLong(value.getTime().toLong())
    }

    override fun deserialize(decoder: Decoder): Date {
        return Date(decoder.decodeLong())
    }
}
