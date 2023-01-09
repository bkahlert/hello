package com.bkahlert.kommons.json

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder

/** [Json] instance with [lenientBuilderAction] applied to it. */
@Suppress("UnusedReceiverParameter")
public val Json.Lenient: Json get() = LenientJson

/** Pretty-printing [Json] instance with [lenientBuilderAction] applied to it. */
@Suppress("UnusedReceiverParameter")
public val Json.LenientAndPretty: Json get() = LenientAndPrettyJson

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

private val LenientJson: Json by lazy {
    Json {
        lenientBuilderAction()
        prettyPrint = false
    }
}

private val LenientAndPrettyJson: Json by lazy {
    Json {
        lenientBuilderAction()
        prettyPrint = true
    }
}
