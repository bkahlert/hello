import com.bkahlert.hello.app.AppStore
import com.bkahlert.hello.app.env.EnvironmentShowcases
import com.bkahlert.hello.app.env.environmentView
import com.bkahlert.hello.app.environment
import com.bkahlert.hello.app.props
import com.bkahlert.hello.app.props.PropsShowcases
import com.bkahlert.hello.app.props.propsView
import com.bkahlert.hello.app.session
import com.bkahlert.hello.app.session.FakeSession
import com.bkahlert.hello.app.session.SessionShowcases
import com.bkahlert.hello.app.session.sessionView
import com.bkahlert.hello.app.showcase.HeadlessUiShowcases
import com.bkahlert.hello.app.user.UserShowcases
import com.bkahlert.hello.app.user.userDropdown
import com.bkahlert.hello.button.button
import com.bkahlert.hello.components.PageRouter
import com.bkahlert.hello.components.ParentPage
import com.bkahlert.hello.components.SimplePage
import com.bkahlert.hello.components.diagnostics
import com.bkahlert.hello.components.loader
import com.bkahlert.hello.components.navigationbar.navigationBar
import com.bkahlert.hello.components.toNavItems
import com.bkahlert.hello.components.toaster.ConsoleToaster
import com.bkahlert.hello.components.toaster.DefaultConsoleMessageRenderer
import com.bkahlert.hello.fritz2.syncState
import com.bkahlert.hello.icon.assets.Images
import com.bkahlert.hello.icon.heroicons.SolidHeroIcons
import com.bkahlert.hello.icon.icon
import dev.fritz2.core.render
import dev.fritz2.core.storeOf
import dev.fritz2.core.transition
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import playground.PlaygroundContainer
import playground.components.app.AppShowcases
import playground.fritz2.Fritz2DemoPage
import playground.fritz2.headlessdemo.pages

@JsModule("./styles/playground-app.scss")
private external val PlaygroundStyles: dynamic

fun main() {
    PlaygroundStyles

    val appStore = AppStore(
        sessionResolver = { { FakeSession.Unauthorized() } },
    )

    val pageRouter = PageRouter(
        PlaygroundContainer,
        SimplePage(
            id = "hello",
            name = "Hello!",
            description = "Hello! app showcases",
            icon = Images.HelloFavicon,
            pages = listOf(
                HeadlessUiShowcases,
                EnvironmentShowcases,
                SessionShowcases,
                UserShowcases,
                PropsShowcases,
                AppShowcases,
            ),
        ),
        Fritz2DemoPage(pages, "page"),
    )

    render("#root") {
        ConsoleToaster(render = DefaultConsoleMessageRenderer { _ ->
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
            appStore.props.render { if (it != null) propsView(it) }
            hr {}
            appStore.session.render { if (it != null) sessionView(it) }
            hr {}
            appStore.environment.render { if (it != null) environmentView(it) }
        }

        navigationBar(
            "app-header",
            selection = pageRouter,
            navItems = pageRouter.pages.toNavItems(),
        ) {
            appStore.session.combine(appStore.props) { a, b -> a to b }.render { (s, p) ->
                if (s != null) userDropdown(
                    sessionStore = s,
                    customize = { syncState(p?.syncState ?: flowOf(null)) },
                    onDiagnostics = { diagnosticsOpen.update(true) }
                )
            }
        }

        div("pt-[--nav-height]") {
            pageRouter.data.render { selectedPage ->
                if (selectedPage != null) {
                    when (val content = selectedPage.content) {
                        null -> div("app-item") {
                            div { icon("mx-auto w-12 h-12 text-default dark:text-invert opacity-60", SolidHeroIcons.squares_plus) }
                            div("grid grid-cols-[repeat(auto-fit,_minmax(min(20rem,_100%),_1fr))] gap-8 m-8 items-start") {
                                selectedPage.let { it as? ParentPage }?.pages.orEmpty().forEach { page ->
                                    button(page.icon, page.name, page.description, simple = true, inverted = true).apply {
                                        clicks.map { page } handledBy pageRouter.navTo
                                    }
                                }
                            }
                        }

                        else -> div("container mx-auto sm:py-8") { content(this) }
                    }
                } else {
                    div("container mx-auto sm:py-8") {
                        div("grid grid-cols-[repeat(auto-fit,_minmax(min(20rem,_100%),_1fr))] gap-8 m-8 items-start") {
                            pageRouter.pages.forEach { page ->
                                button(page.icon, page.name, page.description).apply {
                                    clicks.map { page } handledBy pageRouter.navTo
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
