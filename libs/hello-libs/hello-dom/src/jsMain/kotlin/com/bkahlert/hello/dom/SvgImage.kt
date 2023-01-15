package com.bkahlert.hello.dom

public open class SvgImage(
    /**
     * SVG markup
     */
    // language=SVG
    public val svg: String,
) : Image {
    override val dataURI: String by lazy { "data:image/svg+xml,${encodeURIComponent(svg)}" }
}

private external fun encodeURIComponent(uriComponent: String): String
