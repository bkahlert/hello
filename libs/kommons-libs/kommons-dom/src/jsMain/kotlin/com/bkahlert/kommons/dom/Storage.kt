package com.bkahlert.kommons.dom

import org.w3c.dom.get
import org.w3c.dom.set
import kotlin.collections.Map.Entry
import kotlin.collections.MutableMap.MutableEntry
import org.w3c.dom.Storage as W3cStorage

// TODO test
public fun org.w3c.dom.Storage.asMap(): Map<String, String> = object : AbstractMap<String, String>() {
    override val entries: Set<Entry<String, String>>
        get() = buildSet {
            0.until(length).mapNotNull {
                val key = key(it)
                if (key != null) add(object : Entry<String, String> {
                    override val key: String get() = key
                    override val value: String get() = getItem(key) ?: throw NoSuchElementException("Item $key does not exist")
                })
            }
        }
}

public fun org.w3c.dom.Storage.asMutableMap(): MutableMap<String, String> = object : AbstractMutableMap<String, String>() {
    override val entries: MutableSet<MutableEntry<String, String>>
        get() = object : AbstractMutableSet<MutableEntry<String, String>>() {
            override val size: Int get() = length
            override fun iterator(): MutableIterator<MutableEntry<String, String>> = object : MutableIterator<MutableEntry<String, String>> {
                var index = 0
                override fun hasNext(): Boolean = index < length
                override fun next(): MutableEntry<String, String> {
                    val key = key(index++)
                    return if (key != null) {
                        object : MutableEntry<String, String> {
                            override val key: String get() = key
                            override val value: String get() = getItem(key) ?: throw NoSuchElementException("Item $key does not exist")
                            override fun setValue(newValue: String): String = value.also { setItem(key, newValue) }
                        }
                    } else throw NoSuchElementException("No more items")
                }

                override fun remove() {
                    val key = key(index--)
                    if (key != null) removeItem(key) else throw NoSuchElementException("No more items")
                }
            }

            override fun add(element: MutableEntry<String, String>): Boolean =
                if (this@asMutableMap.getItem(element.key) != null) false
                else {
                    this@asMutableMap.setItem(element.key, element.value)
                    true
                }
        }

    override fun put(key: String, value: String): String? = getItem(key).also { setItem(key, value) }
}

/** Alias for [W3cStorage.removeItem] */
public inline fun W3cStorage.remove(key: String): Unit = removeItem(key)

/**
 * Variant of [W3cStorage.get] that returns the enum [E] with matching [Enum.name].
 */
public inline fun <reified E : Enum<E>> W3cStorage.getEnum(key: String): E? = get(key)?.let { enumValueOf<E>(it) }

/**
 * Variant of [W3cStorage.set] that sets [Enum.name]
 * if [value] is not `null` and that invokes [W3cStorage.removeItem] if it is.
 */
public inline fun <reified E : Enum<E>> W3cStorage.setEnum(key: String, value: E): Unit = set(key, value.name)

/**
 * Returns an [Iterator] that enumerates all keys and their corresponding values
 * of `this` [W3cStorage].
 */
public operator fun W3cStorage.iterator(): Iterator<Pair<String, String?>> = iterator {
    var index = 0
    while (index < length) {
        val key = key(index)
        if (key != null) yield(key to getItem(key))
        index++
    }
}

/** Interface to store key-value pairs. */
public interface Storage {
    /** The keys that are currently used to store values. */
    public val keys: Set<String>

    /** Gets the value previously stored with the specified [key] or `null` otherwise. */
    public operator fun get(key: String): String?

    /** Sets the specified [value] using the specified [key] or removes it if [value] is `null`. */
    public operator fun set(key: String, value: String?)

    public companion object {
        /** Returns a [Storage] instance backed by the specified [storage]. */
        public fun of(storage: W3cStorage): Storage = W3cStorageAdapter(storage)
    }
}

/** [Storage] implementation that is backed by an in-memory [Map]. */
public class InMemoryStorage : Storage {
    private val map = mutableMapOf<String, String>()
    override val keys: Set<String> get() = map.keys
    override fun get(key: String): String? = map[key]
    override fun set(key: String, value: String?) {
        value?.let { map[key] = it } ?: map.remove(key)
    }
}

/** [Storage] implementation that is backed by the specified [storage]. */
private class W3cStorageAdapter(
    private val storage: W3cStorage,
    private val sanitizeKey: (String) -> String = { it },
) : Storage {
    override val keys: Set<String>
        get() = buildSet {
            var index = 0
            while (index < storage.length) {
                storage.key(index)?.also { add(it) }
                index++
            }
        }

    override operator fun get(key: String): String? = storage[sanitizeKey(key)]
    override operator fun set(key: String, value: String?): Unit = value?.let { storage[sanitizeKey(key)] = it } ?: storage.remove(sanitizeKey(key))
}

/**
 * [Storage] implementation that can be used to share a single [Storage] instance
 * among different parties by prefixing storage keys with the specified [scope].
 */
public class ScopedStorage(
    private val scope: String,
    private val storage: Storage,
) : Storage {
    private val prefix = "$scope:"
    override val keys: Set<String> get() = storage.keys.mapNotNull { if (it.startsWith(prefix)) it.removePrefix(prefix) else null }.toSet()
    override operator fun get(key: String): String? = storage["$prefix$key"]
    override operator fun set(key: String, value: String?): Unit = storage.set("$prefix$key", value)

    public companion object {
        /** Returns a [Storage] instance backed by `this` [Storage] using the specified [scope]. */
        public fun Storage.scoped(scope: String): Storage = ScopedStorage(scope, this)

        /** Returns a [Storage] instance backed by `this` [W3cStorage] using the specified [scope]. */
        public fun W3cStorage.scoped(scope: String): Storage = ScopedStorage(scope, Storage.of(this))
    }
}

/** Variant of [Storage.get] that deserializes the enum [E] instance using its [Enum.name]. */
public inline fun <reified E : Enum<E>> Storage.getEnum(key: String): E? = get(key)?.let { enumValueOf<E>(it) }

/** Variant of [Storage.set] that serializes the enum [E] instance using its [Enum.name]. */
public inline fun <reified E : Enum<E>> Storage.setEnum(key: String, value: E?): Unit = set(key, value?.name)

/** Removes all items from this storage. */
public fun Storage.clear(): Unit = keys.forEach { set(it, null) }
