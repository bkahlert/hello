@file:Suppress("RedundantVisibilityModifier")

package playground.components.user

import com.bkahlert.hello.fritz2.components.heroicons.OutlineHeroIcons
import com.bkahlert.hello.fritz2.components.heroicons.SolidHeroIcons
import com.bkahlert.hello.fritz2.components.icon
import com.bkahlert.hello.fritz2.components.screenReaderOnly
import com.bkahlert.kommons.auth.diagnostics
import com.bkahlert.kommons.text.takeUnlessBlank
import com.bkahlert.kommons.uri.GravatarImageUri
import dev.fritz2.core.RenderContext
import dev.fritz2.core.classes
import dev.fritz2.core.transition
import dev.fritz2.core.type
import dev.fritz2.headless.components.menu
import dev.fritz2.headless.foundation.utils.popper.Placement
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

public fun RenderContext.userDropdown(
    classes: String?,
    userStore: UserStore,
) {
    userStore.data.render { user ->
        if (user == null) {
            button(
                classes(
                    "group",
                    "inline-flex w-full items-center justify-center",
                    "rounded-md",
                    "px-4 py-2",
                    "text-left",
                    "font-medium text-white",
                    "hover:box-glass",
                    "focus:outline-none focus-visible:ring focus-visible:ring-white focus-visible:ring-opacity-75"
                )
            ) {
                type("button")
                className("hover:box-glass")
                icon("shrink-0 mr-2 h-4 w-4 hidden group-hover:block", SolidHeroIcons.arrow_right_on_rectangle)
                icon("shrink-0 mr-2 h-4 w-4 group-hover:hidden", OutlineHeroIcons.arrow_right_on_rectangle)
                +"Sign-in"
                clicks handledBy userStore.signIn
            }
        } else menu(classes) {
            div {
                menuButton(
                    classes(
                        "flex",
                        "rounded-full bg-gray-800",
                        "focus:outline-none focus-visible:ring focus-visible:ring-white focus-visible:ring-opacity-75"
                    )
                ) {
                    className(opened.map { if (it) "ring ring-white ring-opacity-75" else "" })

                    val nickname = user.nickname
                    val picture = user.picture ?: user.email?.let { GravatarImageUri(it, size = 256) } ?: OutlineHeroIcons.user_circle

                    icon("shrink-0 h-8 w-8 rounded-full", picture) { attr("title", nickname) }

                    screenReaderOnly { opened.map { if (it) "Close Menu" else "Open Menu" }.renderText() }
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
                        selected.map { false } handledBy userStore.reauthorize
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
                            icon("shrink-0 mr-2 h-4 w-4", if (a) SolidHeroIcons.arrow_left_on_rectangle else OutlineHeroIcons.arrow_left_on_rectangle)
                        }
                        +"Sign-out"
                        selected handledBy userStore.signOut
                    }
                }

                div("px-1 py-1") {
                    div(
                        classes(
                            "rounded-md",
                            "px-2 py-2",
                            "sm:text-sm",
                            "text-left",
                            "shadow-inner shadow-black",
                        )
                    ) {
                        div("font-medium") { +"Diagnostics" }
                        dl {
                            user.claims.diagnostics.forEach { (key, value) ->
                                dt("mt-1 sm:text-xs") { +key }
                                dd("truncate") { +(value?.takeUnlessBlank() ?: "—") }
                            }
                        }
                    }
                }

            }
        }
    }
}

public fun RenderContext.userDropdown(
    userStore: UserStore,
) = userDropdown(null, userStore)
