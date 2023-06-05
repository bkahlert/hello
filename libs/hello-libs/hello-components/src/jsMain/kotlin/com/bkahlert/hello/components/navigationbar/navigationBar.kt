package com.bkahlert.hello.components.navigationbar

import com.bkahlert.hello.fritz2.ContentBuilder
import com.bkahlert.hello.fritz2.ContentBuilder2
import com.bkahlert.hello.fritz2.srOnly
import com.bkahlert.hello.icon.assets.Images
import com.bkahlert.hello.icon.heroicons.MiniHeroIcons
import com.bkahlert.hello.icon.heroicons.OutlineHeroIcons
import com.bkahlert.hello.icon.icon
import com.bkahlert.kommons.uri.Uri
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Store
import dev.fritz2.core.Tag
import dev.fritz2.core.alt
import dev.fritz2.core.classes
import dev.fritz2.core.disabled
import dev.fritz2.core.src
import dev.fritz2.core.transition
import dev.fritz2.core.type
import dev.fritz2.headless.components.Menu
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
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

public interface NavItem<T> {
    public val content: ContentBuilder2<Element, Store<T?>, Placement>?
    public val collapsedContent: (Menu<HTMLElement>.MenuItems<HTMLDivElement>.(RenderContext, selection: Store<T?>) -> Unit)?
}

public fun <T> Flow<NavItem<T>>.asNavItem(): NavItem<T> = object : NavItem<T> {
    override val content: ContentBuilder2<Element, Store<T?>, Placement> = { selection, placement ->
        render { it.content?.invoke(this, selection, placement) }
    }
    override val collapsedContent: (Menu<HTMLElement>.MenuItems<HTMLDivElement>.(RenderContext, selection: Store<T?>) -> Unit) =
        { renderContext, selection ->
            val menu: Menu<HTMLElement>.MenuItems<HTMLDivElement> = this
            render { it.collapsedContent?.invoke(menu, renderContext, selection) }
        }
}

public fun <T> Flow<NavItem<T>?>.asNavItem(renderNull: RenderContext.() -> Unit = {}): NavItem<T> = object :
    NavItem<T> {
    override val content: ContentBuilder2<Element, Store<T?>, Placement> = { selection, placement ->
        render { if (it != null) it.content?.invoke(this, selection, placement) else renderNull(this) }
    }

    // TODO does not update when items added
    override val collapsedContent: (Menu<HTMLElement>.MenuItems<HTMLDivElement>.(RenderContext, selection: Store<T?>) -> Unit) =
        { renderContext, selection ->
            val menu: Menu<HTMLElement>.MenuItems<HTMLDivElement> = this
            render { if (it != null) it.collapsedContent?.invoke(menu, renderContext, selection) else renderNull(renderContext) }
        }
}

public typealias SimpleNavItemGroup<T> = List<SimpleNavItem<T>>

public abstract class SimpleNavItem<T>(
    public val value: T,
) : NavItem<T> {
    public abstract val label: String
    public open val description: String? get() = null
    public abstract val icon: Uri
    public open val activeIcon: Uri get() = icon
    public open val disabled: Boolean = false
    public open val groups: List<SimpleNavItemGroup<T>> get() = emptyList()

    private fun selected(
        selection: Store<T?>,
        includeChildren: Boolean = true,
    ): Flow<Boolean> = selection.data.map {
        it == this || (includeChildren && groups.any { g -> g.any { i -> it == i } })
    }

    override val content: ContentBuilder2<Element, Store<T?>, Placement> = { selection, placement ->
        if (groups.flatten().isEmpty()) div("relative inline-block text-left") {
            val opened = flowOf(false)
            button(
                classes(
                    "inline-flex w-full items-center justify-center",
                    "rounded-md",
                    "px-4 py-2",
                    "text-left",
                    "font-medium text-white",
                    "hover:bg-glass hover:text-default dark:hover:text-invert",
                    "focus:outline-none focus-visible:ring focus-visible:ring-white focus-visible:ring-opacity-75"
                )
            ) {
                type("button")
                val d = disabled
                className(opened.combine(selected(selection)) { o, s ->
                    if (s) "bg-slate-800/75 ring ring-1 ring-inset ring-slate-800 text-white cursor-default"
                    else if (o && !d) "bg-glass text-default dark:text-invert"
                    else if (d) "opacity-50 cursor-default" else ""
                })
                opened.render { a ->
                    icon("shrink-0 mr-2 h-4 w-4", if (a) activeIcon else icon)
                }
                +label
                if (disabled) disabled(true)
                clicks.map { this@SimpleNavItem.value } handledBy selection.update
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
                        "hover:bg-glass hover:text-default dark:hover:text-invert",
                        "focus:outline-none focus-visible:ring focus-visible:ring-white focus-visible:ring-opacity-75"
                    )
                ) {
                    val d = disabled
                    className(opened.combine(selected(selection)) { o, s ->
                        if (s) "bg-slate-800/75 ring ring-1 ring-inset ring-slate-800 text-white cursor-default"
                        else if (o && !d) "bg-glass text-default dark:text-invert"
                        else if (d) "opacity-50 cursor-default" else ""
                    })
                    opened.render { a ->
                        icon("shrink-0 mr-2 h-4 w-4", if (a) activeIcon else icon)
                    }
                    +label
                    if (disabled) disabled(true)

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
                    "shadow-lg dark:shadow-xl bg-glass text-default dark:text-invert",
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

                groups.forEach { itemGroup ->
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
                                    else if (a && !d) "bg-glass text-default dark:text-invert"
                                    else if (d) "opacity-50 cursor-default" else ""
                                })
                                active.combine(s, Boolean::or).render { a ->
                                    icon("shrink-0 mr-2 h-4 w-4", if (a) item.activeIcon else item.icon)
                                }
                                +item.label
                                if (item.disabled) disable(true)
                                selected.map { item.value } handledBy selection.update
                            }
                        }
                    }
                }
            }
        }
    }

    override val collapsedContent: Menu<HTMLElement>.MenuItems<HTMLDivElement>.(RenderContext, Store<T?>) -> Unit = { renderContext, selection ->
        renderContext.menuItem(
            classes(
                "group",
                "flex w-full items-center",
                "rounded-md",
                "px-2 py-2",
                "text-left",
                "font-medium sm:text-sm",
            )
        ) {
            val s = selected(selection, false)
            className(active.combine(disabled.combine(s, Boolean::to)) { a, (d, s) ->
                if (s) "bg-slate-800/75 ring ring-1 ring-inset ring-slate-800 text-white cursor-default"
                else if (a && !d) "bg-glass text-default dark:text-invert"
                else if (d) "opacity-50 cursor-default" else ""
            })
            active.combine(s, Boolean::or).render { a ->
                icon("shrink-0 mr-2 h-4 w-4", if (a) activeIcon else icon)
            }
            +label
            if (this@SimpleNavItem.disabled) disable(true)
            selected.map { this@SimpleNavItem.value } handledBy selection.update
        }

        div("ml-2 px-1 py-1") {
            groups.flatten().forEach { item ->
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
                    val s = selected(selection, false)
                    className(active.combine(disabled.combine(s, Boolean::to)) { a, (d, s) ->
                        if (s) "bg-slate-800/75 ring ring-1 ring-inset ring-slate-800 text-white cursor-default"
                        else if (a && !d) "bg-glass text-default dark:text-invert"
                        else if (d) "opacity-50 cursor-default" else ""
                    })
                    active.combine(s, Boolean::or).render { a ->
                        icon("shrink-0 mr-2 h-4 w-4", if (a) activeIcon else icon)
                    }
                    +label
                    if (this@SimpleNavItem.disabled) disable(true)
                    selected.map { item.value } handledBy selection.update
                }
            }
        }
    }
}

public fun <T> RenderContext.navigationBar(
    classes: String?,
    navItems: List<NavItem<T>?>,
    selection: Store<T?>,
    startContent: ContentBuilder<HTMLDivElement>? = {
        div("flex flex-shrink-0 items-center") {
            img("shrink-0 h-8 w-auto") {
                src(Images.HelloFavicon.toString())
                alt("Hello!")
            }
        }
    },
    endContent: ContentBuilder<HTMLDivElement>? = null,
) {

    menu(classes, tag = RenderContext::nav) {
        div(
            classes(
                "mx-auto max-w-7xl",
                "px-2 sm:px-6 lg:px-8",
            )
        ) {
            // backdrop next to content to avoid cascaded effects (that don't work in all browsers)
            div(classes("absolute inset-0", "shadow-lg dark:shadow-xl bg-glass text-default dark:text-invert")) {}

            div("relative flex h-16 items-center justify-between") {
                // < sm: mobile menu button / hamburger menu
                div("absolute inset-y-0 left-0 flex items-center sm:hidden") {
                    menuButton(
                        classes(
                            "inline-flex items-center justify-center rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-white",
                            "hover:bg-glass hover:text-default dark:hover:text-invert",
                        )
                    ) {
                        srOnly { +"Open main menu" }
                        opened.render {
                            icon("shrink-0 block h-6 w-6", if (it) OutlineHeroIcons.x_mark else OutlineHeroIcons.bars_3)
                        }
                    }
                }

                div("flex flex-1 gap-x-8 min-w-0 items-center justify-center sm:items-stretch sm:justify-start") {
                    startContent?.invoke(this)

                    // >= sm: navigation
                    div("hidden sm:flex sm:min-w-0 sm:w-full sm:items-center sm:justify-center sm:gap-x-4") {
                        navItems.forEach { navItem ->
                            navItem?.content?.invoke(this, selection, bottomStart)
                        }
                    }
                }

                if (endContent != null) {
                    div("absolute inset-y-0 right-0 pr-2 flex items-center sm:static sm:inset-auto sm:ml-8 sm:pr-0") {
                        endContent()
                    }
                }
            }
        }

        menuItems(
            classes(
                "sm:hidden",
                "shadow-lg dark:shadow-xl bg-glass text-default dark:text-invert",
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
                classes("space-y-1")
            ) {
                navItems.forEach { navItem -> navItem?.collapsedContent?.invoke(this@menuItems, this, selection) }
            }
        }
    }
}

public fun <T> RenderContext.navigationBar(
    navItems: List<NavItem<T>?>,
    selection: Store<T?>,
    endContent: ContentBuilder<HTMLDivElement>? = null,
): Unit = navigationBar(null, navItems, selection, endContent)

public fun <T> Tag<Element>.navItem(
    navItem: NavItem<T>,
    selection: Store<T?>,
    placement: Placement = auto,
) {
    navItem.content?.invoke(this, selection, placement)
}

public fun <T> Tag<Element>.navItem(
    item: T,
    selection: Store<T?>,
    placement: Placement = auto,
    transform: (T) -> NavItem<T>,
) {
    navItem(transform(item), selection, placement)
}
