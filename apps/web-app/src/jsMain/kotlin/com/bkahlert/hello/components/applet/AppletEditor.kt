package com.bkahlert.hello.components.applet

import com.bkahlert.hello.fritz2.components.button
import com.bkahlert.hello.fritz2.components.heroicons.SolidHeroIcons
import com.bkahlert.hello.fritz2.components.icon
import com.bkahlert.hello.fritz2.components.metadata.Metadata
import dev.fritz2.core.EmittingHandler
import dev.fritz2.core.Handler
import dev.fritz2.core.Keys
import dev.fritz2.core.RenderContext
import dev.fritz2.core.RootStore
import dev.fritz2.core.Tag
import dev.fritz2.core.disabled
import dev.fritz2.core.shortcutOf
import dev.fritz2.core.storeOf
import dev.fritz2.core.transition
import dev.fritz2.headless.components.disclosure
import dev.fritz2.headless.foundation.InitialFocus
import dev.fritz2.headless.foundation.trapFocusInMountpoint
import dev.fritz2.history.History
import dev.fritz2.history.history
import dev.fritz2.tracking.Tracker
import dev.fritz2.tracking.tracker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.w3c.dom.HTMLElement
import kotlin.time.Duration.Companion.seconds

abstract class AppletEditor<T : Applet>(
    public val isNew: Boolean,
    applet: T,
) : RootStore<T>(applet) {

    public val history: History<T> = history(10, synced = true)

    public val addOrUpdate: EmittingHandler<Unit, Applet> by lazy {
        handleAndEmit { applet, _ ->
            emit(applet)
            applet
        }
    }

    public val autocompleting: Tracker = tracker()
    public open val autocomplete: EmittingHandler<Unit, Metadata> by lazy {
        data.distinctUntilChanged().debounce(2.seconds).map { it }
        handleAndEmit { value ->
            value
        }
    }

    public val cancel: EmittingHandler<Unit, Applet> by lazy {
        handleAndEmit { value ->
            history.clear()
            emit(value)
            value
        }
    }

    public val delete: EmittingHandler<Unit, Applet> by lazy {
        handleAndEmit { value ->
            history.clear()
            emit(value)
            value
        }
    }

    public val undo: Handler<Unit> by lazy {
        handle {
            history.back()
        }
    }

    abstract fun RenderContext.renderFields()

    fun RenderContext.render(labelledbyId: String? = null): Tag<HTMLElement> {
        val controlsOpen = storeOf(true)
        controlsOpen.data.filter { !it }.map { } handledBy cancel
        return disclosure {
            openState(controlsOpen)
            disclosurePanel("relative flex flex-col justify-between gap-5 p-8 pt-4 max-h-full sm:p-0") {
                keydowns.filter { shortcutOf(it) == Keys.Escape }.map { } handledBy close
                // TODO enter -> save but not in textarea
                trapFocusInMountpoint(setInitialFocus = InitialFocus.InsistToSet)

                transition(
                    opened,
                    "transition ease-out duration-400",
                    "opacity-0 -translate-y-full sm:translate-y-0 sm:-translate-x-full",
                    "opacity-100 translate-y-0 sm:translate-x-0",
                    "transition ease-in duration-400",
                    "opacity-100 translate-y-0 sm:translate-x-0",
                    "opacity-0 -translate-y-full sm:translate-y-0 sm:-translate-x-full",
                )

                h2(id = labelledbyId) { if (isNew) +"Create" else +"Edit" }
                div("flex flex-col sm:flex-row gap-8 justify-center relative") {
                    autocompleting.data.render(this) {
                        div("flex-grow flex flex-col gap-2 transition") {
                            className(if (it) "opacity-60" else "opacity-100")
                            renderFields()
                        }
                    }
                }

                div("flex items-center sm:justify-end gap-2 mt-4") {
                    button("Save").apply {
                        disabled(autocompleting.data)
                        clicks handledBy addOrUpdate
                        autocomplete handledBy { delay(1.seconds); domNode.focus() }
                    }
                    button(SolidHeroIcons.arrow_uturn_left, "Undo", iconOnly = true).apply {
                        disabled(autocompleting.data.combine(history.data.combine(data) { history, value ->
                            history.isNotEmpty() && history.first() != value
                        }) { autocompleting, changed ->
                            autocompleting || !changed
                        })
                        clicks handledBy undo
                    }
                    button("Cancel").apply {
                        disabled(autocompleting.data)
                        clicks.map { } handledBy cancel
                    }
                    if (!isNew) button("Delete").apply {
                        disabled(autocompleting.data)
                        className("bg-red-500/60")
                        clicks handledBy delete
                    }
                }

                div("absolute top-4 right-8 sm:top-0 sm:right-0 flex") {
                    transition(
                        opened,
                        "ease-in-out duration-700",
                        "opacity-0",
                        "opacity-100",
                        "ease-in-out duration-400",
                        "opacity-100",
                        "opacity-0",
                    )
                    disclosureCloseButton("text-current") {
                        icon("h-6 w-6", SolidHeroIcons.x_mark)
                    }
                }
            }
        }
    }
}
