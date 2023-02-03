package com.bkahlert.kommons.uri

import io.ktor.http.ContentType
import io.ktor.http.charset
import io.ktor.http.withCharsetIfNeeded
import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.charsets.Charsets
import io.ktor.utils.io.core.toByteArray

/**
 * Returns a new [DataUri] with the specified [mediaType] and textual [data].
 *
 * The data is encoded with the [Charset] included in the [mediaType], or
 * `UTF-8` otherwise.
 */
public fun DataUri(mediaType: ContentType, data: String): DataUri {
    val charset: Charset = mediaType.charset() ?: Charsets.UTF_8
    return DataUri(
        mediaType = mediaType.withCharsetIfNeeded(charset),
        data = data.toByteArray(charset),
    )
}

/**
 * Returns a new [DataUri] with the specified [mediaType]
 * and the return value of the [init] encoded to Base64.
 *
 * The data is encoded with the [Charset] included in the [mediaType], or
 * `UTF-8` otherwise.
 */
public fun DataUri(mediaType: ContentType, init: () -> String): DataUri =
    DataUri(mediaType, init())
