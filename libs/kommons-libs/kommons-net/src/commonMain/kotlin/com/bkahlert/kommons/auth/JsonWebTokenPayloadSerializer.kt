package com.bkahlert.kommons.auth

import com.bkahlert.kommons.auth.JsonWebTokenPayload.AccessTokenPayload
import com.bkahlert.kommons.auth.JsonWebTokenPayload.IdTokenPayload
import com.bkahlert.kommons.json.LenientJson
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject

public object JsonWebTokenPayloadSerializer : KSerializer<JsonWebTokenPayload> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "com.bkahlert.kommons.auth.JsonWebTokenPayloadSerializer", STRING
    )

    override fun serialize(encoder: Encoder, value: JsonWebTokenPayload) {
        encoder.encodeString(LenientJson.encodeToString(value))
    }

    override fun deserialize(decoder: Decoder): JsonWebTokenPayload {
        val jsonElement = LenientJson.parseToJsonElement(decoder.decodeString())
        val jsonObject = jsonElement.jsonObject
        return when (val tokenUse = jsonObject["token_use"]?.toString()) {
            "id" -> LenientJson.decodeFromJsonElement<IdTokenPayload>(jsonObject)
            "access" -> LenientJson.decodeFromJsonElement<AccessTokenPayload>(jsonObject)
            else -> error("Unexpected token_use: $tokenUse")
        }
    }
}
