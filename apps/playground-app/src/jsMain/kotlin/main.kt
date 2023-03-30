import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.clickup.demo.ClickUpDemoProvider
import com.bkahlert.hello.clickup.model.fixtures.ImageFixtures
import com.bkahlert.hello.clickup.viewmodel.ClickUpStyleSheet
import com.bkahlert.hello.data.Resource.Failure
import com.bkahlert.hello.data.Resource.Success
import com.bkahlert.hello.demo.HelloDemoProviders
import com.bkahlert.hello.environment.data.DynamicEnvironmentDataSource
import com.bkahlert.hello.environment.data.EnvironmentRepository
import com.bkahlert.hello.environment.ui.EnvironmentView
import com.bkahlert.hello.fritz2.components.Page
import com.bkahlert.hello.fritz2.components.ScreensType
import com.bkahlert.hello.fritz2.components.diagnostics
import com.bkahlert.hello.fritz2.components.headlessui.HeadlessUiShowcases
import com.bkahlert.hello.fritz2.components.heroicons.OutlineHeroIcons
import com.bkahlert.hello.fritz2.components.horizontalScreens
import com.bkahlert.hello.fritz2.components.icon
import com.bkahlert.hello.fritz2.components.navigationbar.navigationBar
import com.bkahlert.hello.fritz2.components.pages
import com.bkahlert.hello.fritz2.components.proseBox
import com.bkahlert.hello.fritz2.components.screens
import com.bkahlert.hello.fritz2.components.verticalScreens
import com.bkahlert.hello.fritz2.compose.compose
import com.bkahlert.hello.props.data.SessionPropsDataSource
import com.bkahlert.hello.session.data.OpenIDConnectSessionDataSource
import com.bkahlert.hello.session.data.SessionRepository
import com.bkahlert.hello.session.ui.SessionView
import com.bkahlert.kommons.auth.Session
import com.bkahlert.kommons.auth.Session.AuthorizedSession
import com.bkahlert.kommons.devmode.DevMode
import com.bkahlert.kommons.dom.fragmentParameters
import com.bkahlert.kommons.uri.build
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.custom.ErrorMessage
import com.bkahlert.semanticui.custom.rememberReportingCoroutineScope
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.DemoProvider
import com.bkahlert.semanticui.demo.SemanticUiDemoProviders
import com.bkahlert.semanticui.demo.orZero
import com.bkahlert.semanticui.demo.toWord
import com.bkahlert.semanticui.devmode.ComposeDevSession
import com.bkahlert.semanticui.element.Header
import com.bkahlert.semanticui.element.Loader
import com.bkahlert.semanticui.module.updateDebugSettings
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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.maxHeight
import org.jetbrains.compose.web.css.overflowX
import org.jetbrains.compose.web.css.overflowY
import org.jetbrains.compose.web.css.vh
import org.jetbrains.compose.web.dom.Code
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Pre
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement
import playground.PlaygroundContainer
import playground.architecture.ArchitectureDemoProvider
import playground.clickupapp.ClickUpAppDemoProvider
import playground.components.app.AppShowcases
import playground.components.environment.EnvironmentShowcases
import playground.components.environment.EnvironmentStore
import playground.components.environment.environmentView
import playground.components.loader
import playground.components.props.PropsShowcases
import playground.components.props.PropsStore
import playground.components.props.propsView
import playground.components.session.SessionShowcases
import playground.components.session.SessionStore
import playground.components.session.sessionView
import playground.components.user.UserShowcases
import playground.components.user.UserStore
import playground.components.user.userDropdown
import playground.fritz2.Fritz2HeadlessUiDemoAdapter
import playground.fritz2.headlessdemo.pages

@JsModule("./semantic/semantic.less")
private external val SemanticStyles: dynamic

@JsModule("./styles/playground-app.scss")
external val PlaygroundStyles: dynamic

fun main() {
    SemanticStyles
    PlaygroundStyles

    updateDebugSettings { module, settings ->
        settings.debug = true || module !in listOf("transition")
        settings.verbose = true
        settings.performance = true
    }

    val rootElement = document.getElementById("root").unsafeCast<HTMLDivElement>()

    val devMode = DevMode(name = "playground") {
        val rootSibling = document.createElement("div").unsafeCast<HTMLDivElement>().apply {
            classList.add("dev-session")
            checkNotNull(rootElement.parentNode).insertBefore(this, rootElement)
        }

        ComposeDevSession(rootSibling) {
            Div({
                classes("p-6", "mx-auto", "bg-white", "rounded-xl", "shadow-lg", "flex", "items-center", "space-x-4")
                style {
                    maxHeight(35.vh)
                    overflowX("hidden")
                    overflowY("scroll")
                }
            }) {
                Demo("Using Environment") { demoScope ->
                    val environmentRepository = remember { EnvironmentRepository(DynamicEnvironmentDataSource(), demoScope) }
                    val environmentResource by environmentRepository.environmentFlow().collectAsState(null)
                    when (val currentEnvironmentResource = environmentResource) {
                        null -> Loader("Loading environment")
                        is Success -> {
                            val sessionDataSource = remember { OpenIDConnectSessionDataSource(currentEnvironmentResource.data) }
                            val sessionRepository = remember { SessionRepository(sessionDataSource, demoScope) }
                            val sessionResource by sessionRepository.sessionFlow().collectAsState(null)
                            when (val currentSessionResource = sessionResource) {
                                null -> Loader("Loading session")
                                is Success -> {
                                    when (val session = currentSessionResource.data) {
                                        is Session.UnauthorizedSession -> P { Text("Unauthorized") }
                                        is Session.AuthorizedSession -> {
                                            val propsDataSource = remember { SessionPropsDataSource(session, currentEnvironmentResource.data) }
                                            var attempt: Any? by remember { mutableStateOf(null) }
                                            LaunchedEffect(propsDataSource) {
                                                try {
                                                    attempt = propsDataSource.getAll()
                                                } catch (ex: CancellationException) {
                                                    throw ex
                                                } catch (ex: Throwable) {
                                                    attempt = ex
                                                }
                                            }
                                            Header { Text("Response") }
                                            Pre {
                                                Code {
                                                    Text(attempt.toString())
                                                }
                                            }
                                        }
                                    }

                                    SessionView(currentSessionResource.data)
                                }

                                is Failure -> ErrorMessage(currentSessionResource.cause)
                            }

                            EnvironmentView(currentEnvironmentResource.data)
                        }

                        is Failure -> ErrorMessage(currentEnvironmentResource.message, currentEnvironmentResource.cause)
                    }
                }

            }
        }
    }

    val envStore = EnvironmentStore(DynamicEnvironmentDataSource())
    val sessionStore = envStore.data.map { it?.let { SessionStore(OpenIDConnectSessionDataSource(it)) } }
    val userStore = sessionStore.mapLatest { it?.let(::UserStore) }
    val propsStore = sessionStore.flatMapLatest {
        it?.data
            ?.filterIsInstance<AuthorizedSession>()
            ?.combine(envStore.data.filterNotNull(), ::SessionPropsDataSource)
            ?.map { PropsStore(it) }
            ?: flowOf(null)
    }

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
            content = {
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
                            page.content?.invoke(this)
                        }
                    }
                }
            }
        ),
        Fritz2HeadlessUiDemoAdapter(pages, "page"),
        DemoPageContainer,
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
            userStore.render {
                if (it != null) userDropdown("ml-3", it)
                else loader()
            }
        }

        if (false) div("w-screen h-screen pt-16 -mt-16") {
            verticalScreens {
                pages.current.forEach { container ->
                    if (container.pages.isEmpty()) {
                        screen("overflow-clip") {
                            id(container.id)
                            container.content?.invoke(this)
                        }
                    } else {
                        horizontalScreens {
                            screen("overflow-clip") {
                                id(container.id)
                                container.content?.invoke(this)
                            }
                            container.pages.forEach { page ->
                                screen("overflow-clip") {
                                    id(page.id)
                                    page.content?.invoke(this)
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
                        page.content?.invoke(this)
                    }
                }
            }
        }

        diagnostics {
            div("relative z-10 flex justify-end -mt-6") {
                userStore.render {
                    if (it != null) userDropdown("-translate-y-8", it)
                    else div("-translate-y-6") { loader() }
                }
            }
            envStore.data.render { environmentView(it) }
            hr {}
            sessionStore.flatMapLatest { it?.data ?: emptyFlow() }.render { sessionView(it) }
            hr {}
            propsStore.flatMapLatest { it?.data ?: emptyFlow() }.render { propsView(it) }
        }
    }
}

fun DemoProvider.toPage(): Page {
    val provider = this
    return Page(
        id = id,
        label = name,
        icon = OutlineHeroIcons.square_2_stack,
        content = {
            div("m-2 p-2 ring-1 ring-gray-300 rounded-md") {
                compose {
                    Style(ClickUpStyleSheet)
                    S(
                        "ui",
                        provider.content.size.orZero().coerceIn(1..3).toWord(),
                        "column", "stackable", "doubling", "grid", "segment",
                    ) {
                        val demoProviderScope = rememberReportingCoroutineScope()
                        provider.content.forEach { demoProviderContent ->
                            S("column", content = { demoProviderContent(demoProviderScope) })
                        }
                    }
                }
            }
        },
    )
}

val DemoPageContainer = Page(
    id = "demo",
    label = "V2 Demos",
    description = "Hello! v2 demos",
    icon = SemanticUiDemoProviders.first().logo.also { console.warn(it) } ?: OutlineHeroIcons.square_2_stack,
    groups = listOf(
        listOf(ClickUpAppDemoProvider),
        HelloDemoProviders.asList(),
        listOf(ClickUpDemoProvider),
        listOf(ArchitectureDemoProvider),
        SemanticUiDemoProviders.asList(),
    ).map { it.map { it.toPage() } },
    content = { proseBox() },
)
