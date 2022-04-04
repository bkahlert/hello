package com.bkahlert.kommons.dom

import io.ktor.http.URLBuilder
import io.ktor.http.Url

fun CharSequence.toUrl(): Url =
    this.toString().let { kotlin.runCatching { Url(it) } }.getOrThrow()

fun CharSequence?.toUrlOrNull(): Url? =
    this?.toString()?.let { kotlin.runCatching { Url(it) } }?.getOrNull()

inline operator fun Url.invoke(builder: URLBuilder.() -> Unit): Url = URLBuilder(this).apply(builder).build()

infix operator fun Url.div(path: String): Url = invoke { pathSegments = pathSegments + path }
