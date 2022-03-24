package com.bkahlert.kommons

import io.ktor.http.Url

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

fun Url.asAvatar() = SVGImage(
    //language=SVG
    """
    <svg xmlns="http://www.w3.org/2000/svg" version="1.1" width="100" height="100">
        <defs>
            <clipPath id="circular-cut">
                <circle cx="50" cy="50" r="34"/>
            </clipPath>
        </defs>
        <image href="$this" width="100" height="100" clip-path="url(#circular-cut)" crossorigin="anonymous"/>
        <rect x='20' y='20' width='30' height='30' fill='#ff0000'/>
    </svg>
    """.trimIndent()
)
