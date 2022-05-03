package com.bkahlert.kommons.dom

import io.ktor.http.Parameters
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.parseQueryString
import org.w3c.dom.url.URL as DomURL

/**
 * Very simple custom implementation due to non-working [DomURL.equals]
 * and missing ktor `data` [Url] support.
 */
data class URL(
    val schema: String,
    val host: String?,
    val path: String,
    val encodedQuery: String? = null,
    val encodedFragment: String? = null,
) {

    val parameters: Parameters by lazy {
        parseQueryString(encodedQuery ?: "")
    }

    val fragmentParameters: Parameters by lazy {
        parseQueryString(encodedFragment ?: "")
    }

    override fun toString(): String =
        if (schema == "data") {
            "$schema:$path"
        } else {
            buildString {
                append(schema)
                append("://")
                append(host ?: "")
                append(path)
                append(encodedQuery?.let { "?$it" } ?: "")
                append(encodedFragment?.let { "#$it" } ?: "")
            }
        }

    companion object {
        fun parse(url: String): URL = if (url.startsWith("data:")) {
            URL(
                schema = "data",
                host = null,
                path = url.removePrefix("data:")
            )
        } else {
            Url(url).let {
                it.parameters
                URL(
                    schema = it.protocol.name,
                    host = it.host,
                    path = it.encodedPath,
                    encodedQuery = it.encodedQuery.takeUnless(String::isEmpty),
                    encodedFragment = it.encodedFragment.takeUnless(String::isEmpty),
                )
            }
        }
    }
}

fun CharSequence.toURL(): URL = URL.parse(toString())
fun CharSequence?.toURLOrNull(): URL? = kotlin.runCatching { this?.toURL() }.getOrNull()

inline operator fun Url.invoke(builder: URLBuilder.() -> Unit): Url = URLBuilder(toString()).apply(builder).build()

infix operator fun Url.div(path: String): Url = invoke { pathSegments = pathSegments + path }
