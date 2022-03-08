package com.bkahlert.hello.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.bkahlert.Brand
import com.bkahlert.RGB
import com.bkahlert.hello.MagnifyingGlass
import com.bkahlert.hello.center
import com.bkahlert.hello.fontFamily
import com.bkahlert.hello.visuallyHidden
import com.bkahlert.kommons.backgroundImage
import com.bkahlert.kommons.browser.openInNewTab
import com.bkahlert.kommons.browser.openInSameTab
import com.bkahlert.kommons.runtime.id
import com.bkahlert.kommons.web.dom.Toggle
import com.bkahlert.kommons.web.http.toUrlOrNull
import io.ktor.http.Url
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
import org.jetbrains.compose.web.css.FlexDirection
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.StyleScope
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.backgroundImage
import org.jetbrains.compose.web.css.backgroundPosition
import org.jetbrains.compose.web.css.backgroundRepeat
import org.jetbrains.compose.web.css.backgroundSize
import org.jetbrains.compose.web.css.border
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.flex
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.marginLeft
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
    engine: Engine,
    query: String? = null,
    fullSearch: Boolean = false,
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    onEngineChange: (Engine) -> Unit = {},
    onFullSearchChange: (Boolean) -> Unit = {},
    onFocusChange: (Boolean) -> Unit = {},
    onSearch: (String, List<Url>) -> Unit = { _, urls ->
        if (urls.size == 1) window.openInSameTab(urls.first())
        else urls.forEach(window::openInNewTab)
    },
    onPaste: (String) -> Unit = { value ->
        value.toUrlOrNull()
            ?.let { window.openInSameTab(it) }
    },
) {
    val color = RGB("#5f6368")

    val spacerInputId = id("spacer-input")

    val inputState = remember { mutableStateOf(query ?: "") }

    val engineState = remember { mutableStateOf(engine) }

    @Suppress("NAME_SHADOWING")
    val onEngineChange: (Engine) -> Unit = {
        engineState.value = it
        onEngineChange(it)
    }

    val fullSearchState = remember { mutableStateOf(fullSearch) }

    @Suppress("NAME_SHADOWING")
    val onFullSearchChange: (Boolean) -> Unit = {
        fullSearchState.value = it
        onFullSearchChange(it)
    }

    val focusState = remember { mutableStateOf(false) }

    @Suppress("NAME_SHADOWING")
    val onFocusChange: (Boolean) -> Unit = {
        focusState.value = it
        onFocusChange(it)
    }

    @Suppress("NAME_SHADOWING")
    val onSearch: () -> Unit = {
        onSearch(inputState.value, Engine.values()
            .filter { fullSearchState.value || it == engineState.value }
            .map { it.url(inputState.value) })
    }

    val (backgroundPosition, backgroundPositionChanged) = remember { mutableStateOf("0px") }

    val isEmpty = inputState.value.isEmpty()

    Div({
        style {
            center(FlexDirection.Row)
            property("margin", "0 auto")
            width(638.px)
            property("width", "auto")
            maxWidth(584.px)
            padding(0.em, 2.em)
        }
    }) {
        Div({
            style {
                flex(1, 1)
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
            attrs?.invoke(this)
        }) {
            Div({
                style {
                    property("transition",
                        "color .2s ease-in, background-color .2s ease-in, background-position .2s ease-in, background-size .2s ease-in")
                    borderRadius2()
                    backgroundRepeat("no-repeat")
                    if (fullSearchState.value) {
                        if (focusState.value) {
                            color(Color.black)
                            backgroundColor(Brand.colors.input.transparentize(0.33))
                            backgroundImage(engineState.value.coloredImage)
                        } else {
                            color(Color.white)
                            backgroundColor(Brand.colors.input.transparentize(0.0))
                            backgroundImage(engineState.value.grayscaleImage)
                        }
                        backgroundPosition("-15em ${if (isEmpty) "45%" else "50%"}")
                    } else {
                        if (focusState.value) {
                            color(Color.black)
                            backgroundColor(Brand.colors.input)
                            backgroundImage(engineState.value.coloredImage)
                        } else {
                            color(engineState.value.color.textColor)
                            backgroundColor(engineState.value.color)
                            if (isEmpty) backgroundImage(engineState.value.grayscaleImage)
                        }
                        if (isEmpty) {
                            backgroundPosition("$backgroundPosition 45%")
                        } else {
                            backgroundPosition("$backgroundPosition 50%")
                        }
                    }
                    backgroundSize("100% 45%")

                    display(DisplayStyle.Flex)
                    property("box-shadow", "none")
                    borderRadius(24.px)
                    property("margin", "0 auto")
                    width(100.percent)
                }
                attrs?.invoke(this)
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
                                if (focusState.value) backgroundImage(MagnifyingGlass(color))
                                else backgroundImage(MagnifyingGlass(engineState.value.color.textColor))
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
                        onKeyDown { event ->
                            when (event.code) {
                                "ArrowUp" -> onEngineChange(engineState.value.prev)
                                "ArrowDown" -> onEngineChange(engineState.value.next)
                                "OSLeft", "OSRight" -> onFullSearchChange(true)
                                "Enter", "NumpadEnter" -> onSearch()
                            }
                        }
                        onKeyUp {
                            when (it.code) {
                                "OSLeft", "OSRight" -> onFullSearchChange(false)
                            }
                        }
                        onInput { event -> inputState.value = event.value }
                        onPaste { event -> event.getData("text/plain")?.also(onPaste) }
                        style {
                            if (!focusState.value) color(engineState.value.color.textColor)
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
                            property("appearance", "none")
                        }
                        onFocus {
                            onFocusChange(true)
                        }
                        onBlur {
                            onFullSearchChange(false)
                            onFocusChange(false)
                        }
                    }

                    Div({ style { visuallyHidden() } }) {
                        Span({
                            id(spacerInputId)
                            style {
                                fontFamily(Brand.fonts)
                            }
                        })
                    }

                    document.getElementById(spacerInputId)?.unsafeCast<HTMLElement>()?.apply {
                        textContent = inputState.value
                        if (isEmpty && !focusState.value) {
                            backgroundPositionChanged("${offsetWidth + 40}px")
                        } else {
                            backgroundPositionChanged("${offsetWidth + 65}px")
                        }
                    }
                }
            }
        }

        Toggle(
            label = "1",
            checkedLabel = Engine.values().size.toString()
        ) {
            style {
                property("flex", "0 auto")
                marginLeft(1.cssRem)
            }
            title("If checked all search engines are opened in separate tabs")
            checked(fullSearchState.value)
            onChange { event -> onFullSearchChange(event.value) }
        }
    }
}

fun StyleScope.borderRadius2(): Unit {
    borderRadius(20.px)
}
