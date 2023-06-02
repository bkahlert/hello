package com.bkahlert.hello.applet.image

import com.bkahlert.hello.applet.Applet
import com.bkahlert.hello.applet.AppletEditor
import com.bkahlert.hello.applet.AspectRatio
import com.bkahlert.hello.applet.panel
import com.bkahlert.hello.editor.UriLens
import com.bkahlert.hello.fritz2.lens
import com.bkahlert.hello.fritz2.orEmpty
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.toUriOrNull
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.Lens
import dev.fritz2.core.Tag
import dev.fritz2.core.src
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement

@Serializable
public data class ImageApplet(
    override val id: String,
    @SerialName("src") val src: Uri? = null,
    @SerialName("aspect-ratio") val aspectRatio: AspectRatio? = AspectRatio.video,
    override val title: String? = src?.path?.substringAfterLast('/'),
) : Applet {

    override fun editor(isNew: Boolean): AppletEditor<*> = ImageAppletEditor(isNew, this)

    override fun render(renderContext: Tag<Element>): HtmlTag<HTMLDivElement> = renderContext.panel(aspectRatio) {
        val missing = listOf(::src).filter { it.get().isNullOrBlank() }
        if (missing.isNotEmpty()) {
            renderConfigurationMissing(missing)
        } else {
            img { src(src.toString()) }
        }
    }

    public companion object {
        public fun title(): Lens<ImageApplet, String> =
            ImageApplet::title.lens({ it.title }) { p, v -> p.copy(title = v) }.orEmpty()

        public fun src(): Lens<ImageApplet, String> =
            ImageApplet::src.lens({ it.src }, { p, v -> p.copy(src = v?.toUriOrNull()) }) + UriLens

        public fun aspectRatio(): Lens<ImageApplet, AspectRatio?> =
            ImageApplet::aspectRatio.lens({ it.aspectRatio }, { p, v -> p.copy(aspectRatio = v) })
    }
}
