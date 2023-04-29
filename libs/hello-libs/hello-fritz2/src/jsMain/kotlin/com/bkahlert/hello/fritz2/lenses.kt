package com.bkahlert.hello.fritz2

import com.bkahlert.kommons.uri.Authority
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.toUriOrNull
import dev.fritz2.core.Lens
import kotlin.reflect.KProperty1

public inline fun <P, T> lensOf(property: KProperty1<P, T>, crossinline getter: (P) -> T, crossinline setter: (P, T) -> P): Lens<P, T> =
    dev.fritz2.core.lensOf(property.name, getter, setter)

public fun <P, T> KProperty1<P, T>.lens(get: (P) -> T, set: (P, T) -> P): Lens<P, T> =
    lensOf(this, get, set)

public object UriLens : Lens<Uri?, String> {
    override val id: String get() = ""
    override fun get(parent: Uri?): String = parent?.toString().orEmpty()
    override fun set(parent: Uri?, value: String): Uri? = value.takeUnless { it.isBlank() }?.toUriOrNull()?.run {
        if (listOfNotNull(scheme, authority, path, query, fragment).size == 1 && path.contains(".")) {
            Uri("https", Authority(null, path.substringBefore("/", ""), null), path.substringAfter("/"), null, null)
        } else {
            this
        }
    }
}

public object IntLens : Lens<Int?, String> {
    override val id: String get() = ""
    override fun get(parent: Int?): String = parent?.toString().orEmpty()
    override fun set(parent: Int?, value: String): Int? = value.toIntOrNull()
}

public fun <T> defaultLens(id: String, default: T): Lens<T?, T> = object : Lens<T?, T> {
    override val id: String = id
    override fun get(parent: T?): T = parent ?: default
    override fun set(parent: T?, value: T): T? = value.takeUnless { it == default }
}

public fun <T, X> Lens<T, X?>.orDefault(default: X): Lens<T, X> = plus(defaultLens("", default))

public fun <T> Lens<T, String?>.orEmpty(): Lens<T, String> = orDefault("")
public fun <T, E> Lens<T, List<E>?>.orEmpty(): Lens<T, List<E>> = orDefault(emptyList())
