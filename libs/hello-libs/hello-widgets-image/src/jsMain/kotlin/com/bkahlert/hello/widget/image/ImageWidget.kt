package com.bkahlert.hello.widget.image

import com.bkahlert.hello.editor.UriLens
import com.bkahlert.hello.fritz2.lens
import com.bkahlert.hello.fritz2.orEmpty
import com.bkahlert.hello.widget.AspectRatio
import com.bkahlert.hello.widget.Widget
import com.bkahlert.hello.widget.WidgetEditor
import com.bkahlert.hello.widget.panel
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
public data class ImageWidget(
    override val id: String,
    @SerialName("src") val src: Uri? = null,
    @SerialName("aspect-ratio") val aspectRatio: AspectRatio? = AspectRatio.video,
    override val title: String? = src?.path?.substringAfterLast('/'),
) : Widget {

    override fun render(renderContext: Tag<Element>): HtmlTag<HTMLDivElement> = renderContext.panel(aspectRatio) {
        val missing = listOf(::src).filter { it.get().isNullOrBlank() }
        if (missing.isNotEmpty()) {
            renderConfigurationMissing(missing)
        } else {
            img { src(src.toString()) }
        }
    }

    override fun editor(isNew: Boolean): WidgetEditor<*> = ImageWidgetEditor(isNew, this)

    public companion object {
        public fun title(): Lens<ImageWidget, String> =
            ImageWidget::title.lens({ it.title }) { p, v -> p.copy(title = v) }.orEmpty()

        public fun src(): Lens<ImageWidget, String> =
            ImageWidget::src.lens({ it.src }, { p, v -> p.copy(src = v?.toUriOrNull()) }) + UriLens

        public fun aspectRatio(): Lens<ImageWidget, AspectRatio?> =
            ImageWidget::aspectRatio.lens({ it.aspectRatio }, { p, v -> p.copy(aspectRatio = v) })
    }
}
