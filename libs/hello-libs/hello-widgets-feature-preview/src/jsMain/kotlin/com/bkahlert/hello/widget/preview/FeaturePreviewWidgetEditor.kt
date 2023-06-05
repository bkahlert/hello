package com.bkahlert.hello.widget.preview

import com.bkahlert.hello.editor.selectField
import com.bkahlert.hello.widget.AspectRatio
import com.bkahlert.hello.widget.WidgetEditor
import dev.fritz2.core.RenderContext

public class FeaturePreviewWidgetEditor(isNew: Boolean, widget: FeaturePreviewWidget) : WidgetEditor<FeaturePreviewWidget>(isNew, widget) {
    override fun RenderContext.renderFields() {
        selectField(
            store = map(FeaturePreviewWidget.feature()),
            label = "Feature",
            itemTitle = FeaturePreview::title,
            itemIcon = FeaturePreview::icon,
        )
        selectField(
            store = map(FeaturePreviewWidget.aspectRatio()),
            label = "Aspect ratio",
            itemTitle = AspectRatio::title,
            itemIcon = AspectRatio::icon,
        )
    }
}
