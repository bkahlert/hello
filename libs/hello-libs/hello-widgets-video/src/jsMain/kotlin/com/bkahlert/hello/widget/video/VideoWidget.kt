package com.bkahlert.hello.widget.video

import com.bkahlert.hello.editor.UriLens
import com.bkahlert.hello.fritz2.lens
import com.bkahlert.hello.fritz2.orEmpty
import com.bkahlert.hello.widget.AspectRatio
import com.bkahlert.hello.widget.Widget
import com.bkahlert.hello.widget.WidgetEditor
import com.bkahlert.hello.widget.panel
import com.bkahlert.hello.widget.website.PermissionPolicy
import com.bkahlert.hello.widget.website.allow
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.toUriOrNull
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.Lens
import dev.fritz2.core.Tag
import dev.fritz2.core.allowFullscreen
import dev.fritz2.core.src
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement

@Serializable
public data class VideoWidget(
    override val id: String,
    override val title: String? = null,
    override val icon: Uri? = null,
    @SerialName("src") val src: Uri? = null,
) : Widget {

    override fun render(renderContext: Tag<Element>): HtmlTag<HTMLDivElement> = renderContext.panel(AspectRatio.video) {
        val missing = listOf(::src).filter { it.get().isNullOrBlank() }
        if (missing.isNotEmpty()) {
            renderConfigurationMissing(missing)
        } else {
            iframe("w-full h-full border-0") {
                src(src.toString())
                allow(
                    listOf(
                        PermissionPolicy.accelerometer,
                        PermissionPolicy.autoplay,
                        PermissionPolicy.`clipboard-write`,
                        PermissionPolicy.`encrypted-media`,
                        PermissionPolicy.gyroscope,
                        PermissionPolicy.`picture-in-picture`,
                    )
                )
                allowFullscreen(true)
            }
        }
    }

    override fun editor(isNew: Boolean): WidgetEditor<*> = VideoWidgetEditor(isNew, this)

    public companion object {
        public fun title(): Lens<VideoWidget, String> =
            VideoWidget::title.lens({ it.title }) { p, v -> p.copy(title = v) }.orEmpty()

        public fun src(): Lens<VideoWidget, String> =
            VideoWidget::src.lens({ it.src }, { p, v -> p.copy(src = v?.toUriOrNull()) }) + UriLens

        public fun icon(): Lens<VideoWidget, String> =
            VideoWidget::icon.lens({ it.icon }, { p, v -> p.copy(icon = v?.toUriOrNull()) }) + UriLens
    }
}
