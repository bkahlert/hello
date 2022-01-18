package com.bkahlert.kommons

import org.jetbrains.compose.web.css.StyleBuilder
import org.jetbrains.compose.web.css.backgroundImage

interface Image {
    val dataURI: String
}

fun StyleBuilder.backgroundImage(image: Image) {
    backgroundImage("""url("${image.dataURI}")""")
}
