package com.bkahlert.kommons.net

import io.ktor.http.ContentType.Image.SVG

/**
 * Returns the specified [markup] as a
 * [SVG]-typed [DataUri].
 */
public fun DataUri.Companion.Svg(markup: String): DataUri =
    DataUri(SVG, markup.encodeToByteArray())

// TODO implement (Data)URLStreamHandlerProvider for Java
// TODO java.net.URI = Uri
// TODO private operator fun URI.div(path: String) = URI(scheme, userInfo, host, port, "${this.path}/$path", query, fragment)


// TODO add support for [SVG fragment identifiers](https://www.w3.org/TR/SVG11/linking.html#SVGFragmentIdentifiers)
