package com.bkahlert.hello.compose

open class SVGImage(
    /**
     * SVG markup
     */
    // language=SVG
    val svg: String,
) : Image {
    override val dataURI: String by lazy { "data:image/svg+xml,${encodeURIComponent(svg)}" }
}

external fun encodeURIComponent(uriComponent: String): String
