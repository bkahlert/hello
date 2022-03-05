package com.bkahlert.kommons.runtime

import kotlinx.browser.localStorage
import kotlinx.browser.sessionStorage
import org.w3c.dom.get
import org.w3c.dom.set
import org.w3c.dom.Storage as NativeStorage

interface Storable {
    fun serialize(): String
}

interface Loadable<V> {
    fun load(serialized: String): V
}

open class Storage(private val nativeStorage: NativeStorage) {

    operator fun get(key: String): String? = nativeStorage[key]
    operator fun set(key: String, value: String): String = value.also { nativeStorage[key] = it }
//    fun getOrSet(key: String, value: String): String = get(key) ?: set(key, value)

    operator fun <E : Enum<E>> get(key: String, valueOf: (String) -> E): E? = nativeStorage[key]?.let { valueOf(it) }
    operator fun <E : Enum<E>> set(key: String, value: E): E = value.also { nativeStorage[key] = it.name }
//    fun <E : Enum<E>> getOrSet(key: String, value: E, valueOf: (String) -> E): E = get(key, valueOf) ?: set(key, value)
//
//    val inputState = remember { mutableStateOf(initialValue ?: "") }
//    val engineState = remember {
//        val engine = localStorage.get("engine") ?: initialEngine.name.also { localStorage.set("engine", it) }
//        mutableStateOf(Engine.valueOf(engine))
//    }
}

object LocalStorage : Storage(localStorage)
object SessionStorage : Storage(sessionStorage)
