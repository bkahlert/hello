package com.bkahlert.hello.app.showcase

import com.bkahlert.hello.button.button
import com.bkahlert.hello.components.SimplePage
import com.bkahlert.hello.components.headlessui.Item
import com.bkahlert.hello.components.headlessui.ItemNavItem
import com.bkahlert.hello.components.navigationbar.navItem
import com.bkahlert.hello.components.navigationbar.navigationBar
import com.bkahlert.hello.components.proseBox
import com.bkahlert.hello.icon.headlessui.HeadlessUiIcons
import com.bkahlert.hello.icon.heroicons.HeroIcons
import com.bkahlert.hello.icon.heroicons.OutlineHeroIcons
import com.bkahlert.hello.icon.heroicons.SolidHeroIcons
import com.bkahlert.hello.icon.icon
import com.bkahlert.hello.showcase.LoremIpsum
import com.bkahlert.hello.showcase.loremIpsumHeader
import com.bkahlert.hello.showcase.loremIpsumParagraph
import com.bkahlert.hello.showcase.placeholder
import com.bkahlert.hello.showcase.showcase
import com.bkahlert.hello.showcase.showcases
import com.bkahlert.kommons.js.ConsoleLogger
import dev.fritz2.core.Handler
import dev.fritz2.core.RenderContext
import dev.fritz2.core.RootStore
import dev.fritz2.core.classes
import dev.fritz2.headless.foundation.utils.popper.Placement

public object HeadlessUiShowcases : SimplePage(
    "headless-ui",
    "Headless UI",
    "Headless UI showcases",
    icon = HeadlessUiIcons.headless_ui,
    content = {

        showcases("Custom") {
            showcase("box-prose") {
                proseBox {
                    loremIpsumHeader()
                    loremIpsumParagraph()
                }
            }

            showcase("pointer:") {
                div("flex flex-col items-center justify-center gap-4") {
                    button("pointer-coarse:opacity-30", SolidHeroIcons.cursor_arrow_rays, "Click me")
                    button("pointer-fine:opacity-30", SolidHeroIcons.finger_print, "Tap me")
                }
            }
        }

        hr { }

        showcases("Glass", OutlineHeroIcons.stop) {
            fun RenderContext.glassShowcase(classes: String) {
                showcase(classes) {
                    div("relative") {
                        placeholder()
                        div("absolute top-1/2 left-1/2 -mt-8 -ml-8") {
                            div("animate-[spin_2s_3]") {
                                icon("w-16 h-16 animate-[pulse_3s_2]", SolidHeroIcons.stop)
                            }
                        }
                        div(classes("absolute inset-0 right-1/2", classes)) { }
                    }
                }
            }
            glassShowcase("bg-glass")
            glassShowcase("bg-glass bg-glass-invert")
            glassShowcase("bg-glass bg-amber-500/25")
            glassShowcase("bg-glass bg-glass-invert bg-amber-500/25")
        }

        hr { }

        showcases("Gamut", SolidHeroIcons.computer_desktop) {
            fun gamutShowcase(classes: String) {
                val gamut = classes.substringBefore(":")
                showcase("$gamut:") {
                    div("relative sm:rounded-b-xl overflow-hidden") {
                        placeholder()
                        div("absolute inset-0 grid items-center justify-center bg-red-500 font-bold") {
                            +"${gamut.uppercase()} not supported"
                        }
                        div(
                            classes(
                                "absolute inset-0 grid items-center justify-center bg-green-500 font-bold hidden",
                                classes
                            )
                        ) {
                            +"${gamut.uppercase()} supported"
                        }
                    }
                }
            }
            gamutShowcase("srgb:grid")
            gamutShowcase("p3:grid")
            gamutShowcase("rec2020:grid")
        }

        hr { }

        showcases("Color space", SolidHeroIcons.eye_dropper) {
            fun csShowcase(classes: String) {
                showcase(classes) {
                    div(classes(classes, "from-swatch-blue to-swatch-yellow")) { placeholder() }
                }
            }
            csShowcase("bg-gradient-to-r")
            csShowcase("bg-gradient-to-r-in-srgb")
            csShowcase("bg-gradient-to-r-in-hsl")
            csShowcase("bg-gradient-to-r-in-hwb")
            csShowcase("bg-gradient-to-r-in-srgb-linear")
            csShowcase("bg-gradient-to-r-in-display-p3")
            csShowcase("bg-gradient-to-r-in-lch")
            csShowcase("bg-gradient-to-r-in-oklch")
            csShowcase("bg-gradient-to-r-in-oklab")
            csShowcase("bg-gradient-to-r-in-rec2020")
            csShowcase("bg-gradient-to-r-in-a98-rgb")
            csShowcase("bg-gradient-to-r-in-prophoto-rgb")
            csShowcase("bg-gradient-to-r-in-xyz")
        }

        hr { }

        showcases("Button", HeadlessUiIcons.switch) {

            fun RenderContext.buttonsShowcase(
                caption: String,
                simple: Boolean = false,
                inverted: Boolean = false,
                iconOnly: Boolean = false,
            ) {
                showcase(caption) {
                    div("flex flex-col items-center justify-center gap-4") {
                        button("Caption", simple = simple, inverted = inverted, iconOnly = iconOnly)
                        button("Caption", description = LoremIpsum, simple = simple, inverted = inverted, iconOnly = iconOnly)
                        button(SolidHeroIcons.random(), "Caption", simple = simple, inverted = inverted, iconOnly = iconOnly)
                        button(SolidHeroIcons.random(), "Caption", LoremIpsum, simple = simple, inverted = inverted, iconOnly = iconOnly)
                    }
                }
            }

            buttonsShowcase("btn")
            buttonsShowcase("btn: Simple", simple = true)
            buttonsShowcase("btn: Invert", inverted = true)
            buttonsShowcase("btn: Simple+Invert", simple = true, inverted = true)

            buttonsShowcase("btn: Icon-only", iconOnly = true)
            buttonsShowcase("btn: Icon-only+Simple", iconOnly = true, simple = true)
            buttonsShowcase("btn: Icon-only+Invert", iconOnly = true, inverted = true)
            buttonsShowcase("btn: Icon-only+Simple+Invert", iconOnly = true, simple = true, inverted = true)
        }

        hr { }

        showcases("Menu (Dropdown)", HeadlessUiIcons.menu) {

            val selection = object : RootStore<Item?>(null) {
                val logger = ConsoleLogger("Menu")
                val log: Handler<Item> = handle { _, item ->
                    item.also { logger.debug("selected", item) }
                }
            }

            val navItem = ItemNavItem(
                Item(
                    "Document",
                    HeroIcons::document,
                    listOf(
                        Item("Edit", HeroIcons::pencil),
                        Item("Duplicate", HeroIcons::document_duplicate),
                    ),
                    listOf(
                        Item("Archive", HeroIcons::archive_box),
                        Item("Move", HeroIcons::arrow_top_right_on_square, disabled = true),
                    ),
                    listOf(
                        Item("Delete", HeroIcons::trash),
                    ),
                )
            )

            showcase("Auto", resizable = false) {
                div("p-4 text-center") { navItem(navItem, selection) }
            }
            showcase("Left", resizable = false) {
                div("p-4 text-left") { navItem(navItem, selection, placement = Placement.bottomStart) }
            }
            showcase("Center", resizable = false) {
                div("p-4 text-center") { navItem(navItem, selection, placement = Placement.bottom) }
            }
            showcase("Right", resizable = false) {
                div("p-4 text-right") { navItem(navItem, selection, placement = Placement.bottomEnd) }
            }
        }

        hr { }

        showcases("Navbar", HeadlessUiIcons.tabs) {

            val selection = object : RootStore<Item?>(null) {
                val logger = ConsoleLogger("Menu")
                val log: Handler<Item> = handle { _, item ->
                    item.also { logger.debug("selected", item) }
                }
            }

            val navItems = listOf(
                Item(
                    "Foo", HeroIcons::document_text,
                    listOf(
                        Item("Edit", HeroIcons::pencil),
                        Item("Duplicate", HeroIcons::document_duplicate),
                    ),
                ),
                Item(
                    "Bar", HeroIcons::academic_cap,
                    listOf(
                        Item("Archive", HeroIcons::archive_box),
                        Item("Move", HeroIcons::arrow_top_right_on_square, disabled = true),
                    ),
                    listOf(
                        Item("Delete", HeroIcons::trash),
                    ),
                ),
                Item("Baz", HeroIcons::document_magnifying_glass, disabled = true),
            )

            showcase("xx-large", resizable = false, classes = "col-span-full w-full max-w-screen-2xl") {
                navigationBar(navItems.map { ItemNavItem(it) }, selection)
            }
            showcase("small", resizable = false, classes = "col-span-full w-full max-w-screen-sm") {
                navigationBar(navItems.map { ItemNavItem(it) }, selection)
            }
        }
    },
)
