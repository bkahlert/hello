package com.bkahlert.hello.applet.image

import com.bkahlert.hello.applet.AppletEditor
import com.bkahlert.hello.applet.AspectRatio
import com.bkahlert.hello.editor.selectField
import com.bkahlert.hello.fritz2.mergeValidationMessages
import dev.fritz2.core.RenderContext
import dev.fritz2.core.placeholder
import dev.fritz2.core.required
import dev.fritz2.core.type
import dev.fritz2.core.values
import dev.fritz2.headless.components.inputField
import dev.fritz2.headless.foundation.setInitialFocus

public class ImageAppletEditor(isNew: Boolean, applet: ImageApplet) : AppletEditor<ImageApplet>(isNew, applet) {
    override fun RenderContext.renderFields() {
        inputField {
            value(map(ImageApplet.src()))
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
            val store = map(ImageApplet.title())
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
            store = map(ImageApplet.aspectRatio()),
            label = "Aspect ratio",
            itemTitle = AspectRatio::title,
            itemIcon = AspectRatio::icon,
        )
    }
}
