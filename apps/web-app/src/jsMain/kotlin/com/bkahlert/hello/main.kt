package com.bkahlert.hello

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.bkahlert.hello.BookmarkProps.Companion.mapBookmarkProps
import com.bkahlert.hello.ClickUpProps.Companion.mapClickUpProps
import com.bkahlert.hello.app.ui.AppViewModel
import com.bkahlert.hello.app.ui.rememberAppViewModel
import com.bkahlert.hello.data.Resource.Failure
import com.bkahlert.hello.data.Resource.Success
import com.bkahlert.hello.environment.data.DynamicEnvironmentDataSource
import com.bkahlert.hello.environment.data.EnvironmentRepository
import com.bkahlert.hello.fritz2.ContentBuilder
import com.bkahlert.hello.fritz2.components.ScreensType
import com.bkahlert.hello.fritz2.components.diagnostics
import com.bkahlert.hello.fritz2.components.embed.EmbedProps
import com.bkahlert.hello.fritz2.components.navigationbar.NavItem
import com.bkahlert.hello.fritz2.components.navigationbar.navigationBar
import com.bkahlert.hello.fritz2.components.proseBox
import com.bkahlert.hello.fritz2.components.showcase.showcase
import com.bkahlert.hello.fritz2.components.verticalScreens
import com.bkahlert.hello.fritz2.compose.compose
import com.bkahlert.hello.fritz2.register
import com.bkahlert.hello.search.PasteHandlingMultiSearchInput
import com.bkahlert.hello.user.ui.UserMenu
import com.bkahlert.kommons.dom.uri
import com.bkahlert.kommons.randomString
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.semanticui.collection.Item
import com.bkahlert.semanticui.collection.Menu
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Huge
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Large
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Massive
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Mini
import com.bkahlert.semanticui.custom.ErrorMessage
import com.bkahlert.semanticui.custom.IFrame
import com.bkahlert.semanticui.custom.ReportingCoroutineScope
import com.bkahlert.semanticui.custom.Sandbox.ALLOW_POPUPS
import com.bkahlert.semanticui.custom.Sandbox.ALLOW_SAME_ORIGIN
import com.bkahlert.semanticui.custom.Sandbox.ALLOW_SCRIPTS
import com.bkahlert.semanticui.custom.Sandbox.ALLOW_TOP_NAVIGATION
import com.bkahlert.semanticui.custom.Sandbox.ALLOW_TOP_NAVIGATION_BY_USER_ACTIVATION
import com.bkahlert.semanticui.custom.sandbox
import com.bkahlert.semanticui.custom.src
import com.bkahlert.semanticui.element.AnchorButton
import com.bkahlert.semanticui.element.Buttons
import com.bkahlert.semanticui.element.Container
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.Loader
import com.bkahlert.semanticui.element.active
import com.bkahlert.semanticui.element.icon
import com.bkahlert.semanticui.element.inline
import com.bkahlert.semanticui.element.size
import com.bkahlert.semanticui.module.updateDebugSettings
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.RenderContext
import dev.fritz2.core.classes
import dev.fritz2.core.render
import dev.fritz2.core.src
import dev.fritz2.core.storeOf
import dev.fritz2.webcomponents.WebComponent
import kotlinx.browser.window
import org.jetbrains.compose.web.css.border
import org.jetbrains.compose.web.css.px
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.ShadowRoot

@Composable
fun WebApp(
    viewModel: AppViewModel = rememberAppViewModel(),
) {

    S("toolbar") {
        Buttons({
            classes("links")
            v.icon()
        }) {
            AnchorButton("https://start.me/p/4K6MOy/dashboard") { Icon("globe") }
            AnchorButton("https://home.bkahlert.com") { Icon("home") }
            AnchorButton("https://github.com/bkahlert") { Icon("github") }
            AnchorButton("https://console.aws.amazon.com/") { Icon("aws") }
        }

        S("tasks") {
            val clickUpPropsResource by viewModel.getProp("clickup").mapClickUpProps().collectAsState(null)
            ClickUpMenuElement(clickUpPropsResource)
        }

        S("search") {
            PasteHandlingMultiSearchInput()
        }
    }

    S("content") {

        S("bookmarks") {
            val bookmarkPropsResource by viewModel.getProp("bookmark").mapBookmarkProps().collectAsState(null)
            when (val resource = bookmarkPropsResource) {
                null -> Loader({ v.size(Huge); s.active() })

                is Success -> {
                    val defaultUri = resource.data?.default
                    IFrame("Loading bookmarks …", { v.size(Large) }) {
                        style { border(0.px) }
                        src(defaultUri?.takeIf { it.scheme == window.location.uri.scheme } ?: "about:blank")
                        sandbox(
                            ALLOW_POPUPS,
                            ALLOW_SCRIPTS,
                            ALLOW_SAME_ORIGIN,
                            ALLOW_TOP_NAVIGATION,
                            ALLOW_TOP_NAVIGATION_BY_USER_ACTIVATION,
                        )
                    }
                }

                is Failure -> {
                    ErrorMessage(resource.message, resource.cause)
                }
            }
        }
    }

    S("footer") {
        val userResource by viewModel.user.collectAsState(null)
        when (val resource = userResource) {
            null -> Menu({ classes("tiny", "compact", "secondary") }) { Item { Loader({ v.size(Mini).inline(); s.active() }) } }

            is Success -> UserMenu(
                user = resource.data,
                onSignIn = viewModel::authorize,
                onReauthorize = { viewModel.reauthorize(force = false) }.takeUnless { resource.data == null },
                onSignOut = viewModel::unauthorize,
                attrs = { classes("tiny", "compact", "secondary") }
            )

            is Failure -> UserMenu(
                userResource = resource,
                onReauthorize = { viewModel.reauthorize(force = false) },
                onForceReauthorize = { viewModel.reauthorize(force = true) },
                attrs = { classes("tiny", "compact", "secondary") }
            )
        }
    }
}

@JsModule("./semantic/semantic.less")
private external object SemanticStyles {
    val styles: dynamic
    fun use(): dynamic
}

@JsModule("./styles/web-app.scss")
private external val AppStyles: dynamic

fun main() {
    SemanticStyles
    AppStyles

//    val envStore = EnvironmentStore(DynamicEnvironmentDataSource())
//    val sessionStore = envStore.data.map { it?.let { SessionStore(OpenIDConnectSessionDataSource(it)) } }
//    val userStore = sessionStore.mapLatest { it?.let(::UserStore) }
//    val propsStore = sessionStore.flatMapLatest {
//        it?.data
//            ?.filterIsInstance<Session.AuthorizedSession>()
//            ?.combine(envStore.data.filterNotNull(), com.bkahlert.hello.props.data::SessionPropsDataSource)
//            ?.map { PropsStore(it) }
//            ?: flowOf(null)
//    }


    render("#root") {
        diagnostics {
            proseBox {
                h1("text-2xl font-bold") { +"Diagnostics" }
                p { +"This page is used to diagnose the application." }
            }
        }

        navigationBar(
            "sticky top-0 inset-x-0 z-[20]",
            selection = storeOf<NavItem?>(null),
            navItems = emptyList(),
        ) {
        }

        val props = EmbedProps(
            Uri("https://start.me/p/0PBMOo/dkb"),
            mapOf(
                "sandbox" to "allow-popups allow-scripts allow-same-origin allow-top-navigation allow-top-navigation-by-user-activation",
            ),
        )

        div("w-screen h-screen pt-16 -mt-16") {
            verticalScreens(
                classes(
                    "[&>*]:min-h-[50vh]",
                    "[&>*:first-child]:min-h-[75vh]",
                    "[&>*:last-child]:min-h-[75vh]",
                ), type = ScreensType.Vertical
            ) {
                screen {
                    isolate {
                        div("app") {
                            updateDebugSettings { module, settings ->
                                settings.debug = module !in listOf("transition")
                                settings.verbose = true
                                settings.performance = false
                            }
                            val composeScope = ReportingCoroutineScope()
                            compose {
                                val environmentRepository = remember { EnvironmentRepository(DynamicEnvironmentDataSource(), composeScope) }
                                val environmentResource by environmentRepository.environmentFlow().collectAsState(null)
                                when (val resource = environmentResource) {
                                    null -> {
                                        Loader({ v.size(Massive); s.active() })
                                    }

                                    is Success -> {
                                        WebApp(rememberAppViewModel(environment = resource.data))
                                    }

                                    is Failure -> S("progress") {
                                        Container { ErrorMessage(resource.message, resource.cause) }
                                    }
                                }
                            }
                        }
                    }
                }

                screen {
                    appliance("Rick Astley") {
                        embed(EmbedProps.Default)
                    }
                }
                screen {
                    appliance("Bookmarks") {
                        embed(
                            classes(
                                "sm:rounded-xl",
                                "bg-hero-diagonal-lines",
                                "box-shadow",
                                "overflow-hidden",
                            ),
                            props
                        )
                    }
                }
                screen {
                    appliance("ClickUp") {
                        div("app") {
                            updateDebugSettings { module, settings ->
                                settings.debug = module !in listOf("transition")
                                settings.verbose = true
                                settings.performance = false
                            }
                            val composeScope = ReportingCoroutineScope()
                            compose {
                                val environmentRepository = remember { EnvironmentRepository(DynamicEnvironmentDataSource(), composeScope) }
                                val environmentResource by environmentRepository.environmentFlow().collectAsState(null)
                                when (val resource = environmentResource) {
                                    null -> {
                                        Loader({ v.size(Massive); s.active() })
                                    }

                                    is Success -> {
                                        WebApp(rememberAppViewModel(environment = resource.data))
                                    }

                                    is Failure -> S("progress") {
                                        Container { ErrorMessage(resource.message, resource.cause) }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private val contents = mutableMapOf<String, ContentBuilder?>()
public fun RenderContext.isolate(
    content: ContentBuilder? = null,
) {
    val localName = "isolated-${randomString().lowercase()}"
    contents[localName] = content
    IsolatedContent.register(localName).invoke(this, null, null, {}) {
        className(classes("relative", "overflow-hidden", "w-screen", "h-screen"))
    }
}

object IsolatedContent : WebComponent<HTMLDivElement>() {
    override fun RenderContext.init(element: HTMLElement, shadowRoot: ShadowRoot): HtmlTag<HTMLDivElement> {
        console.warn("CONTENT", SemanticStyles.use())
        console.warn("CONTENT", SemanticStyles.styles)
//        linkStylesheet(shadowRoot, "./semantic/semantic.less")
        val xxx = js("require('./semantic/semantic.less')")
        setStylesheet(shadowRoot, SemanticStyles.styles)
        console.warn("CONTENT-NODENAME", element.nodeName)
        console.warn("CONTENT-CONTENTS", contents.keys.toTypedArray<String>())
        val content = contents[element.nodeName.lowercase()]
        console.warn("CONTENT", content)
        return proseBox {
            h1 { +"Isolated Content" }
            content?.invoke(this)
        }
    }
}


public fun RenderContext.appliance(
    name: String,
    content: ContentBuilder? = null,
) {
    showcase(name, simple = true, classes = "m-16") {

//        "[&>*]:transition",
//        "[&>*]:opacity-20",
//        "[&>*]:scale-75",
//        "[&>*:hover]:opacity-100",
//        "[&>*:hover]:scale-100",

        div("rounded-xl block overflow-hidden h-full mt-2 ring-8 ring-gray-500/40") {
            content?.invoke(this)
        }
    }
}

public fun RenderContext.embed(
    classes: String?,
    props: EmbedProps,
) {
    div(
        classes(
            "aspect-w-16 aspect-h-9",
            classes
        )
    ) {
        iframe("border-0") {
            src(props.uri.toString())
            props.attributes.forEach { (key, value) -> attr(key, value) }
        }
    }
}

public fun RenderContext.embed(
    props: EmbedProps,
) = embed(null, props)
