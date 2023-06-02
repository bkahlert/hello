package com.bkahlert.hello.applet.preview

import com.bkahlert.hello.applet.Applet
import com.bkahlert.hello.applet.AppletEditor
import com.bkahlert.hello.applet.AspectRatio
import com.bkahlert.hello.applet.panel
import com.bkahlert.hello.fritz2.lens
import com.bkahlert.kommons.uri.Uri
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.Lens
import dev.fritz2.core.Tag
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement

@Serializable
public data class FeaturePreviewApplet(
    override val id: String,
    @SerialName("feature") val feature: FeaturePreview? = null,
    @SerialName("aspect-ratio") val aspectRatio: AspectRatio? = AspectRatio.video,
) : Applet {
    override val title: String? get() = feature?.title
    override val icon: Uri? get() = feature?.icon

    override fun editor(isNew: Boolean): AppletEditor<*> = FeaturePreviewAppletEditor(isNew, this)

    override fun render(renderContext: Tag<Element>): HtmlTag<HTMLDivElement> = renderContext.panel(aspectRatio) {
        val missing = listOf(::feature).filter { it.get() == null }
        if (missing.isNotEmpty()) {
            renderConfigurationMissing(missing)
        } else {
            feature?.render?.invoke(this)
        }
    }

    public companion object {
        public fun feature(): Lens<FeaturePreviewApplet, FeaturePreview?> =
            FeaturePreviewApplet::feature.lens({ it.feature }, { p, v -> p.copy(feature = v) })

        public fun aspectRatio(): Lens<FeaturePreviewApplet, AspectRatio?> =
            FeaturePreviewApplet::aspectRatio.lens({ it.aspectRatio }, { p, v -> p.copy(aspectRatio = v) })
    }
}
