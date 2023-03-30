package com.bkahlert.hello.applets

import com.bkahlert.hello.fritz2.ContentBuilder
import com.bkahlert.hello.fritz2.EditorAction
import com.bkahlert.hello.fritz2.EditorAction.Companion.DefaultEditorActions
import com.bkahlert.hello.fritz2.SyncStore
import com.bkahlert.hello.fritz2.app.props.PropsStore
import com.bkahlert.hello.fritz2.app.props.prop
import com.bkahlert.hello.fritz2.components.heroicons.SolidHeroIcons
import com.bkahlert.hello.fritz2.components.icon
import com.bkahlert.hello.fritz2.shortcut
import com.bkahlert.hello.fritz2.shortcuts
import com.bkahlert.kommons.uri.Uri
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Store
import dev.fritz2.core.classes
import dev.fritz2.core.disabled
import dev.fritz2.core.lensOf
import dev.fritz2.core.storeOf
import dev.fritz2.core.transition
import dev.fritz2.core.type
import dev.fritz2.headless.foundation.OpenClose
import dev.fritz2.validation.ValidationMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonClassDiscriminator
import kotlinx.serialization.json.JsonNames
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Serializable
@JsonClassDiscriminator("type")
sealed interface Applet {
    @SerialName("id")
    val id: String

    @SerialName("name")
    val name: String

    val icon: Uri

    fun duplicate(): Applet

    fun render(renderContext: RenderContext)

    fun renderEditor(renderContext: RenderContext, contributeMessages: (Flow<List<ValidationMessage>>) -> Unit): Flow<Applet>
}

interface AppletType<out T : Applet> {
    val name: String
    val description: String
    val icon: Uri
    val default: T
}

object CreatableAppletTypes : List<AppletType<Applet>> by listOf(
    FeaturePreviewApplet,
    ImageApplet,
    EmbedApplet,
//    WebSsh2Applet,
)

class Applets(val store: SyncStore<List<Applet>>) : SyncStore<List<Applet>> by store {

    val insertBefore = handle<Pair<String, Applet>> { applets, (id, applet) ->
        applets.flatMap { if (it.id == id) listOf(applet, it) else listOf(it) }
    }

    val insertAfter = handle<Pair<String, Applet>> { applets, (id, applet) ->
        applets.flatMap { if (it.id == id) listOf(it, applet) else listOf(it) }
    }

    val add = handle<Applet> { applets, applet ->
        applets + applet
    }

    val replace = handle<Applet> { applets, applet ->
        applets.map { if (it.id == applet.id) applet else it }
    }

    val rankUp = handle<Applet> { applets, applet ->
        applets.move(applet, -1)
    }
    val rankDown = handle<Applet> { applets, applet ->
        applets.move(applet, +1)
    }

    val delete = handle<Applet> { applets, applet ->
        applets - applet
    }

    fun render(renderContext: RenderContext) {
        renderContext.div("applets bg-swatch-blue-yellow bg-local") {
//                scrollIntoView()

            data.renderEach(
                idProvider = { it.toString() }, // toString is used on purpose to ensure re-rendering on every applet change
                into = this,
            ) { applet ->
                div("applet flex flex-col sm:flex-row") {

                    val editor = object : OpenClose(), Store<Applet?> by storeOf(null) {
                        val save = handle {
                            it?.also { replace(it) }
                            null
                        }

                        val cancel = handle {
                            null
                        }

                        val delete = handle {
                            it?.also { delete(it) }
                            null
                        }

                        val actions = DefaultEditorActions.associateWith {
                            when (it) {
                                is EditorAction.Save -> save
                                is EditorAction.Cancel -> cancel
                                is EditorAction.Delete -> delete
                            }
                        }

                        init {
                            openState(map(lensOf("", { it != null }, { p, v -> if (v) p ?: applet else null })))
                            shortcuts.mapNotNull { (event, shortcut) ->
                                actions.firstNotNullOfOrNull { (action, handler) -> (event to handler).takeIf { action.shortcut == shortcut } }
                            } handledBy { (event, handler) ->
                                event.stopImmediatePropagation()
                                event.preventDefault()
                                handler()
                            }
//                            closeOnBlur()
                        }
                    }

                    editor.opened.render {
                        if (it) div("flex flex-col justify-between m-8 sm:m-0 sm:mr-10") {

                            transition(
                                editor.opened,
                                "transition ease-out duration-400",
                                "opacity-0 -translate-y-full sm:translate-y-0 sm:-translate-x-full",
                                "opacity-100 translate-y-0 sm:translate-x-0",
                                "transition ease-in duration-400",
                                "opacity-100 translate-y-0 sm:translate-x-0",
                                "opacity-0 -translate-y-full sm:translate-y-0 sm:-translate-x-full",
                            )

                            div("flex flex-col space-y-2 items-center text flex-col-reverse") {
                                h2("text-sm text-gray-800/50 font-semibold w-full text-center border-t pt-1 border-current border-dashed select-none") { +"Insert above" }
                                insertOptions(applet) { insertBefore(applet.id to it); editor.close() }
                            }

                            div("flex flex-col space-y-5 text overflow-y-auto") {
                                h2 { +"Edit" }
                                var allMessages: Flow<List<ValidationMessage>> = flowOf(emptyList())
                                div {
                                    applet.renderEditor(this) {
                                        allMessages =
                                            allMessages.combine(flow {
                                                emit(emptyList()) // add initial empty message list to make sure the flow always emits
                                                emitAll(it)
                                            }) { old, new -> old + new }
                                    } handledBy editor.update
                                }

                                div("flex flex-col items-center sm:flex-row sm:justify-end gap-2 mt-6") {
                                    allMessages.render { messages ->
                                        ul("text-red-500") {
                                            messages.forEach { message -> li { +message.toString() } }
                                        }
                                    }
                                }
                                div("flex flex-col items-center sm:flex-row sm:justify-end gap-2 mt-6") {
                                    editor.actions.forEach { (action, handler) ->
                                        button(
                                            classes(
                                                "inline-flex items-center justify-center w-full sm:w-32 px-4 py-2",
                                                "rounded box-shadow box-glass disabled:opacity-75 text-sm font-semibold",
                                                "transition [&:not(:disabled):hover]:-translate-y-0.5 [&:not(:disabled):hover]:shadow-xl [&:not(:disabled):hover]:dark:shadow-2xl",
                                            )
                                        ) {
                                            type("button")
                                            +action.name
                                            shortcut(action.shortcut)
                                            disabled(action.disabled(allMessages))
                                            clicks.map { } handledBy handler
                                        }
                                    }
                                }
                            }

                            div("flex flex-col space-y-2 items-center text hidden sm:flex") {
                                h2("text-sm text-gray-800/50 font-semibold w-full text-center border-b pb-1 border-current border-dashed select-none") { +"Insert below" }
                                insertOptions(applet) { insertAfter(applet.id to it); editor.close() }
                            }
                        }
                    }

                    div("flex-grow animate-in zoom-in") {
                        editor.data.map { it ?: applet }.render { it.render(this) }
                    }
                    div("applet__handle") {
                        className(editor.opened.map { if (it) "hidden" else "" })
                        button(
                            classes(
                                "flex items-center justify-center gap-x-2 p-1",
                                "disabled:pointer-events-none transition opacity-90 disabled:opacity-0 hover:box-glass"
                            )
                        ) {
                            type("button")
                            disabled(editor.opened)
                            icon("shrink-0 w-4 h-4", applet.icon)
                            div("font-bold") { +applet.name }
                            clicks.map { } handledBy editor.open
                        }
                    }

                    editor.opened.render { opened ->
                        if (opened) div("flex flex-col items-center justify-center m-8 sm:m-0 sm:ml-6 space-y-5 text") {

                            transition(
                                editor.opened,
                                "transition ease-out duration-400",
                                "opacity-0 translate-y-full sm:translate-y-0 sm:translate-x-full",
                                "opacity-100 translate-y-0 sm:translate-x-0",
                                "transition ease-in duration-400",
                                "opacity-100 translate-y-0 sm:translate-x-0",
                                "opacity-0 translate-y-full sm:translate-y-0 sm:translate-x-full",
                            )

                            button("flex-1 group disabled:pointer-events-none disabled:opacity-50") {
                                type("button")
                                disabled(data.map { it.firstOrNull() == applet })
                                div("flex items-center justify-center p-2 gap-x-2 group-hover:animate-bounce") {
                                    icon("shrink-0 w-4 h-4", SolidHeroIcons.arrow_up)
                                    div { +"Up" }
                                }
                                clicks.mapNotNull { applet } handledBy rankUp
                            }
                            div("flex items-center justify-end space-x-1 text-md") {
                                div("opacity-75 text-sm") { +"#" }
                                div {
                                    data.map { it.indexOf(applet) + 1 }.render {
                                        div("animate-in spin-in slide-in-from-top") { +"$it" }
                                    }
                                }
                                div("opacity-75 text-sm") { +"/" }
                                div("opacity-75") {
                                    data.map { it.size }.render {
                                        +"$it"
                                    }
                                }
                            }
                            button("flex-1 group disabled:pointer-events-none disabled:opacity-50") {
                                type("button")
                                disabled(data.map { it.lastOrNull() == applet })
                                div("flex items-center justify-center p-2 gap-x-2 group-hover:animate-bounce direction-reverse") {
                                    icon("shrink-0 w-4 h-4", SolidHeroIcons.arrow_down)
                                    div { +"Down" }
                                }
                                clicks.mapNotNull { applet } handledBy rankDown
                            }
                        }
                    }
                }
            }

            data.render {
                if (it.isEmpty()) {
                    div("absolute inset-0 flex flex-col justify-evenly") {
                        div("flex flex-col space-y-2 items-center text") {
                            h2("text-md text-gray-800/75 font-semibold w-full text-center select-none") { +"Insert" }
                            insertOptions { add(it) }
                        }

                        div("relative flex py-5 items-center text-md text-gray-800/50 font-semibold select-none") {
                            div("flex-grow border-b border-current") { }
                            span("flex-shrink mx-4") { +"OR" }
                            div("flex-grow border-b border-current") { }
                        }

                        div("flex flex-col space-y-2 items-center text") {
                            h2("text-md text-gray-800/75 font-semibold w-full text-center select-none") { +"Reset" }
                            div("flex flex-col items-start justify-center sm:flex-row sm:space-x-5") {
                                button("flex-1 flex flex-wrap items-center justify-center p-2 gap-x-2 hover:box-glass") {
                                    type("button")
                                    icon("shrink-0 w-4 h-4 sm:w-full", SolidHeroIcons.rectangle_group)
                                    div { +"Defaults" }
                                    clicks handledBy { update(DefaultApplets) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun RenderContext.insertOptions(applet: Applet? = null, handler: (Applet) -> Unit) {
        div("flex flex-col items-start justify-center sm:flex-row sm:space-x-5") {
            if (applet != null) {
                button("flex-1 flex flex-wrap items-center justify-center p-2 gap-x-2 hover:box-glass") {
                    type("button")
                    icon("shrink-0 w-4 h-4 sm:w-full", SolidHeroIcons.document_duplicate)
                    div { +"Duplicate" }
                    clicks.map { applet.duplicate() } handledBy handler
                }
            }
            CreatableAppletTypes.forEach { type ->
                button("flex-1 flex flex-wrap items-center justify-center p-2 gap-x-2 hover:box-glass") {
                    type("button")
                    icon("shrink-0 w-4 h-4 sm:w-full", type.icon)
                    div { +type.name }
                    clicks.map { type.default } handledBy handler
                }
            }
        }
    }

    override fun toString(): String = "Applets($current)"

    companion object {

        fun PropsStore.applets(
            vararg initialApplets: Applet = DefaultApplets.toTypedArray(),
        ): Applets = Applets(
            prop(
                id = "applets",
                default = { initialApplets.asList() },
                jsonFormat = Json {
                    ignoreUnknownKeys = true
                    serializersModule = SerializersModule {
                        polymorphic(Applet::class) {
                            subclass(EmbedApplet::class)
                            subclass(ImageApplet::class)
                            defaultDeserializer { UnknownApplet.serializer() }
                        }
                    }
                },
            )
        )

        val DefaultApplets = buildList {
            add(ImageApplet.default)
            add(EmbedApplet.default)
            FeaturePreview.values().mapTo(this) { FeaturePreviewApplet(feature = it) }
        }
    }
}

fun RenderContext.window(
    name: String,
    aspectRatio: AspectRatio = AspectRatio.none,
    showTitle: Boolean = false,
    content: ContentBuilder? = null,
) {
    div("window") {
        if (showTitle) div("window-title") { span { +name } }
        div("window-panel") {
            div(classes("window-content", aspectRatio.classes)) {
                aspectRatio.wrap(this) {
                    content?.invoke(this)
                }
            }
        }
    }
}

enum class AspectRatio(
    val classes: String? = null,
    val wrap: RenderContext.(ContentBuilder) -> Unit = { it() },
) {
    @SerialName("video")
    @JsonNames("16:9")
    video(null, { div("aspect-w-16 aspect-h-9") { it() } }),

    @SerialName("fill")
    @JsonNames("full")
    fill("absolute inset-0 grid"),

    @SerialName("none")
    none,
}
