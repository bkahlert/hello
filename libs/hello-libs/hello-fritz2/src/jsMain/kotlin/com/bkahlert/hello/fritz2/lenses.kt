package com.bkahlert.hello.fritz2

import dev.fritz2.core.Lens
import dev.fritz2.core.lensOf
import kotlin.reflect.KProperty1

public inline fun <P, T> lensOf(property: KProperty1<P, T>, crossinline getter: (P) -> T, crossinline setter: (P, T) -> P): Lens<P, T> =
    lensOf(property.name, getter, setter)

public fun <P, T> KProperty1<P, T>.lens(get: (P) -> T, set: (P, T) -> P): Lens<P, T> =
    lensOf(this, get, set)

public fun <T> defaultLens(default: T, id: String = ""): Lens<T?, T> =
    lensOf(id, { p -> p ?: default }, { _, v -> v.takeUnless { it == default } })

public fun <T, X> Lens<T, X?>.orDefault(default: X): Lens<T, X> = plus(defaultLens(default))

public fun <T> Lens<T, String?>.orEmpty(): Lens<T, String> = orDefault("")
public fun <T, E> Lens<T, List<E>?>.orEmpty(): Lens<T, List<E>> = orDefault(emptyList())


public fun <A, B> lensForFirst(): Lens<Pair<A, B>, A> = lensOf("first", Pair<A, B>::first) { p, v -> v to p.second }
public fun <A, B> lensForSecond(): Lens<Pair<A, B>, B> = lensOf("second", Pair<A, B>::second) { p, v -> p.first to v }


/* Entry */

private fun <K, V> Entry(key: K, value: V) = object : Map.Entry<K, V> {
    override val key: K = key
    override val value: V = value
}

public fun <K, V> lensForKey(): Lens<Map.Entry<K, V>, K> = lensOf("key", Map.Entry<K, V>::key) { p, v -> Entry(v, p.value) }
public fun <K, V> lensForValue(): Lens<Map.Entry<K, V>, V> = lensOf("value", Map.Entry<K, V>::value) { p, v -> Entry(p.key, v) }


/* Map */

public fun <K, V> lensForElementOrNull(key: K): Lens<Map<K, V>, V?> = object : Lens<Map<K, V>, V?> {
    override val id: String = key.toString()
    override fun get(parent: Map<K, V>): V? = parent[key]
    override fun set(parent: Map<K, V>, value: V?): Map<K, V> = if (value != null) parent + (key to value) else parent
}

public fun <K, V> lensForElementOrDefault(key: K, defaultValue: V): Lens<Map<K, V>, V> = object : Lens<Map<K, V>, V> {
    override val id: String = key.toString()
    override fun get(parent: Map<K, V>): V = parent[key] ?: defaultValue
    override fun set(parent: Map<K, V>, value: V): Map<K, V> = if (value != defaultValue) parent + (key to value) else parent
}
