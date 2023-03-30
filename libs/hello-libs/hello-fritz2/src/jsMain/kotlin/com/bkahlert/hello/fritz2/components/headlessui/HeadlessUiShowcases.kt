@file:Suppress("RedundantVisibilityModifier")

package com.bkahlert.hello.fritz2.components.headlessui

import com.bkahlert.hello.fritz2.components.Page
import com.bkahlert.hello.fritz2.components.heroicons.HeroIcons
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
import dev.fritz2.core.Handler
import dev.fritz2.core.RootStore
import dev.fritz2.headless.foundation.utils.popper.Placement.bottom
import dev.fritz2.headless.foundation.utils.popper.Placement.bottomEnd
import dev.fritz2.headless.foundation.utils.popper.Placement.bottomStart

public object HeadlessUiShowcases : Page(
    "headless-ui",
    "Headless UI",
    "Headless UI showcases",
    icon = HeadlessUiIcons.headless_ui,
    pageContent = {

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

            showcase("Auto", simple = true, resizable = false) {
                div("p-4 text-center") { navItem(navItem, selection) }
            }
            showcase("Left", simple = true, resizable = false) {
                div("p-4 text-left") { navItem(navItem, selection, placement = bottomStart) }
            }
            showcase("Center", simple = true, resizable = false) {
                div("p-4 text-center") { navItem(navItem, selection, placement = bottom) }
            }
            showcase("Right", simple = true, resizable = false) {
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

            showcase("xx-large", simple = true, resizable = false, classes = "col-span-full w-full max-w-screen-2xl") {
                navigationBar(navItems, selection)
            }
            showcase("small", simple = true, resizable = false, classes = "col-span-full w-full max-w-screen-sm") {
                navigationBar(navItems, selection)
            }
        }
    },
)
