package com.bkahlert.hello.fritz2.components

import com.bkahlert.kommons.uri.DataUri
import com.bkahlert.kommons.uri.Uri
import dev.fritz2.core.RenderContext
import dev.fritz2.core.SvgTag
import dev.fritz2.core.viewBox
import dev.fritz2.headless.foundation.Aria
import io.ktor.http.ContentType.Image
import kotlinx.browser.document
import org.w3c.dom.asList
import org.w3c.dom.svg.SVGElement

public fun RenderContext.icon(
    classes: String?,
    uri: Uri,
    content: (SvgTag.() -> Unit)? = null,
): SvgTag = if (uri is DataUri && uri.mediaType?.match(Image.SVG) == true) {
    val svgElement = document.createElement("div").run {
        innerHTML = uri.data.decodeToString()
        firstElementChild as SVGElement
    }
    svg(classes) {
        svgElement.attributes.asList().forEach {
            attr(it.name, it.value)
        }
        content(svgElement.innerHTML)
        content?.invoke(this)
    }
} else {
    svg(classes) {
        attr("xmlns:xlink", "http://www.w3.org/1999/xlink")
        viewBox("0 0 24 24")
        attr(Aria.hidden, "true")
        content("""<image x="0" y="0" width="24" height="24" xlink:href="$uri"/>""")
        content?.invoke(this)
    }
}

public fun RenderContext.icon(
    uri: Uri,
    content: (SvgTag.() -> Unit)? = null,
): SvgTag = icon(null, uri, content)
