package com.bkahlert.kommons.json

import kotlinx.serialization.KSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


// TODO refactor to make it look more like kotlinx.serialization, e.g. `encodeToString`
public inline fun <reified T> T.serialize(pretty: Boolean = false): String =
    (if (pretty) Json.LenientAndPretty else Json.Lenient).encodeToString(this)

public fun <T> T.serialize(serializer: KSerializer<T>, pretty: Boolean = false): String =
    (if (pretty) Json.LenientAndPretty else Json.Lenient).encodeToString(serializer, this)

public inline fun <reified T> String.deserialize(): T =
    Json.Lenient.decodeFromString(trimIndent())

public fun <T> String.deserialize(serializer: KSerializer<T>): T =
    Json.Lenient.decodeFromString(serializer, trimIndent())
