package com.bkahlert.semanticui.devmode

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.bkahlert.kommons.binding.Binding
import com.bkahlert.kommons.binding.DelegatingDevMode
import com.bkahlert.kommons.binding.DevMode
import com.bkahlert.kommons.binding.DevSession
import com.bkahlert.kommons.binding.adapt
import com.bkahlert.kommons.dom.body
import com.bkahlert.kommons.dom.createChildDivElement
import com.bkahlert.semanticui.collection.Item
import com.bkahlert.semanticui.collection.Menu
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.custom.zIndex
import com.bkahlert.semanticui.demo.DemoContentBuilder
import com.bkahlert.semanticui.demo.DemoProvider
import com.bkahlert.semanticui.demo.DemoView
import com.bkahlert.semanticui.demo.DemoViewState
import com.bkahlert.semanticui.element.Button
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.module.Content
import com.bkahlert.semanticui.module.Modal
import com.bkahlert.semanticui.module.ModalContentElement
import com.bkahlert.semanticui.module.autofocus
import com.bkahlert.semanticui.module.blurring
import com.bkahlert.semanticui.module.centered
import com.bkahlert.semanticui.module.fullScreen
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.dom.addClass
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.bottom
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.left
import org.jetbrains.compose.web.css.position
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.EventTarget
import org.w3c.dom.events.KeyboardEvent

/**
 * Sets up a new [DevMode] instance that
 * delegates the session creation to [createSession], and
 * it's activation to the presence of the specified [fragmentParameterName] (default: `debug`, e.g. `#debug`).
 *
 * The optional [keyboardEventTarget] (default: `document.body`) is used to bind
 * the `F4` to toggle [DevMode.state], and
 * the `ESC` to [DevMode.stop].
 */
public fun <T : DevSession> setupDevMode(
    fragmentParameterName: String = "debug",
    keyboardEventTarget: EventTarget? = document.body(),
    createSession: () -> T,
): Binding<Boolean> {
    val devMode: DevMode<T> = DelegatingDevMode(createSession)

    val binding = window.bindFragmentParameter(fragmentParameterName).adapt(
        from = { it != null },
        to = { if (it) emptyList() else null }
    )

    fun applyValue(value: Boolean) {
        if (value) devMode.start() else devMode.stop()
    }

    with(binding) {
        applyValue(value)
        addValueChangeListener { _, newValue -> applyValue(newValue) }
        if (keyboardEventTarget != null) bindKeyboardEvents(keyboardEventTarget, startValue = true, stopValue = false)
        bindMenu(document.body().createChildDivElement { addClass("dev-mode-controls") }, startValue = true, stopValue = false)
    }

    return binding
}

/**
 * Binds
 * the `F4` to toggle [DevMode.state], and
 * the `ESC` to [DevMode.stop].
 */
private fun <T> Binding<T>.bindKeyboardEvents(
    eventTarget: EventTarget,
    startValue: T,
    stopValue: T,
) {
    eventTarget.addEventListener(
        type = "keydown",
        callback = { event ->
            if (event.target == eventTarget) {
                when ((event as KeyboardEvent).key.uppercase()) {
                    "F4" -> value = when (value == stopValue) {
                        true -> startValue
                        else -> stopValue
                    }

                    "ESCAPE" -> value = stopValue
                    else -> {}
                }
            }
        },
    )
}

/**
 * Creates a hover menu
 * that can be used to toggle [DevMode.state].
 */
private fun <T> Binding<T>.bindMenu(
    root: HTMLDivElement,
    startValue: T,
    stopValue: T,
) {
    renderComposable(root) {
        var active by asMutableState()
        Menu({
            classes("compact", "tiny", "secondary")
            style {
                position(Position.Fixed)
                bottom(1.em)
                left(0.5.em)
                zIndex(2000)
            }
        }) {
            Item {
                Button({
                    classes("animated", "fade", "teal", "basic")
                    tabIndex(0)
                    onClick {
                        active = when (active == stopValue) {
                            true -> startValue
                            else -> stopValue
                        }
                    }
                }) {
                    if (active == stopValue) {
                        S("visible", "content") { Icon("wrench"); Text("F4") }
                        S("hidden", "content") { Text("Debug") }
                    } else {
                        S("visible", "content") { Icon("close") }
                        S("hidden", "content") { Text("Close") }
                    }
                }
            }
        }
    }
}


/**
 * Variant of [setupDevMode] that
 * creates [ComposeDevSession] instances
 * with a [Modal] and the specified [content].
 */
public fun setupModalDevMode(
    fragmentParameterName: String = "debug",
    keyboardEventTarget: EventTarget? = document.body(),
    content: SemanticContentBuilder<ModalContentElement>? = null,
): Binding<Boolean> = setupDevMode(
    fragmentParameterName = fragmentParameterName,
    keyboardEventTarget = keyboardEventTarget,
) {
    ComposeDevSession(
        root = document.body().createChildDivElement(),
        content = {
            Modal({
                style { backgroundColor(Color.transparent) }
                v.fullScreen()
                b.blurring = false // true would blur popups inside the debug mode, too
                b.autofocus = false
                b.centered = false
            }) {
                Content({ style { backgroundColor(Color.transparent) } }, content = content)
            }
        },
    )
}

/**
 * Variant of [setupModalDevMode] that
 * renders a [DemoView] with all specified [providers].
 */
public fun setupDemoDevMode(
    vararg providers: DemoProvider,
    fragmentParameterName: String = "demo",
    keyboardEventTarget: EventTarget? = document.body(),
    trashContent: DemoContentBuilder? = null,
): Binding<Boolean> = setupModalDevMode(
    keyboardEventTarget = keyboardEventTarget,
) {
    val activeDemoBinding = window.bindFragmentParameter(fragmentParameterName).adapt(
        from = { it?.firstOrNull() },
        to = { listOfNotNull(it) },
    )
    DemoView(
        providers = providers,
        state = BoundDemoViewState(activeDemoBinding),
        trashContent = trashContent,
    )
}


@Composable
public fun BoundDemoViewState(
    binding: Binding<String?>,
): DemoViewState = object : DemoViewState {
    override var active: String? by binding.asMutableState()
    override fun onActivate(id: String) {
        active = id
    }
}
