@file:Suppress("RedundantVisibilityModifier")

package com.bkahlert.hello.fritz2.components.headlessui

import com.bkahlert.hello.fritz2.components.Page
import com.bkahlert.hello.fritz2.components.heroicons.HeroIcons
import com.bkahlert.hello.fritz2.components.heroicons.MiniHeroIcons
import com.bkahlert.hello.fritz2.components.heroicons.SolidHeroIcons
import com.bkahlert.hello.fritz2.components.icon
import com.bkahlert.hello.fritz2.components.navigationbar.NavItem
import com.bkahlert.hello.fritz2.components.navigationbar.SimpleNavItem
import com.bkahlert.hello.fritz2.components.navigationbar.navItem
import com.bkahlert.hello.fritz2.components.navigationbar.navigationBar
import com.bkahlert.hello.fritz2.components.proseBox
import com.bkahlert.hello.fritz2.components.showcase.loremIpsumHeader
import com.bkahlert.hello.fritz2.components.showcase.loremIpsumParagraph
import com.bkahlert.hello.fritz2.components.showcase.placeholder
import com.bkahlert.hello.fritz2.components.showcase.showcase
import com.bkahlert.hello.fritz2.components.showcase.showcases
import com.bkahlert.kommons.js.ConsoleLogger
import com.bkahlert.kommons.uri.DataUri
import com.bkahlert.kommons.uri.Uri
import dev.fritz2.core.Handler
import dev.fritz2.core.RenderContext
import dev.fritz2.core.RootStore
import dev.fritz2.core.classes
import dev.fritz2.core.transition
import dev.fritz2.headless.components.menu
import dev.fritz2.headless.foundation.utils.popper.Placement
import dev.fritz2.headless.foundation.utils.popper.Placement.bottom
import dev.fritz2.headless.foundation.utils.popper.Placement.bottomEnd
import dev.fritz2.headless.foundation.utils.popper.Placement.bottomStart
import io.ktor.http.ContentType.Image
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

public object HeadlessUiShowcases : Page(
    "headless-ui",
    "Headless UI",
    "Headless UI showcases",
    icon = HeadlessUiIcons.headless_ui,
    content = {

        showcases("Custom", simple = true) {
            showcase("box-shadow", simple = true) {
                div("p-4") { div("box-shadow") { placeholder() } }
            }
            showcase("box-color-light", simple = true) {
                div("p-4") { div("box-color-light") { placeholder() } }
            }
            showcase("box-color-dark", simple = true) {
                div("p-4") { div("box-color-dark") { placeholder() } }
            }
            showcase("box-color", simple = true) {
                div("p-4") { div("box-color") { placeholder() } }
            }
            showcase("box-glass-light", simple = true) {
                div("p-4") { div("box-glass-light") { placeholder() } }
            }
            showcase("box-glass-dark", simple = true) {
                div("p-4") { div("box-glass-dark") { placeholder() } }
            }
            showcase("box-glass", simple = true) {
                div("p-4") { div("box-glass") { placeholder() } }
            }
            showcase("box-prose", simple = true) {
                proseBox {
                    loremIpsumHeader()
                    loremIpsumParagraph()
                }
            }
            showcase("btn", simple = true) {
                button("btn pointer-course:hidden") {
                    icon("w-4 h-4", SolidHeroIcons.cursor_arrow_rays)
                    +"Click me"
                }
                button("btn pointer-fine:hidden") {
                    icon("w-4 h-4", SolidHeroIcons.finger_print)
                    +"Tap me"
                }
            }
        }

        hr { }

        showcases("Menu (Dropdown)", HeadlessUiIcons.menu, simple = true) {

            val selection = object : RootStore<NavItem?>(null) {
                val logger = ConsoleLogger("Menu")
                val log: Handler<NavItem> = handle { _, item ->
                    item.also { logger.debug("selected", item) }
                }
            }

            val navItem = SimpleNavItem(
                label = "Document",
                heroIcon = HeroIcons::document,
                listOf(
                    SimpleNavItem("Edit", HeroIcons::pencil),
                    SimpleNavItem("Duplicate", HeroIcons::document_duplicate),
                ),
                listOf(
                    SimpleNavItem("Archive", HeroIcons::archive_box),
                    SimpleNavItem("Move", HeroIcons::arrow_top_right_on_square, disabled = true),
                ),
                listOf(
                    SimpleNavItem("Delete", HeroIcons::trash),
                ),
            )

            showcase("Default", simple = true) {
                div("p-4 text-right") { headlessUiMenu(navItem, selection.log) }
            }
            showcase("Customized (Auto)", simple = true) {
                div("p-4 text-center") { navItem(navItem, selection) }
            }
            showcase("Customized (Left)", simple = true) {
                div("p-4 text-left") { navItem(navItem, selection, placement = bottomStart) }
            }
            showcase("Customized (Center)", simple = true) {
                div("p-4 text-center") { navItem(navItem, selection, placement = bottom) }
            }
            showcase("Customized (Right)", simple = true) {
                div("p-4 text-right") { navItem(navItem, selection, placement = bottomEnd) }
            }
        }

        hr { }

        showcases("Navbar", HeadlessUiIcons.tabs, simple = true) {

            val selection = object : RootStore<NavItem?>(null) {
                val logger = ConsoleLogger("Menu")
                val log: Handler<NavItem> = handle { _, item ->
                    item.also { logger.debug("selected", item) }
                }
            }

            val navItems = listOf(
                SimpleNavItem(
                    "Foo", HeroIcons::document_text,
                    listOf(
                        SimpleNavItem("Edit", HeroIcons::pencil),
                        SimpleNavItem("Duplicate", HeroIcons::document_duplicate),
                    ),
                ),
                SimpleNavItem(
                    "Bar", HeroIcons::academic_cap,
                    listOf(
                        SimpleNavItem("Archive", HeroIcons::archive_box),
                        SimpleNavItem("Move", HeroIcons::arrow_top_right_on_square, disabled = true),
                    ),
                    listOf(
                        SimpleNavItem("Delete", HeroIcons::trash),
                    ),
                ),
                SimpleNavItem("Baz", HeroIcons::document_magnifying_glass, disabled = true),
            )

            showcase("xx-large", simple = true, classes = "col-span-full w-full max-w-screen-2xl") {
                navigationBar(navItems, selection)
            }
            showcase("small", simple = true, classes = "col-span-full w-full max-w-screen-sm") {
                navigationBar(navItems, selection)
            }
        }
    },
)

/**
 * @see <a href="https://headlessui.com/react/menu">Menu (Dropdown)</a>
 */
private fun RenderContext.headlessUiMenu(
    navItem: NavItem,
    action: Handler<NavItem>,
) {
    fun Uri.patch(classes: String) =
        DataUri(Image.SVG, unsafeCast<DataUri>().data.decodeToString().replace("<svg ", "<svg class=\"$classes\""))

    val itemGroups = navItem.groups.map { groups ->
        groups.map { item ->
            SimpleNavItem(
                label = item.label,
                icon = item.icon.patch("fill-violet-100 stroke-violet-500"),
                activeIcon = item.icon.patch("fill-transparent stroke-violet-200"),
                disabled = item.disabled,
            )
        }
    }

    menu("relative inline-block text-left") {
        div {
            menuButton(
                classes(
                    "inline-flex w-full justify-center",
                    "rounded-md bg-black bg-opacity-20",
                    "px-4 py-2",
                    "font-medium sm:text-sm text-white hover:bg-opacity-30",
                    "focus:outline-none focus-visible:ring-2 focus-visible:ring-white focus-visible:ring-opacity-75"
                )
            ) {
                className(opened.map { if (it) "bg-opacity-30" else "" })
                +navItem.label
                div(
                    classes(
                        "shrink-0 ml-2 -mr-1",
                        "transition-transform duration-100 ease-in-out motion-reduce:transition-none",
                    )
                ) {
                    className(opened.map { if (it) "rotate-0" else "rotate-[-90deg]" })
                    icon("h-5 w-5 text-violet-200 hover:text-violet-100", MiniHeroIcons.chevron_down)
                }
            }
        }

        menuItems(
            classes(
                "absolute right-0 mt-2 w-56 origin-top-right divide-y",
                "divide-gray-100 rounded-md bg-white shadow-lg",
                "ring-1 ring-black ring-opacity-5 focus:outline-none",
            )
        ) {

            placement = Placement.bottomStart
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

            itemGroups.forEach { itemGroup ->
                div("px-1 py-1") {
                    itemGroup.forEach { item ->
                        menuItem(
                            classes(
                                "group",
                                "flex w-full items-center",
                                "rounded-md",
                                "px-2 py-2",
                                "disabled:opacity-75",
                                "sm:text-sm",
                            )
                        ) {
                            className(active.combine(disabled) { a, d ->
                                if (a && !d) "bg-violet-500 text-white"
                                else if (d) "text-gray-400" else "text-gray-900"
                            })
                            active.render { active ->
                                icon("shrink-0 mr-2 h-4 w-4", if (active) item.activeIcon else item.icon)
                            }
                            +item.label
                            if (item.disabled) disable(true)
                            selected.map { item } handledBy action
                        }
                    }
                }
            }
        }
    }
}
