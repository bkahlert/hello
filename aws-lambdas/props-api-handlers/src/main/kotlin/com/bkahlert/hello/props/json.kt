package com.bkahlert.hello.props

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject

val json by lazy {
    Json {
        ignoreUnknownKeys = true
    }
}

fun AttributeValue.toJsonElement(): JsonElement =
    json.parseToJsonElement(asS())

fun Map<String, AttributeValue>.toJsonObject(): JsonObject = buildJsonObject {
    forEach { (key: String, attr: AttributeValue) ->
        put(key, attr.toJsonElement())
    }
}

fun JsonObject.encodeToString() =
    json.encodeToString(this)
