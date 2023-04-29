package com.bkahlert.kommons.json

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder

/** [Json] instance with [lenientBuilderAction] applied to it. */
public val LenientJson: Json by lazy {
    Json {
        lenientBuilderAction()
        prettyPrint = false
    }
}

/** Pretty-printing [Json] instance with [lenientBuilderAction] applied to it. */
public val LenientAndPrettyJson: Json by lazy {
    Json {
        lenientBuilderAction()
        prettyPrint = true
    }
}

/**
 * A [JsonBuilder] action that
 * enables [JsonBuilder.isLenient] and [JsonBuilder.ignoreUnknownKeys], and
 * disables [JsonBuilder.explicitNulls].
 */
public val JsonBuilder.lenientBuilderAction: () -> Unit
    get() = {
        isLenient = true
        ignoreUnknownKeys = true
        explicitNulls = false
    }

/** @see LenientJson */
public val Json.Default.Lenient: Json get() = LenientJson

/** @see LenientAndPrettyJson */
public val Json.Default.LenientAndPretty: Json get() = LenientAndPrettyJson
