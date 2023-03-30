package com.bkahlert.hello.applets

import com.bkahlert.hello.fritz2.ContentBuilder
import com.bkahlert.hello.fritz2.components.heroicons.SolidHeroIcons
import com.bkahlert.hello.fritz2.components.icon
import com.bkahlert.hello.fritz2.selectEditor
import com.bkahlert.kommons.randomString
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.pathSegments
import com.bkahlert.kommons.uri.toUri
import dev.fritz2.core.RenderContext
import dev.fritz2.core.classes
import dev.fritz2.core.colSpan
import dev.fritz2.core.d
import dev.fritz2.core.fill
import dev.fritz2.core.href
import dev.fritz2.core.lensOf
import dev.fritz2.core.scope
import dev.fritz2.core.storeOf
import dev.fritz2.core.type
import dev.fritz2.core.viewBox
import dev.fritz2.headless.foundation.Aria
import dev.fritz2.validation.ValidationMessage
import io.ktor.http.decodeURLPart
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("feature-preview")
data class FeaturePreviewApplet(
    override val id: String = randomString(),
    @SerialName("feature") val feature: FeaturePreview,
    @SerialName("aspect-ratio") val aspectRatio: AspectRatio = AspectRatio.video,
) : Applet {
    override val name: String get() = feature.initialName

    override val icon: Uri get() = feature.icon

    override fun duplicate(): Applet = copy(id = randomString())

    override fun render(renderContext: RenderContext) {
        renderContext.window(name, aspectRatio) {
            feature.render.invoke(this)
        }
    }

    override fun renderEditor(renderContext: RenderContext, contributeMessages: (Flow<List<ValidationMessage>>) -> Unit): Flow<Applet> {
        val store = storeOf(this)
        renderContext.div("flex flex-col sm:flex-row gap-8 justify-center") {
            div("flex-grow flex flex-col gap-2") {
                label {
                    +"Feature"
                    selectEditor(
                        null, store.map(
                            lensOf(
                                "feature",
                                { it.feature },
                                { p, v -> p.copy(feature = v) })
                        )
                    )
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

    companion object : AppletType<FeaturePreviewApplet> {
        override val name: String = "Feature Preview"
        override val description: String = "Demo of a future feature"
        override val icon: Uri = SolidHeroIcons.star
        override val default = FeaturePreviewApplet(feature = FeaturePreview.webshell)
    }
}

enum class FeaturePreview(
    val initialName: String,
    val icon: Uri,
    val render: ContentBuilder,
) {
    dropbox("Dropbox", SolidHeroIcons.folder, {
        div("dropbox flex flex-col justify-between bg-white bg-hero-texture-blue") {
            ul("flex [&>:not(:last-child)]:after:content-['_/'] space-x-2 m-2 p-2 text-xl text-slate-600 [&>:last-child]:text-slate-800") {
                li("cursor-pointer") { +"Dropbox" }
                li("cursor-pointer") { +"Education" }
                li { +"Monographs" }
            }
            div("m-4 border border-dashed border-gray-500/50 text-[#0161fe] p-2 min-h-[5rem] relative flex items-center cursor-pointer") {
                div("mx-auto flex") {
                    svg("w-5 h-5 mr-1") {
                        viewBox("0 0 24 24")
                        fill("none")
                        attr("role", Aria.Role.presentation)
                        path {
                            d("m10.463 7-.377-.756A2.238 2.238 0 0 0 8.072 5H3.5v11.75A2.25 2.25 0 0 0 5.75 19H9.5v-1.5H5.75a.75.75 0 0 1-.75-.75V6.5h3.073a.745.745 0 0 1 .67.415L9.536 8.5H18.5v8.25a.75.75 0 0 1-.75.75H14V19h3.75A2.249 2.249 0 0 0 20 16.75V7h-9.537Z")
                            fill("currentColor")
                        }
                        path {
                            d("m14.61 15.21-2.86-2.75-2.86 2.75 1.04 1.08L11 15.263V19h1.5v-3.738l1.07 1.029 1.04-1.081Z")
                            fill("currentColor")
                        }
                    }
                    div {
                        strong { +"Drop files here to upload," }
                        +" or use the 'Upload' button"
                    }
                }
            }
            table(
                classes(
                    "flex-1 flex flex-col overflow-hidden",
                    listOf("th", "td").flatMap {
                        listOf(
                            "[&_$it]:py-2",
                        )
                    }.joinToString(" ")
                )
            ) {
                thead {
                    tr("border-b border-b-gray-500/25 flex") {
                        th("w-20 text-center") {
                            scope("col")
                            input {
                                type("checkbox")
                                attr(Aria.label, "Select all")
                            }
                        }
                        th("flex-grow") {
                            scope("col")
                            colSpan(2)
                            button("flex items-center space-x-2") {
                                type("button")
                                span("font-semibold") { +"Name" }
                                icon("w-4 h-4", SolidHeroIcons.arrow_up)
                            }
                        }
                    }
                }
                tbody("space-y-2 overflow-y-scroll") {
                    inlineStyle("vertical-align: top;")
                    listOf(
                        "https://www.dropbox.com/s/ba2n7nmxh656o6v/Bachelorarbeit_Kahlert.pdf?dl=0",
                        "https://www.dropbox.com/s/8ibx90dxjzh91cj/Die%20Entwicklung%20der%20Sprache-Seminararbeit-Kahlert.pdf?dl=0",
                        "https://www.dropbox.com/s/3qa26qlnt8r4enu/Dissertation_Kahlert.pdf?dl=0",
                        "https://www.dropbox.com/s/10uj68sxgbd5gf5/Intelligenz-Seminararbeit-Kahlert.pdf?dl=0",
                        "https://www.dropbox.com/s/t2zd31zig89c18j/IT-Sicherheit_buffer-overflow.pdf?dl=0",
                        "https://www.dropbox.com/s/l4qpn2jrlc680a6/Masterarbeit_Kahlert.pdf?dl=0",
                        "https://www.dropbox.com/s/xj03q5o0m5bfd0i/Seminararbeit_Kahlert.pdf?dl=0",
                        "https://www.dropbox.com/s/qtwvy7fgekfikbf/Seminararbeit-Diagnose-2.pdf?dl=0",
                    ).forEachIndexed { index, link ->
                        tr(classes("flex", "border-t border-t-gray-500/25".takeUnless { index == 0 })) {
                            td("w-20 text-center") {
                                input {
                                    type("checkbox")
                                    attr(Aria.label, "Select")
                                }
                            }
                            td("flex-grow") {
                                a("flex items-center space-x-2 hover:text-[#0161fe]") {
                                    href(link)
                                    svg("w-8 h-8") {
                                        viewBox("0 0 40 40")
                                        fill("none")
                                        attr("role", Aria.Role.img)
                                        path {
                                            d("M28.757 8.5H11.243c-1.048 0-1.86.207-2.411.615C8.28 9.524 8 10.125 8 10.9v19.2c0 .776.28 1.376.832 1.784.552.409 1.363.616 2.411.616h17.514c1.048 0 1.86-.207 2.411-.616.552-.408.832-1.008.832-1.784V10.9c0-.776-.28-1.376-.832-1.785-.552-.408-1.363-.615-2.411-.615Z")
                                            fill("#BFBFBF")
                                        }
                                        path {
                                            d("M28.757 7H11.243c-1.048 0-1.86.207-2.411.615C8.28 8.024 8 8.625 8 9.4v19.2c0 .776.28 1.376.832 1.784.552.409 1.363.616 2.411.616h17.514c1.048 0 1.86-.207 2.411-.616.552-.408.832-1.008.832-1.784V9.4c0-.776-.28-1.376-.832-1.785C30.616 7.207 29.805 7 28.757 7Z")
                                            fill("#F7F5F2")
                                        }
                                        path {
                                            attr("fill-rule", "evenodd")
                                            attr("clip-rule", "evenodd")
                                            d("M17.883 16v6H20l.19-.008c1.893-.094 2.813-1.076 2.813-2.992 0-1.95-.954-2.933-2.916-2.997L17.883 16Zm1.15 1v4h.741-.274.378c.868 0 1.27-.202 1.468-.391.196-.19.407-.776.407-1.609s-.21-1.42-.407-1.608c-.197-.19-.6-.392-1.468-.392h-.845Z")
                                            fill("#F25123")
                                        }
                                        path {
                                            d("M28.108 17h-2.966v2h2.608v1h-2.608v2h-1.15v-6h4.116v1Z")
                                            fill("#F25123")
                                        }
                                        path {
                                            attr("fill-rule", "evenodd")
                                            attr("clip-rule", "evenodd")
                                            d("M12 16v6h1.15v-2l1.893-.006c.527-.03 1.02-.176 1.39-.516.42-.387.567-.919.567-1.478 0-.56-.148-1.09-.568-1.477-.37-.34-.862-.487-1.388-.517L12 16Zm1.15 1v2h1.677c.41 0 .593-.169.679-.248l.004-.004c.085-.078.294-.365.294-.748s-.21-.67-.294-.747l-.004-.004c-.087-.08-.27-.249-.68-.249H13.15Z")
                                            fill("#F25123")
                                        }
                                    }
                                    span { +link.toUri().pathSegments.last().decodeURLPart() }
                                }
                            }
                            td("w-16 text-center") {
                                svg("w-6 h-6 cursor-pointer") {
                                    viewBox("0 0 24 24")
                                    fill("none")
                                    attr("role", Aria.Role.presentation)
                                    path {
                                        d("M12 10a1.857 1.857 0 0 0-2 2 1.857 1.857 0 0 0 2 2 1.857 1.857 0 0 0 2-2 1.857 1.857 0 0 0-2-2Zm6 0a1.857 1.857 0 0 0-2 2 1.858 1.858 0 0 0 2 2 1.857 1.857 0 0 0 2-2 1.857 1.857 0 0 0-2-2ZM6 10a1.857 1.857 0 0 0-2 2 1.857 1.857 0 0 0 2 2 1.856 1.856 0 0 0 2-2 1.857 1.857 0 0 0-2-2Z")
                                        fill("currentColor")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }),
    webshell("WebShell", SolidHeroIcons.command_line, {
        val user = "tatu"
        val host = "ylonen"
        div("terminal flex flex-col justify-between") {
            div("terminal__viewport flex-grow bg-black text-white font-mono p-2") {
                div("terminal__line") { +"Last failed login: Thu Apr 27 19:40:45 UTC 2023 from 93.184.216.34 on ssh:notty" }
                div("terminal__line") { +"There was 1 failed login attempt since the last successful login." }
                div("terminal__line") { +"Last login: Thu Apr 27 19:40:42 2023 from 34.216.184.93" }
                div("terminal__line") {
                    +"[$user@$host ~]# "
                    span("animate-pulse duration-700") {
                        inlineStyle("animation-direction: alternate; animation-timing-function: steps(1);")
                        +"▏"
                    }
                }
            }
            div("terminal__footer flex bg-slate-800 font-bold text-sm text-emerald-500 p-1") {
                div("flex-1 terminal__connection") { +"ssh://$user@$host.example.com" }
                div("flex-1 terminal__status") { +"SSH CONNECTION ESTABLISHED" }
            }
        }
    }),
}
