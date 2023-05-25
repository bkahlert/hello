package com.bkahlert.hello.components.applet

import com.bkahlert.hello.components.applet.media.ImageApplet
import com.bkahlert.hello.components.applet.media.VideoApplet
import com.bkahlert.hello.components.applet.media.WebsiteApplet
import com.bkahlert.hello.components.applet.preview.FeaturePreview
import com.bkahlert.hello.components.applet.preview.FeaturePreviewApplet
import com.bkahlert.hello.components.applet.ssh.WsSshApplet
import com.bkahlert.hello.components.move
import com.bkahlert.hello.fritz2.ContentBuilder
import com.bkahlert.hello.fritz2.SyncStore
import com.bkahlert.hello.fritz2.app.props.PropStoreFactory
import com.bkahlert.hello.fritz2.app.props.PropsStore
import com.bkahlert.hello.fritz2.components.button
import com.bkahlert.hello.fritz2.components.heroicons.OutlineHeroIcons
import com.bkahlert.hello.fritz2.components.heroicons.SolidHeroIcons
import com.bkahlert.hello.fritz2.components.icon
import com.bkahlert.hello.fritz2.scrollTops
import com.bkahlert.hello.fritz2.syncState
import com.bkahlert.hello.fritz2.verticalScrollCoverageRatios
import com.bkahlert.hello.scrollSmoothlyIntoView
import com.bkahlert.hello.scrollSmoothlyTo
import com.bkahlert.kommons.dom.checkedOwnerDocument
import com.bkahlert.kommons.js.ConsoleLogger
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.toUri
import dev.fritz2.core.Handler
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Tag
import dev.fritz2.core.classes
import dev.fritz2.core.disabled
import dev.fritz2.core.lensForElement
import dev.fritz2.core.lensOf
import dev.fritz2.core.storeOf
import dev.fritz2.core.title
import dev.fritz2.core.type
import dev.fritz2.headless.components.dataCollection
import dev.fritz2.headless.foundation.utils.scrollintoview.ScrollPosition
import kotlinx.browser.document
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.builtins.ListSerializer
import org.w3c.dom.Element
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import kotlin.time.Duration.Companion.seconds

class Applets(
    val registration: AppletRegistration,
    val propsStore: PropsStore,
    val defaultValue: List<Applet>,
    id: String,
) : SyncStore<List<Applet>> by propsStore.map(id, defaultValue, ListSerializer(AppletSerializer(registration))) {
    private val logger = ConsoleLogger("hello.applets")

    private val router = AppletRouter(this)

    private fun randomId(): String {
        var id: String
        do id = Applet.randomId()
        while (current.any { it.id == id })
        return id
    }

    private val idProvider: (Applet) -> String = { "applet-${it.id}" }

    val selected = router.map(
        lensOf(
            id = "",
            getter = { it?.applet },
            setter = { p, v -> v?.let { AppletRoute.Current(v, p?.edit ?: false) } },
        )
    )
    val edited = router.map(
        lensOf(
            id = "",
            getter = {
                it?.takeIf { it.edit }?.applet?.let { applet ->
                    applet.editor(isNew = current.none { it.id == applet.id })
                }.also { editor ->
                    if (editor != null) {
                        editor.addOrUpdate handledBy {
                            addOrUpdate(it)
                            router.navTo(AppletRoute.Current(it, false))
                        }
                        editor.delete handledBy {
                            delete(it)
                            router.navTo(AppletRoute.Current(it, false))
                        }
                        editor.cancel handledBy {
                            router.navTo(AppletRoute.Current(it, false))
                        }
                        editor.data handledBy { editedApplet.tryEmit(it) }
                    } else {
                        editedApplet.tryEmit(null)
                    }
                }
            },
            setter = { p, v -> v?.let { AppletRoute.Current(v.current, true) } },
        )
    )

    // shared flow used to make sure updates aren't conflated and always get rendered
    val editedApplet: MutableSharedFlow<Applet?> = MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    val addOrUpdate: Handler<Applet> = handle { applets, applet ->
        val existing = applets.firstOrNull { it.id == applet.id }
        if (existing == null) {
            applets + applet
        } else {
            applets.map { if (it.id == applet.id) applet else it }
        }
    }

    val duplicate: Handler<Applet> = handle { applets, applet ->
        val duplicate: Applet = registration.require(applet).duplicate(randomId(), applet)
        applets.flatMap { if (it.id == applet.id) listOf(applet, duplicate) else listOf(it) }
            .also { router.navTo(AppletRoute.Current(duplicate, true)) }
            .also { logger.info("${applet.title()} duplicated") }
    }

    val reloading = storeOf<Set<Applet>>(emptySet())
    val reload: Handler<Applet> = handle { applets, applet ->
        reloading.update(reloading.current + applet)
        delay(.5.seconds)
        reloading.update(reloading.current - applet)
        applets
    }

    val replace: Handler<Applet> = handle { applets, applet ->
        applets.map { if (it.id == applet.id) applet else it }.also { selected.update(applet) }
    }

    val rankUp: Handler<Applet> = handle { applets, applet ->
        applets.move(applet, -1).also { selected.update(applet) }
    }
    val rankDown: Handler<Applet> = handle { applets, applet ->
        applets.move(applet, +1).also { selected.update(applet) }
    }

    val delete: Handler<Applet> = handle { applets, applet ->
        val scrollTo = applets.indexOfFirst { it.id == applet.id }.let { index ->
            if (index + 1 in applets.indices) applets[index + 1]
            else applets.getOrNull(index - 1)
        }
        applets.filter { it.id != applet.id }
            .also { scrollTo?.also { selected.update(applet) } }
            .also { logger.info("${applet.title()} deleted") }
    }

    private fun Applet.title() = title.takeUnless { it.isNullOrBlank() }
        ?: registration.find(this)?.title?.let { "$it: $id" }
        ?: id

    private fun Applet.icon() = icon
        ?: registration.find(this)?.icon
        ?: SolidHeroIcons.question_mark_circle

    fun render(renderContext: Tag<Element>) {
        runCatching { (renderContext.unsafeCast<Tag<HTMLElement>>()).trackScrolling() }
            .onFailure { logger.error("Failed to track scrolling", it) }

        val appletAdditionControlsId = "applet-addition-controls"

        renderContext.dataCollection("contents") {
            data(
                data = this@Applets.data,
                idProvider = { it.toString() }, // toString is used on purpose to ensure re-rendering on every applet change
            )

            dataCollectionItems("contents") {
                scrollIntoView(vertical = ScrollPosition.center)
                items.renderEach({ it.toString() }, into = this, batch = false) { applet: Applet ->

                    dataCollectionItem(
                        applet,
                        id = idProvider(applet),
                        classes = "applet",
                    ) {
                        // Conflated so that not edited applets don't re-render when an applet is edited.
                        val editing = editedApplet.map { it?.id == applet.id }.distinctUntilChanged()
                        val reloading = reloading.data.map { it.any { it.id == applet.id } }.distinctUntilChanged()

                        // Flow that—when editing—emits all updates of the applet, otherwise the applet itself once.
                        val liveApplet: Flow<Applet> = editing.flatMapLatest {
                            if (it) editedApplet.filterNotNull()
                            else flowOf(applet)
                        }

                        div("applet-controls") {
                            div("flex items-center min-w-0") {
                                div("flex items-center justify-center gap-2 overflow-hidden") {
                                    liveApplet.render(this) {
                                        icon("shrink-0 w-4 h-4", it.icon())
                                        div("truncate") { +it.title() }
                                    }
                                }
                            }

                            div("ml-8 flex items-center justify-center gap-3") {
                                controlButton(OutlineHeroIcons.square_2_stack, "Duplicate") {
                                    disabled(editing)
                                    clicks.map { applet } handledBy duplicate
                                }

                                controlButton(OutlineHeroIcons.pencil_square, "Edit") {
                                    disabled(editing)
                                    clicks.map { applet } handledBy { router.navTo(AppletRoute.Current(it, true)) }
                                }

                                controlButton(OutlineHeroIcons.arrow_path, "Reload") {
                                    className(reloading.map { if (it) "transition animate-spin" else "transition hover:rotate-90" })
                                    disabled(editing)
                                    clicks.map { applet } handledBy reload
                                }

                                div("flex items-center justify-center gap-2") {
                                    controlButton(OutlineHeroIcons.arrow_small_up, "Move up") {
                                        disabled(items.map { it.firstOrNull() == applet })
                                        clicks.mapNotNull { applet } handledBy rankUp
                                    }
                                    div("flex items-center justify-end space-x-1 opacity-60") {
                                        div {
                                            items.map { it.indexOf(applet) }.render {
                                                div("animate-in spin-in slide-in-from-top") { +"${it + 1}" }
                                            }
                                        }
                                        div("font-extralight text-xs") { +"/" }
                                        div("font-extralight") { items.map { it.size }.render { +"$it" } }
                                    }
                                    controlButton(OutlineHeroIcons.arrow_small_down, "Move down") {
                                        disabled(items.map { it.lastOrNull() == applet })
                                        clicks.mapNotNull { applet } handledBy rankDown
                                    }
                                }
                            }
                        }

                        div("applet-content flex flex-row-reverse gap-8") {
                            div("flex-grow") {
                                liveApplet.combine(reloading, ::Pair).render(this) { (applet, r) ->
                                    if (r) div("absolute inset-0 text-default dark:text-invert flex items-center justify-center") {
                                        icon("w-8 h-8 animate-spin", SolidHeroIcons.arrow_path)
                                    } else applet.render(this).apply {
                                        syncState(syncState.map { it.map(lensForElement(applet, Applet::id)) })
                                    }
                                }
                            }
                            editing.combine(edited.data) { editing, editor ->
                                editor?.takeIf { editing }
                            }.render { currentEditor ->
                                if (currentEditor != null) {
                                    with(currentEditor) {
                                        render().apply {
                                            className("absolute inset-0 z-10 bg-glass text-default dark:text-invert sm:relative sm:bg-none sm:ml-3")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        renderContext.div("app-item", id = appletAdditionControlsId) {
            div { icon("mx-auto w-12 h-12 text-default dark:text-invert opacity-60", SolidHeroIcons.squares_plus) }
            div("grid grid-cols-[repeat(auto-fit,_minmax(min(20rem,_100%),_1fr))] gap-8 m-8 items-start") {
                registration.forEach { (_, registration) ->
                    button(registration.icon, registration.title, registration.description, simple = true, inverted = true).apply {
                        clicks.map { registration.create(randomId()) } handledBy {
                            addOrUpdate(it)
                            router.navTo(AppletRoute.Current(it, true))
                        }
                    }
                }
            }
        }

        // TODO extract as "dock" or "toolbar" component
        renderContext.div("absolute z-10 top-1/2 -translate-y-1/2 right-0 pointer-coarse:hidden opacity-0 transition group pr-4 hover:pr-6") {
            className(renderContext.verticalScrollCoverageRatios.mapLatest { it < 0.75 }.mapLatest { if (it) "opacity-100" else "" })

            div(
                classes(
                    "flex flex-col items-center justify-center overflow-hidden transition",
                    "group-hover:my-4 group-hover:ml-4",
                    "rounded-full bg-glass [--glass-opacity:0.1] group-hover:[--glass-opacity:0.5]",
                    "group-hover:scale-100",
                    "scale-75 origin-right group-hover:scale-100",
                )
            ) {
                fun iconButton(icon: Uri, caption: String, customize: HtmlTag<HTMLButtonElement>.() -> Unit = {}) =
                    button("w-8 h-8 relative overflow-hidden text-default dark:text-invert enabled:hover:bg group/button transition") {
                        type("button")
                        icon("absolute inset-1 scale-75 group-hover/button:scale-100", icon)
                        title(caption)
                        customize()
                    }

                iconButton(OutlineHeroIcons.arrow_long_up, "Scroll to top") {
                    clicks.map { renderContext.domNode } handledBy { it.scrollSmoothlyTo(top = 0) }
                }
                data.renderEach(
                    idProvider = { it.toString() }, // toString is used on purpose to ensure re-rendering on every applet change
                ) { applet: Applet ->
                    iconButton(
                        applet.icon(),
                        "Scroll to ${applet.title()}"
                    ) { clicks.map { applet } handledBy selected.update }
                }
                iconButton(
                    OutlineHeroIcons.squares_plus,
                    "Add applet",
                ) {
                    clicks.mapNotNull { renderContext.domNode.checkedOwnerDocument.getElementById("applet-addition-controls") } handledBy {
                        it.scrollSmoothlyIntoView()
                    }
                }
                iconButton(OutlineHeroIcons.arrow_long_down, "Scroll to bottom") {
                    clicks.map { renderContext.domNode } handledBy { it.scrollSmoothlyTo(top = it.scrollHeight) }
                }
            }
        }
    }

    override fun toString(): String = "Applets($current)"

    private var blockTracking = true
    private fun Tag<HTMLElement>.trackScrolling() {
        scrollTops
            .mapNotNull { it.takeUnless { blockTracking } }
            .map { current.find { applet -> applet.isInView(idProvider) } }
            .distinctUntilChanged()
            .debounce(1.seconds) handledBy selected.update
    }

    init {
        selected.data
            .filterNotNull()
            .distinctUntilChanged() handledBy { applet ->
            delay(0.1.seconds)
            if (!applet.isInView(idProvider)) {
                applet.findElement(idProvider)?.scrollSmoothlyIntoView()
            }
            blockTracking = false
        }
    }

    companion object : PropStoreFactory<List<Applet>> {
        private val Element.scrollMiddle get() = scrollTop.toInt() + clientHeight / 2
        private fun Applet.findElement(idProvider: (Applet) -> String): HTMLElement? =
            document.getElementById(idProvider(this))?.unsafeCast<HTMLElement>()

        private fun Applet.isInView(idProvider: (Applet) -> String): Boolean {
            val appletElement = findElement(idProvider) ?: return false
            val offsetThreshold = appletElement.closest(".app-scroll-container")?.scrollMiddle ?: return false
            return offsetThreshold >= appletElement.offsetTop && offsetThreshold <= appletElement.offsetTop + appletElement.clientHeight
        }

        override val DEFAULT_KEY: String = "applets"
        override val DEFAULT_VALUE: List<Applet> = buildList {
            add(
                ImageApplet(
                    id = "nyan-cat",
                    title = "Nyan Cat",
                    src = NyanCatSrc.toUri(),
                    aspectRatio = AspectRatio.video
                )
            )
            add(
                VideoApplet(
                    id = "rick-astley",
                    title = "Rick Astley",
                    src = Uri("https://www.youtube.com/embed/dQw4w9WgXcQ"),
                )
            )
            add(
                WebsiteApplet(
                    id = "impossible-color",
                    title = "Impossible color",
                    src = Uri("https://en.wikipedia.org/wiki/Impossible_color"),
                    aspectRatio = AspectRatio.stretch,
                )
            )
            FeaturePreview.values().mapTo(this) {
                FeaturePreviewApplet(
                    id = "feature-preview-${it.name}",
                    feature = it,
                )
            }
        }

        override fun invoke(propsStore: PropsStore, defaultValue: List<Applet>, id: String): Applets =
            Applets(AppletRegistration().apply {
                register<FeaturePreviewApplet>(
                    "feature-preview",
                    title = "Feature Preview",
                    description = "Demo of a future feature",
                    icon = SolidHeroIcons.star
                )
                register<ImageApplet>(
                    "image",
                    title = "Image",
                    description = "Displays an image",
                    icon = SolidHeroIcons.photo
                )
                register<VideoApplet>(
                    "video",
                    title = "Video",
                    description = "Embeds a video",
                    icon = SolidHeroIcons.video_camera
                )
                register<WebsiteApplet>(
                    "website", "embed",
                    title = "Website",
                    description = "Embeds an external website",
                    icon = SolidHeroIcons.window
                )
                register<WsSshApplet>(
                    "ws-ssh",
                    title = "SSH",
                    description = """Connect to a SSH server via a <a href="https://github.com/bkahlert/ws-ssh">WS-SSH proxy</a>.""",
                    icon = SolidHeroIcons.command_line,
                )
            }, propsStore, defaultValue, id)
    }
}

@JsModule("./nyancat.svg")
private external val NyanCatSrc: String

fun Tag<Element>.panel(
    aspectRatio: AspectRatio? = null,
    content: ContentBuilder<Element>? = null,
): HtmlTag<HTMLDivElement> = div("panel") {
    val ar = aspectRatio ?: AspectRatio.none
    div(classes("panel-content", ar.classes)) {
        ar.wrap(this) { content?.invoke(this) }
    }
}

private fun RenderContext.controlButton(
    icon: Uri,
    title: String,
    content: ContentBuilder<HTMLButtonElement>? = null,
): HtmlTag<HTMLButtonElement> = button("disabled:pointer-events-none disabled:opacity-60") {
    type("button")
    icon("w-4 h-4", icon)
    title(title)
    content?.invoke(this)
}
