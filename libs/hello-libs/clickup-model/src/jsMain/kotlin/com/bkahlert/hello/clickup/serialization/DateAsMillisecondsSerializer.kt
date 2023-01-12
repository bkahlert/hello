package com.bkahlert.hello.clickup.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.js.Date

public object DateAsMillisecondsSerializer : KSerializer<Date> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "com.bkahlert.hello.clickup.serialization.DateAsMillisecondsSerializer", PrimitiveKind.LONG
    )

    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeLong(value.getTime().toLong())
    }

    override fun deserialize(decoder: Decoder): Date {
        return Date(decoder.decodeLong())
    }
}

public typealias DateAsMilliseconds = @Serializable(DateAsMillisecondsSerializer::class) Date
