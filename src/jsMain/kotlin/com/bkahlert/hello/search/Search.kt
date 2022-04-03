package com.bkahlert.hello.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.Brand
import com.bkahlert.hello.ui.MagnifyingGlass
import com.bkahlert.hello.ui.center
import com.bkahlert.hello.ui.fontFamily
import com.bkahlert.hello.ui.visuallyHidden
import com.bkahlert.kommons.Color.RGB
import com.bkahlert.kommons.backgroundImage
import com.bkahlert.kommons.dom.openInNewTab
import com.bkahlert.kommons.dom.openInSameTab
import com.bkahlert.kommons.text.randomString
import com.bkahlert.kommons.time.seconds
import com.bkahlert.kommons.web.dom.Toggle
import com.bkahlert.kommons.web.http.toUrlOrNull
import com.semanticui.compose.element.Icon
import com.semanticui.compose.module.Checkbox
import com.semanticui.compose.module.CheckboxElementType.Toggle
import io.ktor.http.Url
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.attributes.AutoComplete
import org.jetbrains.compose.web.attributes.InputType.Checkbox
import org.jetbrains.compose.web.attributes.InputType.Text
import org.jetbrains.compose.web.attributes.autoComplete
import org.jetbrains.compose.web.attributes.autoFocus
import org.jetbrains.compose.web.attributes.maxLength
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.DisplayStyle.Companion.Flex
import org.jetbrains.compose.web.css.FlexDirection
import org.jetbrains.compose.web.css.JustifyContent
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
import org.jetbrains.compose.web.css.justifyContent
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
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.SearchInput
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

@Composable
fun SearchThing(
    searchEngine: SearchEngine,
    onEngineChange: (SearchEngine) -> Unit = {},
    allAtOnce: Boolean = false,
    onAllAtOnceChange: (Boolean) -> Unit = {},
    query: String? = null,
    onQueryChange: (String) -> Unit = {},
    onSearch: (String, List<Url>) -> Unit = { _, urls ->
        if (urls.size == 1) window.openInSameTab(urls.first())
        else urls.forEach(window::openInNewTab)
    },
    onPaste: (String) -> Unit = { value ->
        value.toUrlOrNull()
            ?.let { window.openInSameTab(it) }
    },
    onReady: () -> Unit = {},
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
) {
    val id = randomString()

    Div({
        classes("ui", "search")
        style {
            display(Flex)
            alignItems(AlignItems.Center)
            justifyContent(JustifyContent.Center)
        }
    }) {
        Div({
            classes("ui", "icon", "input")
            style { flex("1") }
        }) {
            Input(Text) {
                id(id)
                classes("prompt")
                placeholder("Search...")
            }
            Icon("search")
        }
        Label(id) { Text(searchEngine.name) }
        Checkbox(Toggle, { style { marginLeft(1.em) } }) {
            Input(Checkbox) {
                name("all-at-once")
                checked(allAtOnce)
                onChange { onAllAtOnceChange(it.value) }
            }
            Label {
                Text("all at once")
            }
        }
    }

    val color = RGB("#5f6368")

    val spacerInput = "spacer-input"

    var inputState by remember { mutableStateOf(query ?: "") }

    var engineState by remember { mutableStateOf(searchEngine) }

    @Suppress("NAME_SHADOWING")
    val onEngineChange: (SearchEngine) -> Unit = {
        engineState = it
        onEngineChange(it)
    }

    var fullSearchState by remember { mutableStateOf(allAtOnce) }

    @Suppress("NAME_SHADOWING")
    val onFullSearchChange: (Boolean) -> Unit = {
        fullSearchState = it
        onAllAtOnceChange(it)
    }

    var focusState by remember { mutableStateOf(false) }

    @Suppress("NAME_SHADOWING")
    val onFocusChange: (Boolean) -> Unit = { hasFocus ->
        focusState = hasFocus
        if (hasFocus) {
            window.setTimeout({ onReady() }, 1.seconds.inWholeMilliseconds.toInt())
        }
    }

    @Suppress("NAME_SHADOWING")
    val onSearch: () -> Unit = {
        onSearch(inputState, SearchEngine.values()
            .filter { fullSearchState || it == engineState }
            .map { it.url(inputState) })
    }

    val (backgroundPosition, backgroundPositionChanged) = remember { mutableStateOf(65.px) }

    val isEmpty = inputState.isEmpty()

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

                display(Flex)
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
                    if (fullSearchState) {
                        if (focusState) {
                            color(Color.black)
                            backgroundColor(Brand.colors.input.transparentize(0.33))
                            backgroundImage(engineState.coloredImage)
                        } else {
                            color(Color.white)
                            backgroundColor(Brand.colors.input.transparentize(0.0))
                            backgroundImage(engineState.grayscaleImage)
                        }
                        backgroundPosition("-15em ${if (isEmpty) "45%" else "50%"}")
                    } else {
                        if (focusState) {
                            color(Color.black)
                            backgroundColor(Brand.colors.input)
                            backgroundImage(engineState.coloredImage)
                        } else {
                            color(engineState.color.textColor)
                            backgroundColor(engineState.color)
                            if (isEmpty) backgroundImage(engineState.grayscaleImage)
                        }
                        if (isEmpty) {
                            backgroundPosition("$backgroundPosition 45%")
                        } else {
                            backgroundPosition("$backgroundPosition 50%")
                        }
                    }
                    backgroundSize("100% 45%")

                    display(Flex)
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
                        display(Flex)
                        padding(0.px, 8.px, 0.px, 14.px)
                    }
                }) {
                    Div({
                        style {
                            display(Flex)
                            alignItems(AlignItems.Center)
                            paddingRight(7.px)
                            marginTop(1.px)
                        }
                    }) {
                        Em({
                            style {
                                property("margin", "auto")
                                if (focusState) backgroundImage(MagnifyingGlass(color))
                                else backgroundImage(MagnifyingGlass(engineState.color.textColor))
                                width(20.px)
                                height(20.px)
                            }
                        })
                    }
                    // TODO redesign using https://semantic-ui.com/elements/input.html#action
                    SearchInput(inputState) {
                        attr("data-engine", engineState.name)
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
                                "ArrowUp" -> onEngineChange(engineState.prev)
                                "ArrowDown" -> onEngineChange(engineState.next)
                                "OSLeft", "OSRight" -> onFullSearchChange(true)
                                "Enter", "NumpadEnter" -> onSearch()
                            }
                        }
                        onKeyUp {
                            when (it.code) {
                                "OSLeft", "OSRight" -> onFullSearchChange(false)
                            }
                        }
                        onInput { event ->
                            inputState = event.value

                            document.getElementById(spacerInput)?.unsafeCast<HTMLElement>()?.apply {
                                textContent = event.value
                                backgroundPositionChanged((offsetWidth + 65).px)
                            }
                        }
                        onPaste { event -> event.getData("text/plain")?.also(onPaste) }
                        style {
                            if (!focusState) color(engineState.color.textColor)
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
                            id(spacerInput)
                            style {
                                fontFamily(Brand.fonts)
                            }
                        })
                    }
                }
            }
        }

        Toggle(
            label = "1",
            checkedLabel = SearchEngine.values().size.toString()
        ) {
            style {
                property("flex", "0 auto")
                marginLeft(1.cssRem)
            }
            title("If checked all search engines are opened in separate tabs")
            checked(fullSearchState)
            onChange { event -> onFullSearchChange(event.value) }
        }
    }
}

fun StyleScope.borderRadius2(): Unit {
    borderRadius(20.px)
}
