@file:Suppress("RedundantVisibilityModifier")

package com.bkahlert.hello.fritz2.app.user

import com.bkahlert.hello.fritz2.app.session.SessionStore
import com.bkahlert.hello.fritz2.components.heroicons.OutlineHeroIcons
import com.bkahlert.hello.fritz2.components.heroicons.SolidHeroIcons
import com.bkahlert.hello.fritz2.components.icon
import com.bkahlert.hello.fritz2.components.screenReaderOnly
import com.bkahlert.kommons.auth.Session
import com.bkahlert.kommons.auth.diagnostics
import com.bkahlert.kommons.uri.GravatarImageUri
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Tag
import dev.fritz2.core.classes
import dev.fritz2.core.title
import dev.fritz2.core.transition
import dev.fritz2.headless.components.menu
import dev.fritz2.headless.foundation.utils.popper.Placement
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.w3c.dom.HTMLButtonElement

public fun RenderContext.userDropdown(
    classes: String?,
    sessionStore: SessionStore,
    customize: (Tag<HTMLButtonElement>.() -> Unit)? = null,
    onDiagnostics: (() -> Unit)? = null,
) {
    sessionStore.data.render { session ->
        val user = (session as? Session.AuthorizedSession)?.let(::User)
        menu(classes) {
            div {
                menuButton(
                    classes(
                        "group",
                        "flex",
                        "rounded-full",
                        "bg-transparent",
                        "text-white",
                        "focus:outline-none focus-visible:ring focus-visible:ring-white focus-visible:ring-opacity-75"
                    )
                ) {
                    className(opened.map { if (it) "ring ring-white ring-opacity-75" else "" })

                    if (user != null) {
                        val nickname = user.nickname
                        val picture = user.picture ?: user.email?.let { GravatarImageUri(it, size = 256) } ?: SolidHeroIcons.user_circle
                        icon("shrink-0 h-8 w-8 rounded-full", picture) { attr("title", nickname) }
                    } else {
                        icon("shrink-0 h-8 w-8 hidden group-hover:block", SolidHeroIcons.user_circle)
                        icon("shrink-0 h-8 w-8 group-hover:hidden", OutlineHeroIcons.user_circle)
                    }

                    screenReaderOnly { opened.map { if (it) "Close Menu" else "Open Menu" }.renderText() }
                    customize?.invoke(this)
                }
            }

            menuItems(
                classes(
                    "absolute right-0 origin-top-right",
                    "w-48",
                    "rounded-md",
                    "box-shadow box-glass",
                    "focus:outline-none"
                )
            ) {
                placement = Placement.bottomEnd
                distance = 5

                transition(
                    opened,
                    "transition ease-out duration-100",
                    "opacity-0 scale-95",
                    "opacity-100 scale-100",
                    "transition ease-in duration-75",
                    "opacity-100 scale-100",
                    "opacity-0 scale-95",
                )

                div("px-1 py-1") {
                    menuItem(
                        classes(
                            "group",
                            "flex w-full items-center",
                            "rounded-md",
                            "px-2 py-2",
                            "text-left",
                            "font-medium sm:text-sm",
                        )
                    ) {
                        className(active.combine(disabled) { a, d ->
                            if (a && !d) "box-glass"
                            else if (d) "opacity-50 cursor-default" else ""
                        })
                        active.render { a ->
                            icon("shrink-0 mr-2 h-4 w-4", if (a) SolidHeroIcons.arrow_path else OutlineHeroIcons.arrow_path)
                        }
                        +"Refresh"
                        selected.map { false } handledBy sessionStore.reauthorize
                    }

                    if (user == null && onDiagnostics != null) {
                        menuItem(
                            classes(
                                "group",
                                "flex w-full items-center",
                                "rounded-md",
                                "px-2 py-2",
                                "text-left",
                                "font-medium sm:text-sm",
                            )
                        ) {
                            className(active.combine(disabled) { a, d ->
                                if (a && !d) "box-glass"
                                else if (d) "opacity-50 cursor-default" else ""
                            })
                            active.render { a ->
                                icon("shrink-0 mr-2 h-4 w-4", if (a) SolidHeroIcons.information_circle else OutlineHeroIcons.information_circle)
                            }
                            +"Diagnostics"
                            selected handledBy { onDiagnostics() }
                        }
                    }

                    menuItem(
                        classes(
                            "group",
                            "flex w-full items-center",
                            "rounded-md",
                            "px-2 py-2",
                            "text-left",
                            "font-medium sm:text-sm",
                        )
                    ) {
                        className(active.combine(disabled) { a, d ->
                            if (a && !d) "box-glass"
                            else if (d) "opacity-50 cursor-default" else ""
                        })
                        active.render { a ->
                            if (user != null) {
                                icon("shrink-0 mr-2 h-4 w-4", if (a) SolidHeroIcons.arrow_left_on_rectangle else OutlineHeroIcons.arrow_left_on_rectangle)
                            } else {
                                icon("shrink-0 mr-2 h-4 w-4", if (a) SolidHeroIcons.arrow_right_on_rectangle else OutlineHeroIcons.arrow_right_on_rectangle)
                            }
                        }
                        if (user != null) {
                            +"Sign-out"
                            selected handledBy sessionStore.unauthorize
                        } else {
                            +"Sign-in"
                            selected handledBy sessionStore.authorize
                        }
                    }

                    if (user != null) {
                        menuItem(
                            classes(
                                "rounded-md",
                                "w-full",
                                "px-3 py-2",
                                "sm:text-sm",
                                "text-left",
                                "shadow-inner",
                            )
                        ) {
                            val disabled = onDiagnostics == null
                            className(active.map { a ->
                                if (a && !disabled) "box-glass"
                                else if (disabled) "opacity-50 cursor-default" else ""
                            })
                            selected handledBy { onDiagnostics?.invoke() }

                            div("font-medium") { +"Diagnostics" }
                            dl {
                                user.claims.diagnostics.forEach { (key, value) ->
                                    val text = value?.takeUnless { it.isBlank() } ?: "—"
                                    dt("mt-1 sm:text-xs") { +key }
                                    dd("truncate") {
                                        title(text)
                                        +text
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

public fun RenderContext.userDropdown(
    sessionStore: SessionStore,
    customize: (Tag<HTMLButtonElement>.() -> Unit)? = null,
    onDiagnostics: (() -> Unit)? = null,
): Unit = userDropdown(null, sessionStore, customize, onDiagnostics)
