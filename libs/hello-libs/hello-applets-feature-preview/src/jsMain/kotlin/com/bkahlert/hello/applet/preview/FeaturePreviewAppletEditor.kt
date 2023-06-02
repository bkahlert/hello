package com.bkahlert.hello.applet.preview

import com.bkahlert.hello.applet.AppletEditor
import com.bkahlert.hello.applet.AspectRatio
import com.bkahlert.hello.editor.selectField
import dev.fritz2.core.RenderContext

public class FeaturePreviewAppletEditor(isNew: Boolean, applet: FeaturePreviewApplet) : AppletEditor<FeaturePreviewApplet>(isNew, applet) {
    override fun RenderContext.renderFields() {
        selectField(
            store = map(FeaturePreviewApplet.feature()),
            label = "Feature",
            itemTitle = FeaturePreview::title,
            itemIcon = FeaturePreview::icon,
        )
        selectField(
            store = map(FeaturePreviewApplet.aspectRatio()),
            label = "Aspect ratio",
            itemTitle = AspectRatio::title,
            itemIcon = AspectRatio::icon,
        )
    }
}
