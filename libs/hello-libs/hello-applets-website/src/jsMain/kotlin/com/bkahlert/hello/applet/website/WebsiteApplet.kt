package com.bkahlert.hello.applet.website

import com.bkahlert.hello.applet.Applet
import com.bkahlert.hello.applet.AppletEditor
import com.bkahlert.hello.applet.AspectRatio
import com.bkahlert.hello.applet.panel
import com.bkahlert.hello.editor.UriLens
import com.bkahlert.hello.fritz2.lens
import com.bkahlert.hello.fritz2.orDefault
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
public data class WebsiteApplet(
    override val id: String,
    override val title: String? = null,
    override val icon: Uri? = null,
    @SerialName("src") val src: Uri? = null,
    @SerialName("aspect-ratio") val aspectRatio: AspectRatio? = AspectRatio.stretch,
    @SerialName("allow") val allow: List<PermissionPolicy>? = null,
    @SerialName("allow-fullscreen") val allowFullscreen: Boolean? = null,
    @SerialName("sandbox") val sandbox: List<ContentSecurityPolicy>? = null,
) : Applet {
    override fun editor(isNew: Boolean): AppletEditor<*> = WebsiteAppletEditor(isNew, this)

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

    public companion object {
        public fun title(): Lens<WebsiteApplet, String> =
            WebsiteApplet::title.lens({ it.title }) { p, v -> p.copy(title = v) }.orEmpty()

        public fun src(): Lens<WebsiteApplet, String> =
            WebsiteApplet::src.lens({ it.src }, { p, v -> p.copy(src = v?.toUriOrNull()) }) + UriLens

        public fun aspectRatio(): Lens<WebsiteApplet, AspectRatio?> =
            WebsiteApplet::aspectRatio.lens({ it.aspectRatio }, { p, v -> p.copy(aspectRatio = v) })

        public fun allow(): Lens<WebsiteApplet, List<PermissionPolicy>> =
            WebsiteApplet::allow.lens({ it.allow }, { p, v -> p.copy(allow = v) }).orEmpty()

        public fun allowFullscreen(): Lens<WebsiteApplet, Boolean> =
            WebsiteApplet::allowFullscreen.lens({ it.allowFullscreen }, { p, v -> p.copy(allowFullscreen = v) }).orDefault(false)

        public fun sandbox(): Lens<WebsiteApplet, List<ContentSecurityPolicy>> =
            WebsiteApplet::sandbox.lens({ it.sandbox }, { p, v -> p.copy(sandbox = v) }).orEmpty()
    }
}
