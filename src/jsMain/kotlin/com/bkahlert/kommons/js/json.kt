package com.bkahlert.kommons.js

import kotlin.js.Json
import kotlin.js.json

/** Returns a simple JavaScript object (as [Json]) using `this` key-value pairs as names and values of its properties. */
@Suppress("NOTHING_TO_INLINE")
inline fun Iterable<Pair<String, Any?>>.toJson(): Json = json(*toList().toTypedArray())

/** Returns a simple JavaScript object (as [Json]) using entries of `this` map as names and values of its properties. */
@Suppress("NOTHING_TO_INLINE")
inline fun Map<String, Any?>.toJson(): Json = toList().toJson()
