package com.bkahlert.kommons.runtime

import com.bkahlert.hello.deserialize
import com.bkahlert.hello.serialize
import kotlinx.browser.localStorage
import kotlinx.browser.sessionStorage
import org.w3c.dom.Storage
import org.w3c.dom.get
import org.w3c.dom.set

@Deprecated("use localStorage", replaceWith = ReplaceWith("localStorage", "kotlinx.browser.localStorage"))
val LocalStorage = localStorage

@Deprecated("use sessionStorage", replaceWith = ReplaceWith("sessionStorage", "kotlinx.browser.sessionStorage"))
val SessionStorage = sessionStorage

@Deprecated("use org.w3c.dom.get", replaceWith = ReplaceWith("get(key)", "org.w3c.dom.get"))
inline operator fun Storage.get(key: String): String? = getItem(key)

@Deprecated("use org.w3c.dom.set", replaceWith = ReplaceWith("set(key, value)", "org.w3c.dom.set"))
inline operator fun Storage.set(key: String, value: String) = setItem(key, value)

/** Alias for [Storage.removeItem] */
inline fun Storage.remove(key: String): Unit = removeItem(key)

/**
 * Variant of [Storage.get] that deserializes the [Serializable] value after getting it.
 */
inline fun <reified T> Storage.getSerializable(key: String): T? = getItem(key)?.deserialize()

/**
 * Variant of [Storage.set] that serializes the [Serializable] value before setting it
 * if it's not `null` and that invokes [Storage.removeItem] if it is.
 */
inline fun <reified T> Storage.setSerializable(key: String, value: T?): Unit =
    value?.let { setItem(key, it.serialize()) } ?: removeItem(key)

/**
 * Variant of [Storage.get] that returns the enum [E] with matching [Enum.name].
 */
inline fun <reified E : Enum<E>> Storage.getEnum(key: String): E? = get(key)?.let { enumValueOf<E>(it) }

/**
 * Variant of [Storage.set] that sets [Enum.name]
 * if [value] is not `null` and that invokes [Storage.removeItem] if it is.
 */
inline fun <reified E : Enum<E>> Storage.setEnum(key: String, value: E): Unit = set(key, value.name)
