package com.bkahlert.hello.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.bkahlert.Brand
import com.bkahlert.RGB
import com.bkahlert.hello.center
import com.bkahlert.hello.visuallyHidden
import com.bkahlert.kommons.SVGImage
import com.bkahlert.kommons.backgroundImage
import com.bkahlert.kommons.runtime.id
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.attributes.AutoComplete
import org.jetbrains.compose.web.attributes.autoComplete
import org.jetbrains.compose.web.attributes.autoFocus
import org.jetbrains.compose.web.attributes.maxLength
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.StyleBuilder
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.backgroundImage
import org.jetbrains.compose.web.css.backgroundPosition
import org.jetbrains.compose.web.css.backgroundRepeat
import org.jetbrains.compose.web.css.backgroundSize
import org.jetbrains.compose.web.css.border
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.flex
import org.jetbrains.compose.web.css.fontFamily
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.marginTop
import org.jetbrains.compose.web.css.maxWidth
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.paddingRight
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Em
import org.jetbrains.compose.web.dom.SearchInput
import org.jetbrains.compose.web.dom.Span
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

@Composable
fun Search(
    initialEngine: Engine,
    initialValue: String? = null,
    initialAllEngines: Boolean = false,
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
) {
    val color = RGB("#5f6368")

    val spacerInputId = id("spacer-input")

    val inputState = remember { mutableStateOf(initialValue ?: "") }
    val engineState = remember { mutableStateOf(initialEngine) }

    val (allEngines, allEnginesChanged) = remember { mutableStateOf(initialAllEngines) }
    val (backgroundPosition, backgroundPositionChanged) = remember { mutableStateOf("0px") }
    val (isFocused, focusChanged) = remember { mutableStateOf(false) }

    val isEmpty = inputState.value.isEmpty()

    Div({
        style {
            center()
            property("margin", "0 auto")
            width(638.px)
            property("width", "auto")
            maxWidth(584.px)
            padding(2.em)
        }
    }) {
        Div({
            style {
                borderRadius2()
                backgroundSize("cover")
                backgroundColor(Color.transparent)
                backgroundImage("url(rainbow-gradient.svg)")

                display(DisplayStyle.Flex)
                height(44.px)
                border(1.px, LineStyle.Solid, color)
                property("box-shadow", "none")
                borderRadius(24.px)
            }
            attrs?.also { apply(it) }
        }) {
            Div({
                style {
                    property("transition",
                        "color .2s ease-in, background-color .2s ease-in, background-position .2s ease-in, background-size .2s ease-in")
                    borderRadius2()
                    backgroundRepeat("no-repeat")
                    if (allEngines) {
                        if (isFocused) {
                            color(Color.black)
                            backgroundColor(Brand.colors.white.transparentize(0.33))
                            backgroundImage(engineState.value.coloredImage)
                        } else {
                            color(Color.white)
                            backgroundColor(Brand.colors.white.transparentize(0.0))
                            backgroundImage(engineState.value.greyscaleImage)
                        }
                        if (isEmpty) {
                            backgroundPosition("-15em 45%")
                        } else {
                            backgroundPosition("-15em 50%")
                        }
                    } else {
                        if (isFocused) {
                            color(Color.black)
                            backgroundColor(Brand.colors.white)
                            backgroundImage(engineState.value.coloredImage)
                        } else {
                            color(engineState.value.color.textColor)
                            backgroundColor(engineState.value.color)
                            backgroundImage(engineState.value.greyscaleImage)
                        }
                        if (isEmpty) {
                            backgroundPosition("$backgroundPosition 45%")
                        } else {
                            backgroundPosition("$backgroundPosition 50%")
                        }
                    }
                    if (isEmpty) {
                        backgroundSize("100% 67%")
                    } else {
                        backgroundSize("100% 50%")
                    }

                    display(DisplayStyle.Flex)
                    property("box-shadow", "none")
                    borderRadius(24.px)
                    property("margin", "0 auto")
                    width(100.percent)
                }
                attrs?.also { apply(it) }
            }) {
                Div({
                    style {
                        flex("1")
                        display(DisplayStyle.Flex)
                        padding(0.px, 8.px, 0.px, 14.px)
                    }
                }) {
                    Div({
                        style {
                            display(DisplayStyle.Flex)
                            alignItems(AlignItems.Center)
                            paddingRight(7.px)
                            marginTop(1.px)
                        }
                    }) {
                        Em({
                            style {
                                property("margin", "auto")
                                backgroundImage(SVGImage("""
                                <svg focusable="false" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="$color"><path d="M15.5 14h-.79l-.28-.27A6.471 6.471 0 0 0 16 9.5 6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"></path></svg>
                            """.trimIndent()))
                                width(20.px)
                                height(20.px)
                            }
                        })
                    }

                    SearchInput(inputState.value) {
                        attr("data-engine", engineState.value.name)
                        attr("autocapitalize", "off")
                        autoComplete(AutoComplete.off)
                        attr("autocorrect", "off")
                        placeholder("")
                        autoFocus()
                        maxLength(2048)
                        name("query")
                        spellCheck(false)
                        tabIndex(1)
                        title("Press ↑ or ↓ to switch the search engine")
                        onKeyDown {
                            when (it.code) {
                                "ArrowUp" -> engineState.value = engineState.value.prev
                                "ArrowDown" -> engineState.value = engineState.value.next
                                "OSLeft", "OSRight" -> allEnginesChanged(true)
                                "Enter", "NumpadEnter" -> {
                                    println("Search for ${inputState.value} with ${if (allEngines) "all" else engineState.value.name}")
                                    println(engineState.value.url(inputState.value))
                                    if (allEngines) {
                                        Engine.values().forEach {
                                            window.open(it.url(inputState.value).toString(), "_blank")
                                        }
                                    } else {
                                        kotlin.runCatching {
                                            window.open(engineState.value.url(inputState.value).toString(), "_top")
                                        }.onFailure {
                                            window.open(engineState.value.url(inputState.value).toString(), "_blank")
                                        }
                                        engineState.value
                                    }
                                }
                                else -> println(it.code)
                            }
                        }
                        onKeyUp {
                            when (it.code) {
                                "OSLeft", "OSRight" -> allEnginesChanged(false)
                            }
                        }
                        onInput { event -> inputState.value = event.value }
                        style {
                            backgroundColor(Color.transparent)
                            property("border", "none")
                            property("margin", "0")
                            property("padding", "0")
                            fontSize(16.px)
                            property("word-wrap", "break-word")
                            property("outline", "none")
                            property("display", "flex")
                            property("flex", "100%")
                            property("tap-highlight-color", "transparent")
                        }
                        onFocus {
                            focusChanged(true)
                        }
                        onBlur {
                            focusChanged(false)
                        }
                    }

                    Div({ style { visuallyHidden() } }) {
                        Span({
                            id(spacerInputId)
                            style {
                                fontFamily("system-uix",
                                    "-apple-system",
                                    "Segoe UI",
                                    "Roboto",
                                    "Helvetica",
                                    "Arial",
                                    "sans-serif",
                                    "Apple Color Emoji",
                                    "Segoe UI Emoji")
                            }
                        })
                    }

                    document.getElementById(spacerInputId)?.unsafeCast<HTMLElement>()?.apply {
                        textContent = inputState.value
                        if (isEmpty && !isFocused) {
                            backgroundPositionChanged("${offsetWidth + 40}px")
                        } else {
                            backgroundPositionChanged("${offsetWidth + 65}px")
                        }
                    }
                }
            }
        }
    }
}

fun StyleBuilder.borderRadius2(): Unit {
    borderRadius(20.px)
}
