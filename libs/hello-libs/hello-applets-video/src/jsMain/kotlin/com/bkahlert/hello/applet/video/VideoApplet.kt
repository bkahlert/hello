package com.bkahlert.hello.applet.video

import com.bkahlert.hello.applet.Applet
import com.bkahlert.hello.applet.AppletEditor
import com.bkahlert.hello.applet.AspectRatio
import com.bkahlert.hello.applet.panel
import com.bkahlert.hello.applet.website.PermissionPolicy
import com.bkahlert.hello.applet.website.allow
import com.bkahlert.hello.editor.UriLens
import com.bkahlert.hello.fritz2.lens
import com.bkahlert.hello.fritz2.orEmpty
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
public data class VideoApplet(
    override val id: String,
    override val title: String? = null,
    override val icon: Uri? = null,
    @SerialName("src") val src: Uri? = null,
) : Applet {
    override fun editor(isNew: Boolean): AppletEditor<*> = VideoAppletEditor(isNew, this)

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

    public companion object {
        public fun title(): Lens<VideoApplet, String> =
            VideoApplet::title.lens({ it.title }) { p, v -> p.copy(title = v) }.orEmpty()

        public fun src(): Lens<VideoApplet, String> =
            VideoApplet::src.lens({ it.src }, { p, v -> p.copy(src = v?.toUriOrNull()) }) + UriLens

        public fun icon(): Lens<VideoApplet, String> =
            VideoApplet::icon.lens({ it.icon }, { p, v -> p.copy(icon = v?.toUriOrNull()) }) + UriLens
    }
}
