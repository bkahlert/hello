package com.bkahlert.hello.compose

import com.bkahlert.hello.clickup.Image
import com.bkahlert.hello.color.Color
import io.ktor.http.Url
import org.jetbrains.compose.web.css.CSSBorder
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.StyleScope
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.backgroundImage
import org.jetbrains.compose.web.css.color

private fun Color.toCssColorValue() = object : CSSColorValue {
    override fun toString(): String = this@toCssColorValue.toString()
}

fun StyleScope.color(value: Color) = color(value.toCssColorValue())

fun StyleScope.backgroundColor(value: Color) = backgroundColor(value.toCssColorValue())

fun CSSBorder.color(value: Color) = color(value.toCssColorValue())


fun StyleScope.backgroundImage(image: Image) {
    backgroundImage("""url("${image.dataURI}")""")
}

fun StyleScope.backgroundImage(url: Url) {
    backgroundImage("""url("$url")""")
}
