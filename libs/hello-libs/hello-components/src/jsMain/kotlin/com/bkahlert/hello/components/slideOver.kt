package com.bkahlert.hello.components

import com.bkahlert.hello.fritz2.ContentBuilder
import com.bkahlert.hello.icon.heroicons.SolidHeroIcons
import com.bkahlert.hello.icon.icon
import dev.fritz2.core.Keys
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Store
import dev.fritz2.core.Window
import dev.fritz2.core.classes
import dev.fritz2.core.shortcutOf
import dev.fritz2.core.transition
import dev.fritz2.core.type
import dev.fritz2.headless.components.modal
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import org.w3c.dom.HTMLDivElement

public fun RenderContext.slideOver(
    store: Store<Boolean>,
    name: String? = null,
    content: ContentBuilder<HTMLDivElement>? = null,
) {
    modal {
        openState(store)

        // copied from OpenClose as this Modal implementation does not support CloseOnEscape behavior
        openState.data.flatMapLatest { isOpen ->
            Window.keydowns.filter { isOpen && shortcutOf(it) == Keys.Escape }
        } handledBy close

        modalPanel("w-full fixed z-[25] inset-0") {

            modalOverlay("fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity") {
                // Background backdrop, show/hide based on slide-over state.
                transition(
                    "ease-in-out duration-500",
                    "opacity-0",
                    "opacity-100",
                    "ease-in-out duration-500",
                    "opacity-100",
                    "opacity-0",
                )
            }

            // Slide-over panel, show/hide based on slide-over state.
            div("pointer-events-none fixed inset-y-0 right-0 flex max-w-full pl-10") {
                transition(
                    "transform transition ease-in-out duration-500 sm:duration-700",
                    "translate-x-full",
                    "translate-x-0",
                    "transform transition ease-in-out duration-500 sm:duration-700",
                    "translate-x-0",
                    "translate-x-full",
                )

                div("pointer-events-auto relative w-screen max-w-md") {

                    // Close button, show/hide based on slide-over state.
                    div("absolute top-0 left-0 -ml-8 flex pt-4 pr-2 sm:-ml-10 sm:pr-4") {
                        transition(
                            "ease-in-out duration-500",
                            "opacity-0",
                            "opacity-100",
                            "ease-in-out duration-500",
                            "opacity-100",
                            "opacity-0",
                        )
                        button("rounded-md text-gray-300 hover:text-white focus:outline-none focus:ring-2 focus:ring-white") {
                            type("button")
                            screenReaderOnly { +"Close" }
                            icon("h-6 w-6", SolidHeroIcons.x_mark)
                            clicks handledBy close
                        }
                    }

                    // Content
                    div("flex h-full flex-col overflow-y-scroll overscroll-contain bg-white py-6 shadow-xl") {
                        div("px-4 sm:px-6") {
                            name?.also { modalTitle("text-base font-semibold leading-6 text-gray-900") { +it } }
                        }
                        div(
                            classes(
                                "relative mt-6 flex-1",
                                "px-4 sm:px-6"
                            )
                        ) {
                            content?.invoke(this)
                        }
                    }
                }
            }
        }
    }
}
