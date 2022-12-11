package com.bkahlert.kommons.dom

import io.ktor.http.Parameters
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.encodeURLPath
import io.ktor.http.parseQueryString
import org.w3c.dom.url.URL as DomURL

/**
 * Very simple custom implementation due to non-working [DomURL.equals]
 * and missing ktor `data` [Url] support.
 */
data class URL(
    val schema: String,
    val host: String? = null,
    val port: Int? = null,
    val path: List<String> = emptyList(),
    val query: Parameters = Parameters.Empty,
    val fragment: Parameters = Parameters.Empty,
) {

    val encodedHost: String get() = host ?: ""
    val encodedPort: String get() = port?.let { ":$it" } ?: ""
    val encodedPath: String get() = path.joinToString("/") { it.encodeURLPath() }
    val encodedQuery: String get() = query.takeUnless { it.isEmpty() }?.let { "?${it.formUrlEncode()}" } ?: ""
    val encodedFragment: String get() = fragment.takeUnless { it.isEmpty() }?.let { "#${it.formUrlEncode()}" } ?: ""

    override fun toString(): String =
        if (schema == "data") {
            "$schema:${path.first()}"
        } else {
            buildString {
                append(schema)
                append("://")
                listOfNotNull("$encodedHost$encodedPort", if (path.isEmpty()) null else encodedPath, "$encodedQuery$encodedFragment".takeIf { it.isNotEmpty() })
                    .joinTo(this, "/")
            }
        }

    companion object {
        fun parse(url: String): URL = if (url.startsWith("data:")) {
            URL(
                schema = "data",
                host = null,
                path = listOf(url.removePrefix("data:")),
            )
        } else {
            Url(url).run {
                URL(
                    schema = protocol.name,
                    host = host,
                    port = port.takeUnless { it == protocol.defaultPort },
                    path = pathSegments.let {
                        val suffix = (encodedPathAndQuery + encodedFragment).removePrefix("/")
                        if (url.endsWith(suffix) && url.removeSuffix(suffix).last() == '/') it
                        else emptyList()
                    }.let {
                        if (it.size > 1) it.dropWhile { it.isEmpty() }
                        else it
                    },
                    query = parseQueryString(encodedQuery),
                    fragment = parseQueryString(encodedFragment),
                )
            }
        }
    }
}

fun CharSequence.toURL(): URL = URL.parse(toString())
fun CharSequence?.toURLOrNull(): URL? = kotlin.runCatching { this?.toURL() }.getOrNull()

inline operator fun Url.invoke(builder: URLBuilder.() -> Unit): Url = URLBuilder(toString()).apply(builder).build()

infix operator fun Url.div(path: String): Url = invoke { pathSegments = pathSegments + path }
