package com.bkahlert.hello.url

import io.ktor.http.Url as ktorUrl
import io.ktor.http.encodeURLPath as ktorEncodeURLPath

public actual typealias URLProtocol = io.ktor.http.URLProtocol

public actual typealias Url = io.ktor.http.Url

internal actual fun Url(urlString: String): Url = ktorUrl(urlString)

internal actual fun String.encodeURLPath(): String = ktorEncodeURLPath()
