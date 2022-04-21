package com.bkahlert.kommons.dom

import io.ktor.http.URLBuilder
import io.ktor.http.Url
import org.w3c.dom.url.URL

fun CharSequence.toUrl(): URL =
    this.toString().let { kotlin.runCatching { URL(it) } }.getOrThrow()

fun CharSequence?.toUrlOrNull(): URL? =
    this?.toString()?.let { kotlin.runCatching { URL(it) } }?.getOrNull()

inline operator fun Url.invoke(builder: URLBuilder.() -> Unit): Url = URLBuilder(toString()).apply(builder).build()

infix operator fun Url.div(path: String): Url = invoke { pathSegments = pathSegments + path }
