package com.bkahlert.kommons.color

import com.bkahlert.kommons.ValueRange.Normalized
import com.bkahlert.kommons.color.Color.HSL
import com.bkahlert.kommons.color.Color.RGB
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializer(forClass = Color::class)
public object ColorSerializer : KSerializer<Color> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "com.bkahlert.hello.color.ColorSerializer", PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: Color) {
        encoder.encodeString((value.takeIf { (it as? HSL)?.alpha != Normalized.endInclusive }?.toRGB() ?: value).toString())
    }

    override fun deserialize(decoder: Decoder): Color {
        return Color.parseOrNull(decoder.decodeString()) ?: throw SerializationException()
    }
}

@Serializer(forClass = RGB::class)
public object RgbSerializer : KSerializer<RGB> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "com.bkahlert.hello.color.RGBSerializer", PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: RGB) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): RGB {
        return RGB.parseOrNull(decoder.decodeString()) ?: throw SerializationException()
    }
}

@Serializer(forClass = HSL::class)
public object HslSerializer : KSerializer<HSL> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "com.bkahlert.hello.color.HSLSerializer", PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: HSL) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): HSL {
        return HSL.parseOrNull(decoder.decodeString()) ?: throw SerializationException()
    }
}
