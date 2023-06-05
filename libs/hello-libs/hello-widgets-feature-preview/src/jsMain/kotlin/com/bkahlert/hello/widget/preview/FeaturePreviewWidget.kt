package com.bkahlert.hello.widget.preview

import com.bkahlert.hello.fritz2.lens
import com.bkahlert.hello.widget.AspectRatio
import com.bkahlert.hello.widget.Widget
import com.bkahlert.hello.widget.WidgetEditor
import com.bkahlert.hello.widget.panel
import com.bkahlert.kommons.uri.Uri
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.Lens
import dev.fritz2.core.Tag
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement

@Serializable
public data class FeaturePreviewWidget(
    override val id: String,
    @SerialName("feature") val feature: FeaturePreview? = null,
    @SerialName("aspect-ratio") val aspectRatio: AspectRatio? = AspectRatio.video,
) : Widget {
    override val title: String? get() = feature?.title?.plus(" (Demo)")
    override val icon: Uri? get() = feature?.icon

    override fun editor(isNew: Boolean): WidgetEditor<*> = FeaturePreviewWidgetEditor(isNew, this)

    override fun render(renderContext: Tag<Element>): HtmlTag<HTMLDivElement> = renderContext.panel(aspectRatio) {
        val missing = listOf(::feature).filter { it.get() == null }
        if (missing.isNotEmpty()) {
            renderConfigurationMissing(missing)
        } else {
            feature?.render?.invoke(this)
        }
    }

    public companion object {
        public fun feature(): Lens<FeaturePreviewWidget, FeaturePreview?> =
            FeaturePreviewWidget::feature.lens({ it.feature }, { p, v -> p.copy(feature = v) })

        public fun aspectRatio(): Lens<FeaturePreviewWidget, AspectRatio?> =
            FeaturePreviewWidget::aspectRatio.lens({ it.aspectRatio }, { p, v -> p.copy(aspectRatio = v) })
    }
}
