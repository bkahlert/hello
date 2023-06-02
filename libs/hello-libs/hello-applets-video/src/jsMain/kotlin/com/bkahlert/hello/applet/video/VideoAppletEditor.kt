package com.bkahlert.hello.applet.video

import com.bkahlert.hello.applet.AppletEditor
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

public class VideoAppletEditor(isNew: Boolean, applet: VideoApplet) : AppletEditor<VideoApplet>(isNew, applet) {

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
            value(map(VideoApplet.src()))
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
            val store = map(VideoApplet.title())
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
        inputField {
            value(map(VideoApplet.icon()))
            inputLabel {
                +"Icon"
                div("flex items-center gap-4") {
                    val changes = inputTextfield {
                        type("url")
                        placeholder("Automatic")
                    }.also(::mergeValidationMessages).changes
                }
            }
        }
    }
}
