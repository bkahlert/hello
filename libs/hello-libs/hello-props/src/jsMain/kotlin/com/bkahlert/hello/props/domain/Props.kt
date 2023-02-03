package com.bkahlert.hello.props.domain

import com.bkahlert.kommons.json.LenientAndPrettyJson
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject

@Serializable
public value class Props(
    public val content: JsonObject,
) {

    public val stringifiedValue: String
        get() = LenientAndPrettyJson.encodeToString(JsonObject.serializer(), content)

    public operator fun plus(prop: Pair<String, JsonElement>): Props =
        Props(JsonObject(content + prop))

    public operator fun minus(id: String): Props =
        Props(JsonObject(content - id))

    override fun toString(): String = buildString {
        append("Props ")
        append(stringifiedValue)
    }

    public companion object {

        /** Empty [Props]. */
        public val EMPTY: Props = Props(JsonObject(buildJsonObject { }))
    }
}
