package com.bkahlert.hello.widget.website

import com.bkahlert.hello.editor.UriLens
import com.bkahlert.hello.fritz2.lens
import com.bkahlert.hello.fritz2.orDefault
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
public data class WebsiteWidget(
    override val id: String,
    override val title: String? = null,
    override val icon: Uri? = null,
    @SerialName("src") val src: Uri? = null,
    @SerialName("aspect-ratio") val aspectRatio: AspectRatio? = AspectRatio.stretch,
    @SerialName("allow") val allow: List<PermissionPolicy>? = null,
    @SerialName("allow-fullscreen") val allowFullscreen: Boolean? = null,
    @SerialName("sandbox") val sandbox: List<ContentSecurityPolicy>? = null,
) : Widget {

    override fun render(renderContext: Tag<Element>): HtmlTag<HTMLDivElement> = renderContext.panel(aspectRatio) {
        val missing = listOf(::src).filter { it.get().isNullOrBlank() }
        if (missing.isNotEmpty()) {
            renderConfigurationMissing(missing)
        } else {
            iframe("w-full h-full border-0") {
                src(src.toString())
                allow(allow)
                sandbox(sandbox)
            }
        }
    }

    override fun editor(isNew: Boolean): WidgetEditor<*> = WebsiteWidgetEditor(isNew, this)

    public companion object {
        public fun title(): Lens<WebsiteWidget, String> =
            WebsiteWidget::title.lens({ it.title }) { p, v -> p.copy(title = v) }.orEmpty()

        public fun src(): Lens<WebsiteWidget, String> =
            WebsiteWidget::src.lens({ it.src }, { p, v -> p.copy(src = v?.toUriOrNull()) }) + UriLens

        public fun aspectRatio(): Lens<WebsiteWidget, AspectRatio?> =
            WebsiteWidget::aspectRatio.lens({ it.aspectRatio }, { p, v -> p.copy(aspectRatio = v) })

        public fun allow(): Lens<WebsiteWidget, List<PermissionPolicy>> =
            WebsiteWidget::allow.lens({ it.allow }, { p, v -> p.copy(allow = v) }).orEmpty()

        public fun allowFullscreen(): Lens<WebsiteWidget, Boolean> =
            WebsiteWidget::allowFullscreen.lens({ it.allowFullscreen }, { p, v -> p.copy(allowFullscreen = v) }).orDefault(false)

        public fun sandbox(): Lens<WebsiteWidget, List<ContentSecurityPolicy>> =
            WebsiteWidget::sandbox.lens({ it.sandbox }, { p, v -> p.copy(sandbox = v) }).orEmpty()
    }
}
