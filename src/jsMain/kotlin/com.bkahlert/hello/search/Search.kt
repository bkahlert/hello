package com.bkahlert.hello.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.bkahlert.Brand
import com.bkahlert.hello.center
import com.bkahlert.hello.visuallyHidden
import com.bkahlert.kommons.backgroundImage
import com.bkahlert.kommons.text.randomString
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.attributes.AutoComplete
import org.jetbrains.compose.web.attributes.autoComplete
import org.jetbrains.compose.web.attributes.autoFocus
import org.jetbrains.compose.web.attributes.maxLength
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.StyleBuilder
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.backgroundImage
import org.jetbrains.compose.web.css.backgroundPosition
import org.jetbrains.compose.web.css.backgroundRepeat
import org.jetbrains.compose.web.css.backgroundSize
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.fontFamily
import org.jetbrains.compose.web.css.paddingLeft
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.SearchInput
import org.jetbrains.compose.web.dom.Span
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

@Composable
fun Search(
    initialEngine: Engine,
    initialValue: String? = null,
    allEngines: Boolean = false,
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
) {
    val spacerInputIdState = remember { mutableStateOf("spacer-input--${randomString()}") }

    val inputState = remember { mutableStateOf(initialValue ?: "") }
    val engineState = remember { mutableStateOf(initialEngine) }

    val (allEngines, allEnginesChanged) = remember { mutableStateOf(allEngines) }
    val (backgroundPosition, backgroundPositionChanged) = remember { mutableStateOf("0px") }
    val (isFocused, focusChanged) = remember { mutableStateOf(false) }

    val isEmpty = inputState.value.isEmpty()

    Div({
        style {
            center()
        }
    }) {
        Div({
            style {
                borderRadius2()
                backgroundSize("cover")
                backgroundColor(Color.transparent)
                backgroundImage("url(rainbow-gradient.svg)")
            }
            attrs?.also { apply(it) }
        }) {
            SearchInput(inputState.value) {
                attr("data-engine", engineState.value.name)
                attr("autocapitalize", "off")
                autoComplete(AutoComplete.off)
                attr("autocorrect", "off")
                placeholder("")
                autoFocus()
                maxLength(1000)
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
                    property("transition", "color .2s ease-in, background-color .2s ease-in, background-position .2s ease-in, background-size .2s ease-in")
                    width(100.percent)
                    paddingLeft(.5.em)
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
                            backgroundPosition("-15em 0%")
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
                            backgroundPosition("$backgroundPosition 0%")
                        } else {
                            backgroundPosition("$backgroundPosition 50%")
                        }
                    }
                    if (isEmpty) {
                        backgroundSize("100% 100%")
                    } else {
                        backgroundSize("100% 50%")
                    }
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
                    id(spacerInputIdState.value)
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

            document.getElementById(spacerInputIdState.value)?.unsafeCast<HTMLElement>()?.apply {
                textContent = inputState.value
                if (isEmpty && !isFocused) {
                    backgroundPositionChanged("${offsetWidth + 15}px")
                } else {
                    backgroundPositionChanged("${offsetWidth + 45}px")
                }
            }
        }
    }
}

fun StyleBuilder.borderRadius2(): Unit {
    borderRadius(20.px)
}
