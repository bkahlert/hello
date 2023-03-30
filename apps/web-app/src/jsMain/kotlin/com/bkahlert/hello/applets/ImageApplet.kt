package com.bkahlert.hello.applets

import com.bkahlert.hello.fritz2.components.heroicons.SolidHeroIcons
import com.bkahlert.hello.fritz2.inputEditor
import com.bkahlert.hello.fritz2.mapValidating
import com.bkahlert.hello.fritz2.selectEditor
import com.bkahlert.hello.fritz2.uriEditor
import com.bkahlert.kommons.randomString
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.toUri
import dev.fritz2.core.RenderContext
import dev.fritz2.core.lensOf
import dev.fritz2.core.src
import dev.fritz2.core.storeOf
import dev.fritz2.validation.ValidationMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@JsModule("./nyancat.svg")
private external val NyanCatSrc: String

@Serializable
@SerialName("image")
data class ImageApplet(
    override val id: String = randomString(),
    @SerialName("src") val src: Uri,
    @SerialName("aspect-ratio") val aspectRatio: AspectRatio = AspectRatio.video,
    override val name: String = src.path.substringAfterLast('/'),
) : Applet {

    override val icon: Uri get() = ImageApplet.icon

    override fun duplicate(): Applet = copy(id = randomString())

    override fun render(renderContext: RenderContext) {
        renderContext.window(name, aspectRatio) {
            img { src(src.toString()) }
        }
    }

    override fun renderEditor(renderContext: RenderContext, contributeMessages: (Flow<List<ValidationMessage>>) -> Unit): Flow<Applet> {
        val store = storeOf(this)
        renderContext.div("flex flex-col sm:flex-row gap-8 justify-center") {
            div("flex-grow flex flex-col gap-2") {
                label {
                    +"Name"
                    inputEditor(null, store.mapValidating(lensOf("name", { it.name }, { p, v ->
                        require(v.isNotBlank()) { "Name must not be blank" }
                        p.copy(name = v)
                    })).also { contributeMessages(it.messages) })
                }
                label {
                    +"Source"
                    uriEditor(null, store.map(lensOf("src", { it.src }, { p, v -> p.copy(src = v) })))
                }
                label {
                    +"Aspect ratio"
                    selectEditor(
                        null, store.map(
                            lensOf(
                                "aspect-ratio",
                                { it.aspectRatio },
                                { p, v -> p.copy(aspectRatio = v) })
                        )
                    )
                }
            }
        }
        return store.data
    }

    companion object : AppletType<ImageApplet> {
        override val name: String = "Image"
        override val description: String = "Displays an image"
        override val icon: Uri = SolidHeroIcons.photo
        override val default = ImageApplet(name = "Nyan Cat", src = NyanCatSrc.toUri(), aspectRatio = AspectRatio.video)
    }
}
