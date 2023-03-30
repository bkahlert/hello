import com.bkahlert.hello.clickup.model.fixtures.ImageFixtures
import com.bkahlert.hello.fritz2.app.AppState
import com.bkahlert.hello.fritz2.app.AppStore
import com.bkahlert.hello.fritz2.app.env.EnvironmentShowcases
import com.bkahlert.hello.fritz2.app.env.environmentView
import com.bkahlert.hello.fritz2.app.environment
import com.bkahlert.hello.fritz2.app.props
import com.bkahlert.hello.fritz2.app.props.PropsShowcases
import com.bkahlert.hello.fritz2.app.props.propsView
import com.bkahlert.hello.fritz2.app.session
import com.bkahlert.hello.fritz2.app.session.SessionShowcases
import com.bkahlert.hello.fritz2.app.session.sessionView
import com.bkahlert.hello.fritz2.app.user.UserShowcases
import com.bkahlert.hello.fritz2.app.user.userDropdown
import com.bkahlert.hello.fritz2.components.Page
import com.bkahlert.hello.fritz2.components.ScreensType
import com.bkahlert.hello.fritz2.components.diagnostics
import com.bkahlert.hello.fritz2.components.headlessui.HeadlessUiShowcases
import com.bkahlert.hello.fritz2.components.horizontalScreens
import com.bkahlert.hello.fritz2.components.icon
import com.bkahlert.hello.fritz2.components.loader
import com.bkahlert.hello.fritz2.components.navigationbar.navigationBar
import com.bkahlert.hello.fritz2.components.pages
import com.bkahlert.hello.fritz2.components.screens
import com.bkahlert.hello.fritz2.components.verticalScreens
import com.bkahlert.kommons.dom.fragmentParameters
import com.bkahlert.kommons.uri.build
import dev.fritz2.core.RootStore
import dev.fritz2.core.Store
import dev.fritz2.core.classes
import dev.fritz2.core.id
import dev.fritz2.core.lensOf
import dev.fritz2.core.render
import dev.fritz2.core.storeOf
import dev.fritz2.core.type
import dev.fritz2.routing.MapRouter
import io.ktor.util.StringValues
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.flow.map
import org.w3c.dom.HTMLDivElement
import playground.PlaygroundContainer
import playground.components.app.AppShowcases
import playground.fritz2.Fritz2HeadlessUiDemoAdapter
import playground.fritz2.headlessdemo.pages

@JsModule("./styles/playground-app.scss")
external val PlaygroundStyles: dynamic

fun main() {
    PlaygroundStyles

    val rootElement = document.getElementById("root").unsafeCast<HTMLDivElement>()

    val appStore = AppStore()

    class PagesStore(
        initialPages: List<Page>,
    ) : RootStore<List<Page>>(initialPages) {
        constructor(vararg initialPages: Page) : this(initialPages.asList())

        val router = MapRouter(mapOf("page" to (initialPages.firstOrNull()?.id ?: "")))
        val selectedPage: Store<Page?> = router.mapByKey("page").map(lensOf("page", { id ->
            current.firstNotNullOfOrNull { p ->
                if (p.id == id) p
                else p.pages.firstOrNull { it.id == id }
            }
        }, { _, page -> page?.id ?: "" }))
    }

    val pages = PagesStore(
        PlaygroundContainer,
        Page(
            id = "hello",
            label = "Hello!",
            description = "Hello! app showcases",
            icon = ImageFixtures.HelloFavicon,
            groups = listOf(
                listOf(
                    HeadlessUiShowcases,
                    EnvironmentShowcases,
                    SessionShowcases,
                    UserShowcases,
                    PropsShowcases,
                    AppShowcases,
                )
            ),
            pageContent = {
                val x = storeOf(
                    listOf(
                        HeadlessUiShowcases,
                        EnvironmentShowcases,
                        SessionShowcases,
                        UserShowcases,
                        PropsShowcases,
                        AppShowcases,
                    )
                )
                if (true) {
                    x.current.forEach { page ->
                        button("btn") {
                            type("button")
                            +page.label
                            clicks handledBy {
                                window.location.fragmentParameters = StringValues.build(window.location.fragmentParameters) {
                                    set("page", page.id)
                                }
                            }
                        }
                    }
                } else horizontalScreens("max-h-[50vh]") {
                    x.current.forEach { page ->
                        screen("overflow-clip scale-50") {
                            id(page.id)
                            page.pageContent?.invoke(this)
                        }
                    }
                }
            }
        ),
        Fritz2HeadlessUiDemoAdapter(pages, "page"),
    )

    render(".app") {

        navigationBar(
            "sticky top-0 inset-x-0 z-[20]",
            selection = pages.selectedPage.map(lensOf(
                id = "nav",
                getter = { it },
                setter = { _, navItem -> navItem as? Page }
            )),
            navItems = pages.current,
            startContent = {
                div("flex flex-shrink-0 items-center") {
                    button(
                        classes(
                            "group",
                            "inline-flex w-full items-center justify-center",
                            "text-left",
                            "hover:box-glass",
                            "focus:outline-none focus-visible:ring focus-visible:ring-white focus-visible:ring-opacity-75"
                        )
                    ) {
                        type("button")
                        icon("shrink-0 block h-8 w-auto", ImageFixtures.HelloFavicon)
                        clicks.map { null } handledBy pages.selectedPage.update
                    }
                }
            }
        ) {
            appStore.data.render { if (it is AppState.Loaded) userDropdown("ml-3", it.session) }
        }

        if (false) div("w-screen h-screen pt-16 -mt-16") {
            verticalScreens {
                pages.current.forEach { container ->
                    if (container.pages.isEmpty()) {
                        screen("overflow-clip") {
                            id(container.id)
                            container.pageContent?.invoke(this)
                        }
                    } else {
                        horizontalScreens {
                            screen("overflow-clip") {
                                id(container.id)
                                container.pageContent?.invoke(this)
                            }
                            container.pages.forEach { page ->
                                screen("overflow-clip") {
                                    id(page.id)
                                    page.pageContent?.invoke(this)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            pages.selectedPage.data.render { page ->
                if (page == null) {
                    screens(
                        "w-screen h-screen pt-16 -mt-16",
                        pages = pages,
                        selectedPage = storeOf(pages.selectedPage.current.also { console.warn("SELECTED PAGE", it) }),
                        type = ScreensType.Vertical,
                    )
                } else {
                    div("w-full h-screen pt-16 -mt-16") {
                        page.pageContent?.invoke(this)
                    }
                }
            }
        }

        diagnostics {
            div("relative z-10 flex justify-end -mt-6") {
                appStore.session.render { if (it != null) userDropdown("-translate-y-8", it) else div("-translate-y-6") { loader() } }
            }
            appStore.props.render { if (it != null) propsView(it) }
            hr {}
            appStore.session.render { if (it != null) sessionView(it) }
            hr {}
            appStore.environment.render { if (it != null) environmentView(it) }
        }
    }
}
