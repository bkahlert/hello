package com.bkahlert.hello

import com.bkahlert.hello.components.QuickLinks
import com.bkahlert.hello.components.applet.Applets
import com.bkahlert.hello.fritz2.app.AppState
import com.bkahlert.hello.fritz2.app.AppStore
import com.bkahlert.hello.fritz2.app.env.environmentView
import com.bkahlert.hello.fritz2.app.environment
import com.bkahlert.hello.fritz2.app.props
import com.bkahlert.hello.fritz2.app.props.PropStoreFactory
import com.bkahlert.hello.fritz2.app.props.propsView
import com.bkahlert.hello.fritz2.app.session
import com.bkahlert.hello.fritz2.app.session.sessionView
import com.bkahlert.hello.fritz2.app.user.userDropdown
import com.bkahlert.hello.fritz2.components.assets.Images
import com.bkahlert.hello.fritz2.components.diagnostics
import com.bkahlert.hello.fritz2.components.loader
import com.bkahlert.hello.fritz2.components.toaster.ConsoleToaster
import com.bkahlert.hello.fritz2.components.toaster.DebugConsoleMessageParser
import com.bkahlert.hello.fritz2.components.toaster.DefaultConsoleMessageRenderer
import com.bkahlert.hello.fritz2.syncState
import com.bkahlert.hello.fritz2.verticalScrollProgresses
import com.bkahlert.kommons.color.Color
import com.bkahlert.kommons.dom.verticalScrollProgress
import dev.fritz2.core.alt
import dev.fritz2.core.classes
import dev.fritz2.core.render
import dev.fritz2.core.src
import dev.fritz2.core.storeOf
import dev.fritz2.core.transition
import dev.fritz2.headless.foundation.Aria
import dev.fritz2.headless.foundation.utils.scrollintoview.*
import kotlinx.browser.document
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import org.w3c.dom.SMOOTH
import org.w3c.dom.ScrollToOptions
import org.w3c.dom.asList

@JsModule("./styles/web-app.scss")
private external val AppStyles: dynamic

val PropStoreFactories: List<PropStoreFactory<*>> = listOf(Applets, QuickLinks)

fun main() {
    AppStyles

    val appStore = AppStore()

    render("#root") {
        ConsoleToaster(
            parse = DebugConsoleMessageParser { logger ->
                logger?.removePrefix("hello:")?.split(":")?.last()?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            },
            render = DefaultConsoleMessageRenderer { _ ->
                transition(
                    "transition ease-out duration-200",
                    "opacity-0 translate-x-full",
                    "opacity-100 translate-x-0",
                )
            }).attach(this).className("top-[--nav-height] right-2 overflow-x-hidden")

        val diagnosticsOpen = storeOf(false)
        diagnostics(diagnosticsOpen) {
            div("relative z-10 flex justify-end -mt-6") {
                appStore.session.render { if (it != null) userDropdown("-translate-y-8", it) else div("-translate-y-6") { loader() } }
            }
            appStore.props.render { if (it != null) propsView(it, PropStoreFactories.map(PropStoreFactory<*>::DEFAULT_KEY)) }
            hr {}
            appStore.session.render { if (it != null) sessionView(it) }
            hr {}
            appStore.environment.render { if (it != null) environmentView(it) }
        }

        div("app-header") {
            attr("role", Aria.Role.toolbar)

            // backdrop next to content to avoid cascaded effects (that don't work in all browsers)
            div(classes("absolute inset-0", "shadow-lg dark:shadow-xl bg-glass text-default dark:text-invert")) {}

            div("mx-auto container p-8 relative flex gap-8 h-16 items-center justify-between") {
                div("flex-0 flex flex-shrink-0 items-center") {
                    img("shrink-0 h-8 w-auto") {
                        src(Images.HelloFavicon.toString())
                        alt("Hello!")
                    }
                }
                div("flex-1 flex gap-8 items-center justify-center") {
                    div("flex-0") {
                        appStore.props.map { it?.let { QuickLinks(it) } }.render { it?.render(this) }
                    }
                }
                div("flex-0") {
                    appStore.session.combine(appStore.props) { a, b -> a to b }.render { (s, p) ->
                        if (s != null) userDropdown(
                            sessionStore = s,
                            customize = { syncState(p?.syncState ?: flowOf(null)) },
                            onDiagnostics = { diagnosticsOpen.update(true) }
                        )
                    }
                }
            }
        }

        div("app-scroll-container") {

            val scrollTop = domNode.verticalScrollProgress
            verticalScrollProgresses handledBy { updateBackgroundColor(it) }
            appStore.data.render(into = this) { state ->
                when (state) {
                    is AppState.Loading -> app()
                    is AppState.Unauthorized -> app(state.session, state.props)
                    is AppState.Authorized -> app(state.session, state.user, state.props)
                }
                domNode.scrollSmoothlyTo(top = scrollTop)
            }
        }
    }
}

fun Node.scrollSmoothlyIntoView(
    mode: ScrollMode? = ScrollMode.always,
    block: ScrollPosition? = ScrollPosition.center,
    inline: ScrollPosition? = ScrollPosition.center,
) {
    scrollIntoView(
        this,
        ScrollIntoViewOptionsInit(
            behavior = ScrollBehavior.smooth,
            mode = mode,
            block = block,
            inline = inline,
        )
    )
}

fun Element.scrollSmoothlyTo(
    left: Number? = undefined,
    top: Number? = undefined,
) {
    scrollTo(
        ScrollToOptions(
            left = left?.toDouble(),
            top = top?.toDouble(),
            behavior = org.w3c.dom.ScrollBehavior.SMOOTH,
        )
    )
}


val gradient = arrayOf(
    Color(0x28abe2),
    Color(0x00b7e0),
    Color(0x00c3d9),
    Color(0x00cdce),
    Color(0x00d6c0),
    Color(0x00deae),
    Color(0x48e499),
    Color(0x78e880),
    Color(0xa1e965),
    Color(0xc7e944),
    Color(0xebe611),
)

fun updateBackgroundColor(progress: Double) {
    val index = (progress * 5.9).toInt().coerceAtMost(6)
    val color = gradient[index]
    document.getElementsByTagName("html").asList().filterIsInstance<HTMLElement>().forEach {
        it.style.backgroundColor = color.toString()
    }
}
