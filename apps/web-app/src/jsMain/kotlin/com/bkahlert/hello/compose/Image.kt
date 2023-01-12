package com.bkahlert.hello.compose

import io.ktor.http.Url
import org.jetbrains.compose.web.css.StyleScope
import org.jetbrains.compose.web.css.backgroundImage

interface Image {
    val dataURI: String
}

fun StyleScope.backgroundImage(image: Image) {
    backgroundImage("""url("${image.dataURI}")""")
}

fun StyleScope.backgroundImage(url: Url) {
    backgroundImage("""url("$url")""")
}
