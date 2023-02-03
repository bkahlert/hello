package com.bkahlert.semanticui.devmode

import com.bkahlert.kommons.devmode.DevMode
import com.bkahlert.kommons.dom.LocationFragmentParameters
import com.bkahlert.kommons.dom.appendDivElement
import com.bkahlert.kommons.dom.body
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.demo.DemoContentBuilder
import com.bkahlert.semanticui.demo.DemoProvider
import com.bkahlert.semanticui.demo.DemoView
import com.bkahlert.semanticui.demo.asDemoViewState
import com.bkahlert.semanticui.module.Content
import com.bkahlert.semanticui.module.Modal
import com.bkahlert.semanticui.module.ModalContentElement
import com.bkahlert.semanticui.module.autofocus
import com.bkahlert.semanticui.module.blurring
import com.bkahlert.semanticui.module.centered
import com.bkahlert.semanticui.module.fullScreen
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.dom.addClass
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.backgroundColor
import org.w3c.dom.events.EventTarget

/**
 * Variant of [DevMode] that
 * creates [ComposeDevSession] instances
 * with a [Modal] and the specified [content].
 */
public fun ModalDevMode(
    name: String = "debug",
    keyboardEventTarget: EventTarget? = document.body(),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    scope: CoroutineScope = CoroutineScope(dispatcher),
    content: SemanticContentBuilder<ModalContentElement>? = null,
): DevMode<ComposeDevSession> = DevMode(
    name = name,
    keyboardEventTarget = keyboardEventTarget,
    dispatcher = dispatcher,
    scope = scope,
) {
    ComposeDevSession(
        root = document.body().appendDivElement { addClass("dev-session") },
        content = {
            Modal({
                style { backgroundColor(Color.transparent) }
                v.fullScreen()
                b.blurring = false // true would blur popups inside the debug mode, too
                b.autofocus = false
                b.centered = false
            }) {
                Content(
                    attrs = {
                        style { backgroundColor(Color.transparent) }
                    },
                    content = content
                )
            }
        },
    )
}

/**
 * Variant of [ModalDevMode] that
 * renders a [DemoView] with all specified [providers].
 */
public fun DemoDevMode(
    vararg providers: DemoProvider,
    name: String = "demo",
    keyboardEventTarget: EventTarget? = document.body(),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    scope: CoroutineScope = CoroutineScope(dispatcher),
    trashContent: DemoContentBuilder? = null,
): DevMode<ComposeDevSession> = ModalDevMode(
    keyboardEventTarget = keyboardEventTarget,
    dispatcher = dispatcher,
    scope = scope,
) {
    DemoView(
        providers = providers,
        state = LocationFragmentParameters(window).asDemoViewState(name),
        trashContent = trashContent,
    )
}
