package com.bkahlert.kommons.serialization

import com.bkahlert.kommons.Color
import com.bkahlert.kommons.Color.HSL
import com.bkahlert.kommons.Color.RGB
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializer(forClass = Color::class)
object ColorSerializer : KSerializer<Color> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "com.bkahlert.kommons.serialization.ColorSerializer", STRING)

    override fun serialize(encoder: Encoder, value: Color) {
        encoder.encodeString(value.toRGB().toString())
    }

    override fun deserialize(decoder: Decoder): Color {
        return Color.parseOrNull(decoder.decodeString()) ?: throw SerializationException()
    }
}

@Serializer(forClass = RGB::class)
object RgbSerializer : KSerializer<RGB> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "com.bkahlert.kommons.serialization.RGBSerializer", STRING)

    override fun serialize(encoder: Encoder, value: RGB) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): RGB {
        return RGB.parseOrNull(decoder.decodeString()) ?: throw SerializationException()
    }
}

@Serializer(forClass = HSL::class)
object HslSerializer : KSerializer<HSL> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "com.bkahlert.kommons.serialization.HSLSerializer", STRING)

    override fun serialize(encoder: Encoder, value: HSL) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): HSL {
        return HSL.parseOrNull(decoder.decodeString()) ?: throw SerializationException()
    }
}
