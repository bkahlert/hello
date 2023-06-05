package com.bkahlert.hello.widget.website

import com.bkahlert.hello.editor.selectField
import com.bkahlert.hello.editor.selectMultipleField
import com.bkahlert.hello.editor.switchField
import com.bkahlert.hello.fritz2.mergeValidationMessages
import com.bkahlert.hello.metadata.Metadata
import com.bkahlert.hello.metadata.fetchMetadata
import com.bkahlert.hello.widget.AspectRatio
import com.bkahlert.hello.widget.WidgetEditor
import com.bkahlert.kommons.uri.host
import com.bkahlert.kommons.uri.toUriOrNull
import dev.fritz2.core.EmittingHandler
import dev.fritz2.core.RenderContext
import dev.fritz2.core.placeholder
import dev.fritz2.core.required
import dev.fritz2.core.type
import dev.fritz2.core.values
import dev.fritz2.headless.components.inputField
import dev.fritz2.headless.foundation.setInitialFocus
import kotlinx.coroutines.flow.debounce
import kotlin.time.Duration.Companion.seconds

public class WebsiteWidgetEditor(isNew: Boolean, widget: WebsiteWidget) : WidgetEditor<WebsiteWidget>(isNew, widget) {

    override val autocomplete: EmittingHandler<Unit, Metadata> = handleAndEmit { widget ->
        val uri = widget.src?.takeIf { it.host.orEmpty().contains(".") }
        if (uri != null) {
            autocompleting.track {
                uri.fetchMetadata()?.let { metadata ->
                    emit(metadata)
                    widget.copy(
                        title = metadata.title,
                        icon = metadata.favicon?.toUriOrNull() ?: widget.icon,
                    )
                }
            } ?: widget
        } else {
            widget
        }
    }

    override fun RenderContext.renderFields() {
        inputField {
            value(map(WebsiteWidget.src()))
            inputLabel {
                +"Source"
                inputTextfield {
                    type("url")
                    placeholder("https://example.com")
                    required(true)
                    setInitialFocus()
                    focuss handledBy { domNode.select() }
                    changes.debounce(0.2.seconds) handledBy autocomplete
                }.also(::mergeValidationMessages)
            }
        }
        inputField {
            val store = map(WebsiteWidget.title())
            value(store)
            inputLabel {
                +"Title"
                inputTextfield {
                    type("text")
                    placeholder("My favourite website")
                    keyups.values() handledBy store.update
                }.also(::mergeValidationMessages)
            }
        }
        selectField(
            store = map(WebsiteWidget.aspectRatio()),
            label = "Aspect ratio",
            itemTitle = AspectRatio::title,
            itemIcon = AspectRatio::icon,
        )

        selectMultipleField(
            store = map(WebsiteWidget.allow()),
            label = "Allow",
            itemTitle = PermissionPolicy::name,
        )
        switchField(
            store = map(WebsiteWidget.allowFullscreen()),
            label = "Allow fullscreen",
        )
        selectMultipleField(
            store = map(WebsiteWidget.sandbox()),
            label = "Sandbox",
            itemTitle = ContentSecurityPolicy::name,
        )
    }
}
