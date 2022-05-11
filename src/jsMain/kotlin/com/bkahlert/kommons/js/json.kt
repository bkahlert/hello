package com.bkahlert.kommons.js

import kotlinx.browser.document
import kotlinx.browser.window
import kotlin.js.Json
import kotlin.js.json

private fun sanitizeKey(key: String): String =
    key.replace("(.+)_\\d+".toRegex()) { it.groupValues.drop(1).first() }

external object Object {
    /**
     * Returns an array of a given object's own enumerable property names, iterated in the same order that a normal loop would.
     */
    fun keys(obj: Any): Array<String>

    /**
     * Returns an array of a given object's own enumerable string-keyed property [key, value] pairs.
     */
    fun entries(obj: Any): Array<Array<Any?>>

    /**
     * Returns an array of all properties (including non-enumerable properties except for those which use Symbol) found directly in a given object.
     */
    fun getOwnPropertyNames(obj: Any): Array<String>
}

/**
 * Returns an array of a `this` object's own enumerable property names, iterated in the same order that a normal loop would.
 */
val Any.keys: Array<String> get() = Object.keys(this)

/**
 * Returns an array of a `this` object's own enumerable string-keyed property [key, value] pairs.
 */
val Any.entries: Array<Array<Any?>> get() = Object.entries(this)

/**
 * Returns a [Map] of a `this` object's own enumerable string-keyed property [key, value] pairs.
 *
 * ***Important:** The property names are heuristically sanitized and can not reliably be used to
 * create copies of the original object.*
 */
val Any.properties: Map<String, Any?>
    get() = entries.associate {
        it[0].unsafeCast<String>() to it[1]
    }.mapKeys { (key, _) ->
        sanitizeKey(key)
    }

/** Returns a simple JavaScript object (as [Json]) using `this` key-value pairs as names and values of its properties. */
@Suppress("NOTHING_TO_INLINE")
inline fun Iterable<Pair<String, Any?>>.toJson(): Json =
    json(*map { (key, value) -> key to value }.toTypedArray())

/** Returns a simple JavaScript object (as [Json]) using entries of `this` map as names and values of its properties. */
@Suppress("NOTHING_TO_INLINE")
inline fun Map<String, Any?>.toJson(): Json = toList().toJson()

/** Returns an array of [Json] instances. */
fun Iterable<Any?>.toJsonArray(): Array<Json> = map { it.toJson() }.toTypedArray()

/** Returns an array of [Json] instances. */
fun Array<out Any?>.toJsonArray(): Array<Json> = map { it.toJson() }.toTypedArray()

/**
 * Returns `this` object stringified, that is,
 * [JSON.stringify] applied to its [Any.properties] and a couple of internal properties filtered.
 */
fun Any?.stringify(
    space: Int = 2,
    predicate: (Any?, String, Any?) -> Boolean = run {
        val ignoredKeyPrefixes = arrayOf("\$", "_", "coroutine", "jQuery")
        val ignoredKeyInfix = arrayOf("\$")
        val ignoredValues = arrayOf(window, document)
        ({ self, key, value ->
            ignoredKeyPrefixes.none { key.startsWith(it) } &&
                ignoredKeyInfix.none { key.contains(it) } &&
                ignoredValues.none<Any> { value === it } &&
                value !== self
        })
    }
): String {
    val o = when (this) {
        null -> null
        is Boolean -> this
        is Number -> this
        is String -> this
        is Array<*> -> this
        is List<*> -> toTypedArray()
        is Map<*, *> -> map { (key, value) -> sanitizeKey(key.toString()) to value }.toJson()
        else -> entries.map { sanitizeKey(it[0].unsafeCast<String>()) to it[1] }.toJson()
    }
    return JSON.stringify(
        o = o,
        replacer = { key, value ->
            val self: Any? = js("this").unsafeCast<Any?>()
            if (key.isEmpty()) {
                value
            } else if (predicate(self, key, value)) {
                value.stringify(space, predicate).parse()
            } else {
                undefined
            }
        },
        space = space,
    )
}

/** Returns a simple JavaScript object (as [Json]) by applying [JSON.parse] to `this` string. */
fun String.parse(): Json = when (this) {
    "null" -> json()
    else -> JSON.parse(this)
}

/** Returns a simple JavaScript object (as [Json]) by applying [JSON.stringify] to `this` object and [JSON.parse] to its output. */
fun Any?.toJson(): Json = stringify().parse()
