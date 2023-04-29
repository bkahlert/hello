package com.bkahlert.hello.components.applet.media

import com.bkahlert.hello.components.applet.Applet
import com.bkahlert.hello.components.applet.AppletEditor
import com.bkahlert.hello.components.applet.AspectRatio
import com.bkahlert.hello.components.applet.panel
import com.bkahlert.hello.fritz2.UriLens
import com.bkahlert.hello.fritz2.lens
import com.bkahlert.hello.fritz2.mergeValidationMessages
import com.bkahlert.hello.fritz2.orEmpty
import com.bkahlert.hello.fritz2.selectField
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.toUriOrNull
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.Lens
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Tag
import dev.fritz2.core.placeholder
import dev.fritz2.core.required
import dev.fritz2.core.src
import dev.fritz2.core.type
import dev.fritz2.core.values
import dev.fritz2.headless.components.inputField
import dev.fritz2.headless.foundation.setInitialFocus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement

@Serializable
data class ImageApplet(
    override val id: String,
    @SerialName("src") val src: Uri? = null,
    @SerialName("aspect-ratio") val aspectRatio: AspectRatio? = AspectRatio.video,
    override val title: String? = src?.path?.substringAfterLast('/'),
) : Applet {

    override fun editor(isNew: Boolean): AppletEditor<*> = ImageAppletEditor(isNew, this)

    override fun render(renderContext: Tag<Element>): HtmlTag<HTMLDivElement> = renderContext.panel(aspectRatio) {
        val missing = listOf(::src).filter { it.get().isNullOrBlank() }
        if (missing.isNotEmpty()) {
            renderConfigurationMissing(missing)
        } else {
            img { src(src.toString()) }
        }
    }

    companion object {
        public fun title(): Lens<ImageApplet, String> =
            ImageApplet::title.lens({ it.title }) { p, v -> p.copy(title = v) }.orEmpty()

        public fun src(): Lens<ImageApplet, String> =
            ImageApplet::src.lens({ it.src }, { p, v -> p.copy(src = v?.toUriOrNull()) }) + UriLens

        public fun aspectRatio(): Lens<ImageApplet, AspectRatio?> =
            ImageApplet::aspectRatio.lens({ it.aspectRatio }, { p, v -> p.copy(aspectRatio = v) })
    }
}


class ImageAppletEditor(isNew: Boolean, applet: ImageApplet) : AppletEditor<ImageApplet>(isNew, applet) {
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
