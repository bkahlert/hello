package com.bkahlert.hello.components.applet.media

import com.bkahlert.hello.components.applet.Applet
import com.bkahlert.hello.components.applet.AppletEditor
import com.bkahlert.hello.components.applet.AspectRatio
import com.bkahlert.hello.components.applet.panel
import com.bkahlert.hello.fritz2.UriLens
import com.bkahlert.hello.fritz2.components.metadata.Metadata
import com.bkahlert.hello.fritz2.components.metadata.fetchMetadata
import com.bkahlert.hello.fritz2.lens
import com.bkahlert.hello.fritz2.mergeValidationMessages
import com.bkahlert.hello.fritz2.orEmpty
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.host
import com.bkahlert.kommons.uri.toUriOrNull
import dev.fritz2.core.EmittingHandler
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.Lens
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Tag
import dev.fritz2.core.allowFullscreen
import dev.fritz2.core.placeholder
import dev.fritz2.core.required
import dev.fritz2.core.src
import dev.fritz2.core.type
import dev.fritz2.core.values
import dev.fritz2.headless.components.inputField
import dev.fritz2.headless.foundation.setInitialFocus
import kotlinx.coroutines.flow.debounce
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import kotlin.time.Duration.Companion.seconds

@Serializable
data class VideoApplet(
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

    companion object {
        public fun title(): Lens<VideoApplet, String> =
            VideoApplet::title.lens({ it.title }) { p, v -> p.copy(title = v) }.orEmpty()

        public fun src(): Lens<VideoApplet, String> =
            VideoApplet::src.lens({ it.src }, { p, v -> p.copy(src = v?.toUriOrNull()) }) + UriLens

        public fun icon(): Lens<VideoApplet, String> =
            VideoApplet::icon.lens({ it.icon }, { p, v -> p.copy(icon = v?.toUriOrNull()) }) + UriLens
    }
}


class VideoAppletEditor(isNew: Boolean, applet: VideoApplet) : AppletEditor<VideoApplet>(isNew, applet) {

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
