package com.bkahlert.kommons.js

import kotlin.js.Json
import kotlin.js.json

/**
 * Returns a simple JavaScript object (as [Json]) using
 * the key-value pairs of the specified [map] as names and values of its properties.
 */
public fun json(map: Map<String, Any?>): Json =
    json(*map.toList().toTypedArray())
