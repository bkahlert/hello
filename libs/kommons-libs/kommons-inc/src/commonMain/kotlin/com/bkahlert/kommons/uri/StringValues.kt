package com.bkahlert.kommons.uri

import io.ktor.util.StringValues
import kotlin.reflect.KProperty

/**
 * Returns all values of the parameter with the name of the delegates [property]:
 * - each value corresponds to one parameter occurrence (for example `name=value1&name=value2`)
 * - an empty list represents a parameter with no value (for example `#name`)
 * - `null` represents no parameter with name at all
 */
public operator fun StringValues.getValue(thisRef: Any?, property: KProperty<*>): List<String>? =
    getAll(property.name)
