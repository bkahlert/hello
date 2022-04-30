package com.bkahlert.kommons.dom

import io.ktor.http.URLBuilder
import io.ktor.http.Url
import org.w3c.dom.url.URL as DomURL

/**
 * Very simple custom implementation due to non-working [DomURL.equals]
 * and missing ktor `data` [Url] support.
 */
data class URL(
    val schema: String,
    val host: String?,
    val path: String,
    val query: String? = null,
) {
    override fun toString(): String =
        if (schema == "data") "$schema:$path" else "$schema://${host ?: ""}$path${query?.let { "?$it" } ?: ""}"

    companion object {
        fun parse(url: String): URL = if (url.startsWith("data:")) {
            URL(
                schema = "data",
                host = null,
                path = url.removePrefix("data:")
            )
        } else {
            Url(url).let { URL(
                schema = it.protocol.name,
                host = it.host,
                path = it.encodedPath,
                query = it.encodedQuery.takeUnless(String::isEmpty)
            ) }
        }
    }
}

fun CharSequence.toURL(): URL =
    this.toString().let { kotlin.runCatching { URL.parse(it) } }.getOrThrow()

fun CharSequence?.toURLOrNull(): URL? =
    this?.toString()?.let { kotlin.runCatching { URL.parse(it) } }?.getOrNull()

inline operator fun Url.invoke(builder: URLBuilder.() -> Unit): Url = URLBuilder(toString()).apply(builder).build()

infix operator fun Url.div(path: String): Url = invoke { pathSegments = pathSegments + path }
