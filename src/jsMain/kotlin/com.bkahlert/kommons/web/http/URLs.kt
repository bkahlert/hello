package com.bkahlert.kommons.web.http

import io.ktor.http.URLBuilder
import io.ktor.http.Url
import org.w3c.dom.Location

fun CharSequence.toUrl(): Url =
    this.toString().let { kotlin.runCatching { Url(it) } }.getOrThrow()

fun CharSequence?.toUrlOrNull(): Url? =
    this?.toString()?.let { kotlin.runCatching { Url(it) } }?.getOrNull()

var Location.url: Url
    get() = href.toUrl()
    set(value) {
        href = value.toString()
    }


inline operator fun Url.invoke(builder: URLBuilder.() -> Unit): Url = URLBuilder(this).apply(builder).build()

inline infix operator fun Url.div(path: String): Url = invoke { pathSegments = pathSegments + path }

inline infix operator fun Url.div(path: Number): Url = this / path.toString()