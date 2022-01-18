package com.bkahlert.kommons

class SVGImage(val svg: String) : Image {
    override val dataURI: String by lazy { "data:image/svg+xml,${encodeURIComponent(svg)}" }
}

external fun encodeURIComponent(uriComponent: String): String
