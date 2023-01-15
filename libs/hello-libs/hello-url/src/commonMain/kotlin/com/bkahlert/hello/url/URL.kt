package com.bkahlert.hello.url

import kotlinx.serialization.Serializable

/**
 * Simple custom implementation due to non-working [org.w3c.dom.url.URL.equals]
 * and missing ktor `data` [Url] support.
 */
@Serializable(with = UrlSerializer::class)
public data class URL(
    val schema: String,
    val host: String? = null,
    val port: Int? = null,
    val path: List<String> = emptyList(),
    val query: Parameters = EmptyParameters,
    val fragment: Parameters = EmptyParameters,
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
                append(encodedHost)
                append(encodedPort)
                if (path.isNotEmpty() || encodedQuery.isNotEmpty() || encodedFragment.isNotEmpty()) {
                    append("/")
                }
                if (path.isNotEmpty()) {
                    append(encodedPath)
                }
                append(encodedQuery)
                append(encodedFragment)
            }
        }

    public companion object {
        public fun parse(urlString: String): URL = if (urlString.startsWith("data:")) {
            URL(
                schema = "data",
                host = null,
                path = listOf(urlString.removePrefix("data:")),
            )
        } else {
            val parsed = kotlin.runCatching { Url(urlString) }.getOrElse { throw IllegalArgumentException(it) }
            with(parsed) {
                URL(
                    schema = protocol.name,
                    host = host,
                    port = port.takeUnless { it == protocol.defaultPort },
                    path = pathSegments.let {
                        val suffix = (encodedPathAndQuery + encodedFragment).removePrefix("/")
                        if (urlString.endsWith(suffix) && urlString.removeSuffix(suffix).last() == '/') it
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

public fun CharSequence.toURL(): URL = URL.parse(toString())
public fun CharSequence?.toURLOrNull(): URL? = kotlin.runCatching { this?.toURL() }.getOrNull()


public expect class URLProtocol {
    public val name: String
    public val defaultPort: Int
}

public expect class Url {
    public val protocol: URLProtocol
    public val host: String
    public val specifiedPort: Int
    public val pathSegments: List<String>
    public val parameters: Parameters
    public val fragment: String
    public val user: String?
    public val password: String?
    public val trailingQuery: Boolean

    public val port: Int
    public val encodedPathAndQuery: String
    public val encodedQuery: String
    public val encodedFragment: String
}

internal expect fun Url(urlString: String): Url

internal expect fun String.encodeURLPath(): String
