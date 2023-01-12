package com.bkahlert.hello.url

import com.bkahlert.hello.url.URL
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializer(URL::class)
public object UrlSerializer : KSerializer<URL> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "com.bkahlert.hello.url.UrlSerializer", STRING
    )

    override fun serialize(encoder: Encoder, value: URL) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): URL {
        return URL.parse(decoder.decodeString())
    }
}
