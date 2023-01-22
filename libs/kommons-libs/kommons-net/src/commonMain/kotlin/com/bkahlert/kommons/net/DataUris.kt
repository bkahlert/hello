package com.bkahlert.kommons.net

import io.ktor.http.ContentType.Image.SVG

/**
 * Returns the specified [markup] as a
 * [SVG]-typed [DataUri].
 */
public fun DataUri.Companion.Svg(markup: String): DataUri =
    DataUri(SVG, markup.encodeToByteArray())

// TODO add support for [SVG fragment identifiers](https://www.w3.org/TR/SVG11/linking.html#SVGFragmentIdentifiers)
