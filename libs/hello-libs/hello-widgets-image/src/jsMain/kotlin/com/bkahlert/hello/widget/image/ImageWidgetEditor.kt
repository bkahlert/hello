package com.bkahlert.hello.widget.image

import com.bkahlert.hello.editor.selectField
import com.bkahlert.hello.fritz2.mergeValidationMessages
import com.bkahlert.hello.widget.AspectRatio
import com.bkahlert.hello.widget.WidgetEditor
import dev.fritz2.core.RenderContext
import dev.fritz2.core.placeholder
import dev.fritz2.core.required
import dev.fritz2.core.type
import dev.fritz2.core.values
import dev.fritz2.headless.components.inputField
import dev.fritz2.headless.foundation.setInitialFocus

public class ImageWidgetEditor(isNew: Boolean, widget: ImageWidget) : WidgetEditor<ImageWidget>(isNew, widget) {
    override fun RenderContext.renderFields() {
        inputField {
            value(map(ImageWidget.src()))
            inputLabel {
                +"Source"
                inputTextfield {
                    type("url")
                    placeholder("https://example.com")
                    required(true)
                    setInitialFocus()
                    focuss handledBy { domNode.select() }
                }.also(::mergeValidationMessages)
            }
        }
        inputField {
            val store = map(ImageWidget.title())
            value(store)
            inputLabel {
                +"Title"
                inputTextfield {
                    type("text")
                    placeholder("What happens in Vegas stays in Vegas")
                    keyups.values() handledBy store.update
                }.also(::mergeValidationMessages)
            }
        }
        selectField(
            store = map(ImageWidget.aspectRatio()),
            label = "Aspect ratio",
            itemTitle = AspectRatio::title,
            itemIcon = AspectRatio::icon,
        )
    }
}
