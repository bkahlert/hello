package com.bkahlert.hello.applet.website

import com.bkahlert.hello.applet.AppletEditor
import com.bkahlert.hello.applet.AspectRatio
import com.bkahlert.hello.editor.selectField
import com.bkahlert.hello.editor.selectMultipleField
import com.bkahlert.hello.editor.switchField
import com.bkahlert.hello.fritz2.mergeValidationMessages
import com.bkahlert.hello.metadata.Metadata
import com.bkahlert.hello.metadata.fetchMetadata
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

public class WebsiteAppletEditor(isNew: Boolean, applet: WebsiteApplet) : AppletEditor<WebsiteApplet>(isNew, applet) {

    override val autocomplete: EmittingHandler<Unit, Metadata> = handleAndEmit { applet ->
        val uri = applet.src?.takeIf { it.host.orEmpty().contains(".") }
        if (uri != null) {
            autocompleting.track {
                uri.fetchMetadata()?.let { metadata ->
                    emit(metadata)
                    applet.copy(
                        title = metadata.title,
                        icon = metadata.favicon?.toUriOrNull() ?: applet.icon,
                    )
                }
            } ?: applet
        } else {
            applet
        }
    }

    override fun RenderContext.renderFields() {
        inputField {
            value(map(WebsiteApplet.src()))
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
            val store = map(WebsiteApplet.title())
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
            store = map(WebsiteApplet.aspectRatio()),
            label = "Aspect ratio",
            itemTitle = AspectRatio::title,
            itemIcon = AspectRatio::icon,
        )

        selectMultipleField(
            store = map(WebsiteApplet.allow()),
            label = "Allow",
            itemTitle = PermissionPolicy::name,
        )
        switchField(
            store = map(WebsiteApplet.allowFullscreen()),
            label = "Allow fullscreen",
        )
        selectMultipleField(
            store = map(WebsiteApplet.sandbox()),
            label = "Sandbox",
            itemTitle = ContentSecurityPolicy::name,
        )
    }
}
