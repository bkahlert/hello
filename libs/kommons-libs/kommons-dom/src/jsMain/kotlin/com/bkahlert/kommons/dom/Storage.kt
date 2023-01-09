package com.bkahlert.kommons.dom

import com.bkahlert.kommons.json.deserialize
import com.bkahlert.kommons.json.serialize
import com.bkahlert.kommons.text.toKebabCasedString
import org.w3c.dom.get
import org.w3c.dom.set
import kotlin.reflect.KProperty
import org.w3c.dom.Storage as W3cStorage

/** Alias for [W3cStorage.removeItem] */
public inline fun W3cStorage.remove(key: String): Unit = removeItem(key)

/**
 * Variant of [W3cStorage.get] that deserializes the [Serializable] value after getting it.
 */
public inline fun <reified T> W3cStorage.getSerializable(key: String): T? = getItem(key)?.deserialize()

/**
 * Variant of [W3cStorage.set] that serializes the [Serializable] value before setting it
 * if it's not `null` and that invokes [W3cStorage.removeItem] if it is.
 */
public inline fun <reified T> W3cStorage.setSerializable(key: String, value: T?): Unit =
    value?.let { setItem(key, it.serialize(pretty = false)) } ?: removeItem(key)

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
    private val prefix = "$scope."
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

/** Variant of [Storage.get] that deserializes the [Serializable] value after getting it. */
public inline fun <reified T> Storage.getSerializable(key: String): T? = get(key)?.deserialize()

/** Variant of [Storage.set] that serializes the [Serializable] value before setting it. */
public inline fun <reified T> Storage.setSerializable(key: String, value: T?): Unit = set(key, value?.serialize(pretty = false))

/** Variant of [Storage.get] that deserializes the enum [E] instance using its [Enum.name]. */
public inline fun <reified E : Enum<E>> Storage.getEnum(key: String): E? = get(key)?.let { enumValueOf<E>(it) }

/** Variant of [Storage.set] that serializes the enum [E] instance using its [Enum.name]. */
public inline fun <reified E : Enum<E>> Storage.setEnum(key: String, value: E?): Unit = set(key, value?.name)

/** Removes all items from this storage. */
public fun Storage.clear(): Unit = keys.forEach { set(it, null) }

/**
 * Delegates the property to `this` [Storage] by using the property name
 * as the key and the serialization of the [Serializable] value
 * as the value.
 *
 * ### Usage
 * ```kotlin
 * class Foo(val storage: Storage) {
 *     val bar: String? by storage
 *     val baz: Baz? by storage
 * }
 * ```
 *
 * @see [Storage.default]
 * @see [StorageDelegate]
 */
public operator fun Storage.provideDelegate(thisRef: Any?, property: KProperty<*>): StorageDelegate =
    StorageDelegate(this)

/**
 * Delegates the property to `this` [Storage] by using the property name
 * as the key and the serialization of the [Serializable] value
 * as the value respectively the specified [defaultValue].
 *
 * ### Usage
 * ```kotlin
 * class Foo(val storage: Storage) {
 *     val bar: String by storage default "default value"
 *     val baz: Baz by storage default Baz.Default
 * }
 * ```
 *
 * @see [Storage.provideDelegate]
 * @see [StorageDelegateWithDefault]
 */
public infix fun <T : Any> Storage.default(defaultValue: T): StorageDelegateWithDefault<T> =
    StorageDelegateWithDefault(StorageDelegate(this), defaultValue)

/**
 * Delegate that reads and writes values using the specified [storage],
 * the [KProperty.name] as the key and the serialization of the [Serializable] value
 * as the value.
 */
public value class StorageDelegate(
    /** Storage to delegate to. */
    public val storage: Storage,
) {
    /**
     * Reads the value for the key with the name of the [property] from [storage]
     * and if set returns it deserialized to [T]. Strings are returned unchanged.
     */
    public inline operator fun <reified T> getValue(thisRef: Any?, property: KProperty<*>): T? {
        val name = property.name.toKebabCasedString()
        return when (T::class) {
            String::class -> storage[name]?.let { it as T? }
            else -> storage.getSerializable<T>(name)
        }
    }

    /**
     * Writes the serialized [value] for the key with the name of the [property] to [storage]
     * if not `null`. Otherwise, removes it. Strings are stored unchanged.
     */
    public inline operator fun <reified T> setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        val name = property.name.toKebabCasedString()
        when (T::class) {
            String::class -> storage[name] = value as String?
            else -> storage.setSerializable(name, value)
        }
    }
}

/**
 * Delegate that reads and writes values using the specified [delegate],
 * the [KProperty.name] as the key and the serialization of the [Serializable] value
 * as the value respectively the specified [defaultValue].
 */
public class StorageDelegateWithDefault<R>(
    /** The delegate this implementation delegates to. */
    public val delegate: StorageDelegate,
    /** Value to return if [delegate] returns `null`. */
    public val defaultValue: R,
) {
    /**
     * Returns the non-`null` value read using [delegate] or [defaultValue] otherwise.
     */
    public inline operator fun <reified T : R> getValue(thisRef: Any?, property: KProperty<*>): R =
        delegate.getValue<T>(thisRef, property) ?: defaultValue

    /**
     * Writes the [value] using [delegate].
     */
    public inline operator fun <reified T> setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        delegate.setValue(thisRef, property, value)
    }
}
