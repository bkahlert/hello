package com.bkahlert.hello.widget

import com.bkahlert.hello.button.button
import com.bkahlert.hello.editor.move
import com.bkahlert.hello.fritz2.ContentBuilder
import com.bkahlert.hello.fritz2.SyncStore
import com.bkahlert.hello.fritz2.scrollTo
import com.bkahlert.hello.fritz2.scrollTops
import com.bkahlert.hello.fritz2.syncState
import com.bkahlert.hello.fritz2.verticalScrollCoverageRatios
import com.bkahlert.hello.icon.heroicons.OutlineHeroIcons
import com.bkahlert.hello.icon.heroicons.SolidHeroIcons
import com.bkahlert.hello.icon.icon
import com.bkahlert.kommons.dom.checkedOwnerDocument
import com.bkahlert.kommons.js.ConsoleLogger
import com.bkahlert.kommons.uri.Uri
import dev.fritz2.core.Handler
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Store
import dev.fritz2.core.Tag
import dev.fritz2.core.classes
import dev.fritz2.core.disabled
import dev.fritz2.core.href
import dev.fritz2.core.lensForElement
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
import org.w3c.dom.Element
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import kotlin.time.Duration.Companion.seconds

public class Widgets(
    public val store: SyncStore<List<Widget>>,
    public val registration: WidgetRegistration,
    public val selectionState: Store<Widget?> = storeOf(null),
    public val editState: Store<Boolean> = storeOf(false),
) : SyncStore<List<Widget>> by store {
    private val logger = ConsoleLogger("hello.widgets")

    private fun randomId(): String {
        var id: String
        do id = Widget.randomId()
        while (current.any { it.id == id })
        return id
    }

    private val idProvider: (Widget) -> String = { "widget-${it.id}" }

    private val edited: Flow<WidgetEditor<*>?> = selectionState.data.combine(editState.data) { s, e ->
        s.takeIf { e }
    }.map { widget ->
        widget?.editor(isNew = store.current.none { it.id == widget.id })?.also { editor: WidgetEditor<*>? ->
            if (editor != null) {
                editor.addOrUpdate handledBy {
                    addOrUpdate(it)
                    editState.update(false)
                    editedWidget.tryEmit(null)
                }
                editor.delete handledBy {
                    delete(it)
                    editState.update(false)
                    editedWidget.tryEmit(null)
                }
                editor.cancel handledBy {
                    editState.update(false)
                    editedWidget.tryEmit(null)
                }
                editor.data handledBy { editedWidget.tryEmit(it) }
            } else {
                editedWidget.tryEmit(null)
            }
        }
    }

    // shared flow used to make sure updates aren't conflated and always get rendered
    private val editedWidget: MutableSharedFlow<Widget?> = MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    init {
        editedWidget.tryEmit(selectionState.current.takeIf { editState.current })
    }

    public val addOrUpdate: Handler<Widget> = handle { widgets, widget ->
        val existing = widgets.firstOrNull { it.id == widget.id }
        if (existing == null) {
            widgets + widget
        } else {
            widgets.map { if (it.id == widget.id) widget else it }
        }
    }

    public val duplicate: Handler<Widget> = handle { widgets, widget ->
        val duplicate: Widget = registration.require(widget).duplicate(randomId(), widget)
        widgets.flatMap { if (it.id == widget.id) listOf(widget, duplicate) else listOf(it) }
            .also { selectionState.update(duplicate) }
            .also { editState.update(true) }
            .also { logger.info("${widget.title()} duplicated") }
    }

    public val reloading: Store<Set<Widget>> = storeOf<Set<Widget>>(emptySet())
    public val reload: Handler<Widget> = handle { widgets, widget ->
        reloading.update(reloading.current + widget)
        delay(.5.seconds)
        reloading.update(reloading.current - widget)
        widgets
    }

    public val replace: Handler<Widget> = handle { widgets, widget ->
        widgets.map { if (it.id == widget.id) widget else it }.also { selectionState.update(widget) }
    }

    public val rankUp: Handler<Widget> = handle { widgets, widget ->
        widgets.move(widget, -1).also { selectionState.update(widget) }
    }
    public val rankDown: Handler<Widget> = handle { widgets, widget ->
        widgets.move(widget, +1).also { selectionState.update(widget) }
    }

    public val delete: Handler<Widget> = handle { widgets, widget ->
        val scrollTo = widgets.indexOfFirst { it.id == widget.id }.let { index ->
            if (index + 1 in widgets.indices) widgets[index + 1]
            else widgets.getOrNull(index - 1)
        }
        widgets.filter { it.id != widget.id }
            .also { scrollTo?.also { selectionState.update(widget) } }
            .also { logger.info("${widget.title()} deleted") }
    }

    private fun Widget.title() = title.takeUnless { it.isNullOrBlank() }
        ?: registration.find(this)?.title
        ?: id

    private fun Widget.icon() = icon
        ?: registration.find(this)?.icon
        ?: SolidHeroIcons.question_mark_circle

    public fun render(renderContext: Tag<Element>) {
        runCatching { (renderContext.unsafeCast<Tag<HTMLElement>>()).trackScrolling() }
            .onFailure { logger.error("Failed to track scrolling", it) }

        val widgetAdditionControlsId = "widget-addition-controls"

        renderContext.dataCollection("contents") {
            data(
                data = this@Widgets.data,
                idProvider = { it.toString() }, // toString is used on purpose to ensure re-rendering on every widget change
            )

            dataCollectionItems("contents") {
                scrollIntoView(vertical = ScrollPosition.center)
                items.renderEach({ it.toString() }, into = this, batch = false) { widget: Widget ->

                    dataCollectionItem(
                        widget,
                        id = idProvider(widget),
                        classes = "widget",
                    ) {
                        // Conflated so that not edited widgets don't re-render when a widget is edited.
                        val editing = editedWidget.map { it?.id == widget.id }.distinctUntilChanged()
                        val reloading = reloading.data.map { it.any { it.id == widget.id } }.distinctUntilChanged()

                        // Flow that—when editing—emits all updates of the widget, otherwise the widget itself once.
                        val liveWidget: Flow<Widget> = editing.flatMapLatest {
                            if (it) editedWidget.filterNotNull()
                            else flowOf(widget)
                        }

                        div("widget-controls") {
                            div("flex flex-col items-start min-w-0") {
                                div("flex items-center justify-center gap-2 overflow-hidden") {
                                    liveWidget.render(this) {
                                        icon("shrink-0 w-4 h-4", it.icon())
                                        div("truncate") { +it.title() }
                                        a("rounded-full px-1 text-xs opacity-60") {
                                            href("#${it.id}")
                                            +"#${it.id}"
                                        }
                                    }
                                }
                            }

                            div("ml-8 flex items-center justify-center gap-3") {
                                controlButton(OutlineHeroIcons.square_2_stack, "Duplicate") {
                                    disabled(editing)
                                    clicks.map { widget } handledBy duplicate
                                }

                                controlButton(OutlineHeroIcons.pencil_square, "Edit") {
                                    disabled(editing)
                                    clicks.map { widget } handledBy {
                                        selectionState.update(it)
                                        editState.update(true)
                                    }
                                }

                                controlButton(OutlineHeroIcons.arrow_path, "Reload") {
                                    className(reloading.map { if (it) "transition animate-spin" else "transition hover:rotate-90" })
                                    disabled(editing)
                                    clicks.map { widget } handledBy reload
                                }

                                div("flex items-center justify-center gap-2") {
                                    controlButton(OutlineHeroIcons.arrow_small_up, "Move up") {
                                        disabled(items.map { it.firstOrNull() == widget })
                                        clicks.mapNotNull { widget } handledBy rankUp
                                    }
                                    div("flex items-center justify-end space-x-1 opacity-60") {
                                        div {
                                            items.map { it.indexOf(widget) }.render {
                                                div("animate-in spin-in slide-in-from-top") { +"${it + 1}" }
                                            }
                                        }
                                        div("font-extralight text-xs") { +"/" }
                                        div("font-extralight") { items.map { it.size }.render { +"$it" } }
                                    }
                                    controlButton(OutlineHeroIcons.arrow_small_down, "Move down") {
                                        disabled(items.map { it.lastOrNull() == widget })
                                        clicks.mapNotNull { widget } handledBy rankDown
                                    }
                                }
                            }
                        }

                        div("widget-content flex flex-row-reverse gap-8") {
                            div("flex-grow") {
                                liveWidget.combine(reloading, ::Pair).render(this) { (widget, r) ->
                                    if (r) div("absolute inset-0 text-default dark:text-invert flex items-center justify-center") {
                                        icon("w-8 h-8 animate-spin", SolidHeroIcons.arrow_path)
                                    } else widget.render(this).apply {
                                        syncState(syncState.map { it.map(lensForElement(widget, Widget::id)) })
                                    }
                                }
                            }
                            editing.combine(edited) { editing, editor ->
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

        renderContext.div("app-item", id = widgetAdditionControlsId) {
            div { icon("mx-auto w-12 h-12 text-default dark:text-invert opacity-60", SolidHeroIcons.squares_plus) }
            div("grid grid-cols-[repeat(auto-fit,_minmax(min(20rem,_100%),_1fr))] gap-8 m-8 items-start") {
                registration.forEach { (_, registration) ->
                    button(registration.icon, registration.title, registration.description, simple = true, inverted = true).apply {
                        clicks.map { registration.create(randomId()) } handledBy {
                            addOrUpdate(it)
                            selectionState.update(it)
                            editState.update(true)
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
                    clicks.map { renderContext } handledBy { it.scrollTo(top = 0) }
                }
                data.renderEach(
                    idProvider = { it.toString() }, // toString is used on purpose to ensure re-rendering on every widget change
                ) { widget: Widget ->
                    iconButton(
                        widget.icon(),
                        "Scroll to ${widget.title()}"
                    ) { clicks.map { widget } handledBy selectionState.update }
                }
                iconButton(
                    OutlineHeroIcons.squares_plus,
                    "Add widget",
                ) {
                    clicks.mapNotNull { renderContext.domNode.checkedOwnerDocument.getElementById("widget-addition-controls") } handledBy {
                        // TODO invoke correct scrollIntoView method
                        it.scrollIntoView()
                    }
                }
                iconButton(OutlineHeroIcons.arrow_long_down, "Scroll to bottom") {
                    clicks.map { renderContext } handledBy { it.scrollTo(top = it.domNode.scrollHeight) }
                }
            }
        }
    }

    override fun toString(): String = "Widgets($current)"

    private var blockTracking = true
    private fun Tag<HTMLElement>.trackScrolling() {
        scrollTops
            .mapNotNull { it.takeUnless { blockTracking } }
            .map { current.find { widget -> widget.isInView(idProvider) } }
            .distinctUntilChanged()
            .debounce(1.seconds) handledBy selectionState.update
    }

    init {
        selectionState.data
            .filterNotNull()
            .distinctUntilChanged() handledBy { widget ->
            delay(0.1.seconds)
            if (!widget.isInView(idProvider)) {
                // TODO invoke correct scrollIntoView method
                widget.findElement(idProvider)?.scrollIntoView()
            }
            blockTracking = false
        }
    }

    public companion object {
        private val Element.scrollMiddle get() = scrollTop.toInt() + clientHeight / 2
        private fun Widget.findElement(idProvider: (Widget) -> String): HTMLElement? =
            document.getElementById(idProvider(this))?.unsafeCast<HTMLElement>()

        private fun Widget.isInView(idProvider: (Widget) -> String): Boolean {
            val widgetElement = findElement(idProvider) ?: return false
            val offsetThreshold = widgetElement.closest(".app-scroll-container")?.scrollMiddle ?: return false
            return offsetThreshold >= widgetElement.offsetTop && offsetThreshold <= widgetElement.offsetTop + widgetElement.clientHeight
        }

    }
}

public fun Tag<Element>.panel(
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
