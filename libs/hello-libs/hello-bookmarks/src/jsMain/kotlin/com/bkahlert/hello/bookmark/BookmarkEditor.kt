package com.bkahlert.hello.bookmark

import com.bkahlert.hello.button.button
import com.bkahlert.hello.editor.UriLens
import com.bkahlert.hello.fritz2.mergeValidationMessages
import com.bkahlert.hello.icon.heroicons.SolidHeroIcons
import com.bkahlert.hello.icon.icon
import com.bkahlert.hello.metadata.Metadata
import com.bkahlert.hello.metadata.fetchMetadata
import com.bkahlert.kommons.uri.host
import com.bkahlert.kommons.uri.toUriOrNull
import dev.fritz2.core.EmittingHandler
import dev.fritz2.core.Handler
import dev.fritz2.core.RenderContext
import dev.fritz2.core.RootStore
import dev.fritz2.core.disabled
import dev.fritz2.core.placeholder
import dev.fritz2.core.required
import dev.fritz2.core.type
import dev.fritz2.core.values
import dev.fritz2.headless.components.inputField
import dev.fritz2.headless.foundation.setInitialFocus
import dev.fritz2.history.History
import dev.fritz2.history.history
import dev.fritz2.tracking.Tracker
import dev.fritz2.tracking.tracker
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlin.time.Duration.Companion.seconds

public class BookmarkEditor(
    public val isNew: Boolean,
    bookmark: Bookmark,
) : RootStore<Bookmark>(bookmark) {
    public val history: History<Bookmark> = history(10, synced = true)

    public val addOrUpdate: EmittingHandler<Unit, Bookmark> = handleAndEmit { bookmark ->
        emit(bookmark)
        bookmark
    }

    public val autocompleting: Tracker = tracker()
    public val autocomplete: EmittingHandler<Unit, Metadata> = handleAndEmit { bookmark ->
        val uri = bookmark.href?.takeIf { it.host.orEmpty().contains(".") }
        if (uri != null) {
            autocompleting.track {
                uri.fetchMetadata()?.let { metadata ->
                    emit(metadata)
                    bookmark.copy(
                        title = metadata.title,
                        icon = metadata.favicon?.toUriOrNull() ?: bookmark.icon,
                    )
                }
            } ?: bookmark
        } else {
            bookmark
        }
    }

    public val cancel: EmittingHandler<Unit, Bookmark> = handleAndEmit { _ ->
        history.clear()
        emit(bookmark)
        Bookmark()
    }

    public val delete: EmittingHandler<Unit, Bookmark> = handleAndEmit { bookmark ->
        history.clear()
        emit(bookmark)
        Bookmark()
    }

    public val undo: Handler<Unit> = handle {
        history.back()
    }

    public fun RenderContext.render(labelledbyId: String? = null) {
        div("relative flex flex-col justify-between gap-5 p-8 pt-4 max-h-full sm:p-0") {
            h2(id = labelledbyId) { if (isNew) +"Create bookmark" else +"Edit bookmark" }

            div("flex flex-col sm:flex-row gap-8 justify-center") {
                div("flex-grow flex flex-col gap-2") {
                    inputField {
                        value(map(Bookmark.uri()))
                        inputLabel {
                            +"Href"
                            inputTextfield {
                                type("url")
                                placeholder("https://example.com")
                                required(true)
                                disabled(autocompleting.data)
                                className(autocompleting.data.map {
                                    if (it) "disabled:opacity-100 bg-gradient-to-r from-swatch-blue to-transparent animate-bg-x-1/2 placeholder:opacity-0"
                                    else ""
                                })
                                setInitialFocus()
                                focuss handledBy { domNode.select() }
                                changes.debounce(0.2.seconds) handledBy autocomplete
                            }.also(::mergeValidationMessages)
                        }
                    }
                    inputField {
                        value(map(Bookmark.title()))
                        inputLabel {
                            +"Title"
                            inputTextfield("transition") {
                                type("text")
                                placeholder("Automatic")
                                disabled(autocompleting.data)
                                className(autocompleting.data.map {
                                    if (it) "disabled:opacity-100 bg-gradient-to-r from-swatch-blue to-transparent animate-bg-x-1/2 placeholder:opacity-0"
                                    else ""
                                })
                            }.also(::mergeValidationMessages)
                        }
                    }
                    inputField {
                        value(map(Bookmark.icon()))
                        inputLabel {
                            +"Icon"
                            div("flex items-center gap-4") {
                                val changes = inputTextfield {
                                    type("url")
                                    placeholder("Automatic")
                                    disabled(autocompleting.data)
                                    className(autocompleting.data.map {
                                        if (it) "disabled:opacity-100 bg-gradient-to-r from-swatch-blue to-transparent animate-bg-x-1/2 placeholder:opacity-0"
                                        else ""
                                    })
                                }.also(::mergeValidationMessages).changes
                                div("w-8 h-8") {
                                    merge(value.data, changes.values())
                                        .map { UriLens.set(null, it) }
                                        .render(this) { icon ->
                                            current.copy(icon = icon ?: SolidHeroIcons.bookmark).also {
                                                with(it) {
                                                    render(null, RenderContext::button)
                                                }
                                            }
                                        }
                                }
                            }
                        }
                    }
                }
            }

            div("flex items-center sm:justify-end gap-2 mt-4") {
                button("Save").apply {
                    clicks handledBy addOrUpdate
                    autocomplete handledBy { domNode.focus() }
                }
                button(SolidHeroIcons.arrow_uturn_left, "Undo", iconOnly = true).apply {
                    className(history.data.combine(data) { history, value ->
                        history.isNotEmpty() && history.first() != value
                    }.map { if (it) "" else "hidden" })
                    clicks handledBy undo
                }
                button("Cancel").apply {
                    clicks.map { } handledBy cancel
                }
                if (!isNew) button("Delete").apply {
                    className("bg-red-500/60")
                    clicks handledBy delete
                }
            }

            div("absolute top-4 right-8 sm:top-0 sm:right-0 flex") {
                button("text-current") {
                    icon("h-6 w-6", SolidHeroIcons.x_mark)
                    clicks handledBy cancel
                }
            }
        }
    }
}
