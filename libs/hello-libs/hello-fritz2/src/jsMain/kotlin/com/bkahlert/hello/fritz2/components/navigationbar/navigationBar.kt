package com.bkahlert.hello.fritz2.components.navigationbar

import com.bkahlert.hello.fritz2.ContentBuilder
import com.bkahlert.hello.fritz2.components.assets.Images
import com.bkahlert.hello.fritz2.components.heroicons.HeroIcons
import com.bkahlert.hello.fritz2.components.heroicons.MiniHeroIcons
import com.bkahlert.hello.fritz2.components.heroicons.OutlineHeroIcons
import com.bkahlert.hello.fritz2.components.heroicons.SolidHeroIcons
import com.bkahlert.hello.fritz2.components.icon
import com.bkahlert.hello.fritz2.components.screenReaderOnly
import com.bkahlert.kommons.uri.DataUri
import com.bkahlert.kommons.uri.Uri
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Store
import dev.fritz2.core.alt
import dev.fritz2.core.classes
import dev.fritz2.core.disabled
import dev.fritz2.core.src
import dev.fritz2.core.transition
import dev.fritz2.core.type
import dev.fritz2.headless.components.menu
import dev.fritz2.headless.foundation.utils.popper.Placement
import dev.fritz2.headless.foundation.utils.popper.Placement.auto
import dev.fritz2.headless.foundation.utils.popper.Placement.bottom
import dev.fritz2.headless.foundation.utils.popper.Placement.bottomEnd
import dev.fritz2.headless.foundation.utils.popper.Placement.bottomStart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlin.reflect.KProperty1

public interface NavItem {
    public val label: String
    public val description: String?
    public val icon: Uri
    public val activeIcon: Uri
    public val disabled: Boolean
    public val groups: List<NavItemGroup>
}

public typealias NavItemGroup = List<NavItem>

public val NavItem.items: List<NavItem> get() = groups.flatten()

public fun NavItem.selected(
    selection: Store<NavItem?>,
    includeChildren: Boolean = true,
): Flow<Boolean> = selection.data.map {
    it == this || (includeChildren && groups.any { g -> g.any { i -> it == i } })
}

public data class SimpleNavItem(
    override val label: String,
    override val description: String? = null,
    override val icon: Uri,
    override val activeIcon: Uri,
    override val disabled: Boolean = false,
    override val groups: List<NavItemGroup> = emptyList(),
) : NavItem {
    public constructor(
        label: String,
        description: String?,
        heroIcon: KProperty1<HeroIcons, DataUri>,
        vararg groups: NavItemGroup,
        disabled: Boolean = false,
    ) : this(
        label = label,
        description = description,
        icon = heroIcon.get(OutlineHeroIcons),
        activeIcon = heroIcon.get(SolidHeroIcons),
        disabled = disabled,
        groups = groups.asList()
    )

    public constructor(
        label: String,
        heroIcon: KProperty1<HeroIcons, DataUri>,
        vararg groups: NavItemGroup,
        disabled: Boolean = false,
    ) : this(label, null, heroIcon, *groups, disabled = disabled)
}

public fun RenderContext.navigationBar(
    classes: String?,
    navItems: List<NavItem>,
    selection: Store<NavItem?>,
    startContent: ContentBuilder? = {
        div("flex flex-shrink-0 items-center") {
            // < lg: logo
            img("shrink-0 block h-8 w-auto lg:hidden") {
                src(Images.HelloFavicon.toString())
                alt("Hello!")
            }
            // >= lg: logo
            img("shrink-0 hidden h-8 w-auto lg:block") {
                src(Images.HelloFavicon.toString())
                alt("Hello!")
            }
        }
    },
    endContent: ContentBuilder? = null,
) {

    menu(classes, tag = RenderContext::nav) {
        div(
            classes(
                "mx-auto max-w-7xl",
                "px-2 sm:px-6 lg:px-8",
            )
        ) {
            // backdrop next to content to avoid cascaded effects (that don't work in all browsers)
            div(classes("absolute inset-0", "box-shadow box-glass")) {}

            div("relative flex h-16 items-center justify-between") {
                // < sm: mobile menu button / hamburger menu
                div("absolute inset-y-0 left-0 flex items-center sm:hidden") {
                    menuButton(
                        classes(
                            "inline-flex items-center justify-center rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-white",
                            "hover:bg-slate-800/50 hover:text-white"
                        )
                    ) {
                        screenReaderOnly { +"Open main menu" }
                        opened.render {
                            icon("shrink-0 block h-6 w-6", if (it) OutlineHeroIcons.x_mark else OutlineHeroIcons.bars_3)
                        }
                    }
                }

                div("flex flex-1 items-center justify-center sm:items-stretch sm:justify-start") {
                    startContent?.invoke(this)

                    // >= sm: navigation
                    div("hidden sm:mx-auto sm:block") {
                        div("flex flex-wrap gap-x-4") {
                            navItems.forEach { navItem ->
                                navItem(navItem, selection, bottomStart)
                            }
                        }
                    }
                }

                if (endContent != null) {
                    div("absolute inset-y-0 right-0 pr-2 flex items-center sm:static sm:inset-auto sm:ml-6 sm:pr-0") {
                        endContent()
                    }
                }
            }
        }

        menuItems(
            classes(
                "sm:hidden",
                "box-shadow box-glass",
                "max-h-[calc(100vh-4rem)] overflow-y-auto",
                "focus:outline-none",
            )
        ) {

            placement = bottom
            distance = 12

            transition(
                opened,
                "transition ease-out duration-100 origin-top",
                "transform opacity-0 scale-95",
                "transform opacity-100 scale-100",
                "transition ease-in duration-75 origin-top",
                "transform opacity-100 scale-100",
                "transform opacity-0 scale-95",
            )

            div(
                classes(
                    "space-y-1",
                    "px-2 pt-2 pb-3",
                )
            ) {
                navItems.forEach { navItem ->
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
                        val s = navItem.selected(selection, false)
                        className(active.combine(disabled.combine(s, Boolean::to)) { a, (d, s) ->
                            if (s) "bg-slate-800/75 ring ring-1 ring-inset ring-slate-800 text-white cursor-default"
                            else if (a && !d) "box-glass"
                            else if (d) "opacity-50 cursor-default" else ""
                        })
                        active.combine(s, Boolean::or).render { a ->
                            icon("shrink-0 mr-2 h-4 w-4", if (a) navItem.activeIcon else navItem.icon)
                        }
                        +navItem.label
                        if (navItem.disabled) disable(true)
                        selected.map { navItem } handledBy selection.update
                    }

                    div("ml-2 px-1 py-1") {
                        navItem.items.forEach { item ->
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
                                val s = item.selected(selection, false)
                                className(active.combine(disabled.combine(s, Boolean::to)) { a, (d, s) ->
                                    if (s) "bg-slate-800/75 ring ring-1 ring-inset ring-slate-800 text-white cursor-default"
                                    else if (a && !d) "box-glass"
                                    else if (d) "opacity-50 cursor-default" else ""
                                })
                                active.combine(s, Boolean::or).render { a ->
                                    icon("shrink-0 mr-2 h-4 w-4", if (a) item.activeIcon else item.icon)
                                }
                                +item.label
                                if (item.disabled) disable(true)
                                selected.map { item } handledBy selection.update
                            }
                        }
                    }
                }
            }
        }
    }
}

public fun RenderContext.navigationBar(
    navItems: List<NavItem>,
    selection: Store<NavItem?>,
    endContent: ContentBuilder? = null,
): Unit = navigationBar(null, navItems, selection, endContent)

public fun RenderContext.navItem(
    navItem: NavItem,
    selection: Store<NavItem?>,
    placement: Placement = auto,
) {
    if (navItem.items.isEmpty()) div("relative inline-block text-left") {
        val opened = flowOf(false)
        button(
            classes(
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
            val d = navItem.disabled
            className(opened.combine(navItem.selected(selection)) { o, s ->
                if (s) "bg-slate-800/75 ring ring-1 ring-inset ring-slate-800 text-white cursor-default"
                else if (o && !d) "box-glass"
                else if (d) "opacity-50 cursor-default" else ""
            })
            opened.render { a ->
                icon("shrink-0 mr-2 h-4 w-4", if (a) navItem.activeIcon else navItem.icon)
            }
            +navItem.label
            if (navItem.disabled) disabled(true)
            clicks.map { navItem } handledBy selection.update
        }
    } else menu("relative inline-block text-left") {
        div {
            menuButton(
                classes(
                    "inline-flex w-full items-center justify-center",
                    "rounded-md",
                    "px-4 py-2",
                    "text-left",
                    "font-medium text-white",
                    "hover:box-glass",
                    "focus:outline-none focus-visible:ring focus-visible:ring-white focus-visible:ring-opacity-75"
                )
            ) {
                val d = navItem.disabled
                className(opened.combine(navItem.selected(selection)) { o, s ->
                    if (s) "bg-slate-800/75 ring ring-1 ring-inset ring-slate-800 text-white cursor-default"
                    else if (o && !d) "box-glass"
                    else if (d) "opacity-50 cursor-default" else ""
                })
                opened.render { a ->
                    icon("shrink-0 mr-2 h-4 w-4", if (a) navItem.activeIcon else navItem.icon)
                }
                +navItem.label
                if (navItem.disabled) disabled(true)

                div(
                    classes(
                        "shrink-0 ml-2 -mr-1",
                        "transition-transform duration-100 ease-in-out motion-reduce:transition-none",
                    )
                ) {
                    className(opened.map { if (it) "rotate-0" else "rotate-[-90deg]" })
                    icon("h-5 w-5", MiniHeroIcons.chevron_down)
                }
            }
        }

        menuItems(
            classes(
                when (placement) {
                    bottomStart -> "origin-top-left"
                    bottomEnd -> "absolute right-0 origin-top-right"
                    else -> null
                },
                "w-56",
                "divide-y divide-slate-500/50 dark:divide-slate-400/50",
                "rounded-md",
                "box-shadow box-glass",
                "focus:outline-none",
            )
        ) {

            this.placement = placement
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

            navItem.groups.forEach { itemGroup ->
                div("px-1 py-1") {
                    itemGroup.forEach { item ->
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
                            val s = item.selected(selection)
                            className(active.combine(disabled.combine(s, Boolean::to)) { a, (d, s) ->
                                if (s) "bg-slate-800/75 ring ring-1 ring-inset ring-slate-800 text-white cursor-default"
                                else if (a && !d) "box-glass"
                                else if (d) "opacity-50 cursor-default" else ""
                            })
                            active.combine(s, Boolean::or).render { a ->
                                icon("shrink-0 mr-2 h-4 w-4", if (a) item.activeIcon else item.icon)
                            }
                            +item.label
                            if (item.disabled) disable(true)
                            selected.map { item } handledBy selection.update
                        }
                    }
                }
            }
        }
    }
}
