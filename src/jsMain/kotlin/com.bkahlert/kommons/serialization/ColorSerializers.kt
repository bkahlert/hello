package com.bkahlert.kommons.serialization

import com.bkahlert.kommons.Color
import com.bkahlert.kommons.Color.HSL
import com.bkahlert.kommons.Color.RGB
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ColorSerializer : KSerializer<Color> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Color", STRING)

    override fun serialize(encoder: Encoder, value: Color) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Color {
        return Color.parseOrNull(decoder.decodeString()) ?: throw SerializationException()
    }
}

object RgbSerializer : KSerializer<RGB> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("RGB", STRING)

    override fun serialize(encoder: Encoder, value: RGB) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): RGB {
        return RGB.parseOrNull(decoder.decodeString().also { println(it) }) ?: throw SerializationException()
    }
}
//
//object RgbSerializer : KSerializer<RGB> {
//    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Color", STRING)
//
//    override fun serialize(encoder: Encoder, value: RGB) {
//        val string = value.toString()
//        encoder.encodeString(string)
//    }
//
//    override fun deserialize(decoder: Decoder): RGB {
//        val string = decoder.decodeString()
//        return RGB(string)
//    }
//}

object HslSerializer : KSerializer<HSL> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("HSL", STRING)

    override fun serialize(encoder: Encoder, value: HSL) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): HSL {
        return HSL.parseOrNull(decoder.decodeString()) ?: throw SerializationException()
    }
}
