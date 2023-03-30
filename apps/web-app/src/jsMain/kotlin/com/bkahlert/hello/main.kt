package com.bkahlert.hello

import com.bkahlert.hello.applets.QuickLinks.Companion.quickLinks
import com.bkahlert.hello.applets.clickup
import com.bkahlert.hello.fritz2.app.AppState
import com.bkahlert.hello.fritz2.app.AppStore
import com.bkahlert.hello.fritz2.app.env.environmentView
import com.bkahlert.hello.fritz2.app.environment
import com.bkahlert.hello.fritz2.app.props
import com.bkahlert.hello.fritz2.app.props.propsView
import com.bkahlert.hello.fritz2.app.session
import com.bkahlert.hello.fritz2.app.session.sessionView
import com.bkahlert.hello.fritz2.app.user.userDropdown
import com.bkahlert.hello.fritz2.components.assets.Images
import com.bkahlert.hello.fritz2.components.diagnostics
import com.bkahlert.hello.fritz2.components.loader
import com.bkahlert.hello.fritz2.components.navigationbar.NavItem
import com.bkahlert.hello.fritz2.components.navigationbar.asNavItem
import com.bkahlert.hello.fritz2.components.navigationbar.navigationBar
import com.bkahlert.hello.fritz2.syncState
import dev.fritz2.core.Store
import dev.fritz2.core.alt
import dev.fritz2.core.render
import dev.fritz2.core.src
import dev.fritz2.core.storeOf
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@JsModule("./styles/web-app.scss")
private external val AppStyles: dynamic

val prototype = false;// = window.location.hostname == "localhost"

fun main() {
    AppStyles

    val appStore = AppStore(
//        sessionResolver = { { FakeSession.Unauthorized() } },
    )

    render("#root") {
        val diagnosticsOpen: Store<Boolean> = storeOf(false)
        diagnostics(diagnosticsOpen) {
            div("relative z-10 flex justify-end -mt-6") {
                appStore.session.render { if (it != null) userDropdown("-translate-y-8", it) else div("-translate-y-6") { loader() } }
            }
            appStore.props.render { if (it != null) propsView(it) }
            hr {}
            appStore.session.render { if (it != null) sessionView(it) }
            hr {}
            appStore.environment.render { if (it != null) environmentView(it) }
        }

        navigationBar(
            "sticky top-0 inset-x-0 z-[20]",
            selection = storeOf<NavItem?>(null),
            startContent = {
                div("flex flex-shrink-0 items-center") {
                    img("shrink-0 h-8 w-auto") {
                        src(Images.HelloFavicon.toString())
                        alt("Hello!")
                        clicks.map { !diagnosticsOpen.current } handledBy diagnosticsOpen.update
                    }
                }
            },
            navItems = listOf(
                appStore.props.map { it?.quickLinks() }.asNavItem(),
                appStore.props.map { it?.clickup() }.asNavItem(),
            ),
        ) {
            appStore.session.combine(appStore.props) { a, b -> a to b }.render { (s, p) ->
                if (s != null) userDropdown(
                    sessionStore = s,
                    customize = { syncState(p?.syncState ?: flowOf(null)) },
                    onDiagnostics = { diagnosticsOpen.update(true) }
                )
            }
        }

        appStore.data.render { state ->
            when (state) {
                is AppState.Loading -> app()
                is AppState.Unauthorized -> app(state.session, state.props)
                is AppState.Authorized -> app(state.session, state.user, state.props)
            }
        }
    }
}
