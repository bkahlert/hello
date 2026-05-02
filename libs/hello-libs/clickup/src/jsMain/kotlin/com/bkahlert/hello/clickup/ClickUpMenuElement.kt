package com.bkahlert.hello.clickup

import androidx.compose.runtime.Composition
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.bkahlert.hello.clickup.client.ClickUpHttpClient
import com.bkahlert.hello.clickup.client.ClickUpHttpClientConfigurer
import com.bkahlert.hello.clickup.view.ClickUpTestClientConfigurer
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenu
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Succeeded.Disabled
import com.bkahlert.hello.clickup.viewmodel.ClickUpStyleSheet
import com.bkahlert.hello.clickup.viewmodel.fixtures.rememberClickUpMenuTestViewModel
import com.bkahlert.hello.clickup.viewmodel.fixtures.toFullyLoaded
import com.bkahlert.hello.clickup.viewmodel.rememberClickUpMenuViewModel
import com.bkahlert.hello.fritz2.custom
import com.bkahlert.hello.fritz2.register
import com.bkahlert.kommons.dom.ScopedStorage.Companion.scoped
import com.bkahlert.kommons.dom.appendScript
import com.bkahlert.kommons.dom.appendStyle
import com.bkahlert.kommons.dom.head
import com.bkahlert.kommons.json.LenientJson
import com.bkahlert.semanticui.module.updateDebugSettings
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Tag
import dev.fritz2.webcomponents.WebComponent
import io.ktor.util.decodeBase64String
import io.ktor.util.encodeBase64
import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.dom.appendText
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLScriptElement
import org.w3c.dom.ShadowRoot
import org.w3c.dom.events.EventListener

private val clickUpMenu by lazy {
    document.head().appendStyle(FontStyles)
    ClickUpComponent.register("clickup-menu-v2", "props", "css")
    custom<Tag<HTMLDivElement>>("clickup-menu-v2")
}

public fun RenderContext.clickUpMenu(
    clickUpProps: ClickUpProps? = null,
    css: String? = null,
): Tag<HTMLDivElement> = clickUpMenu(this, null, null, {}) {
    attr("props", clickUpProps?.let { LenientJson.encodeToString(ClickUpProps.serializer(), it) }?.encodeBase64())
    attr("css", css ?: "")
}

public object ClickUpComponent : WebComponent<HTMLDivElement>() {
    private val props: Flow<String> = attributeChanges("props")
    private val css: Flow<String> = attributeChanges("css")
    private val clickUpProps: Flow<ClickUpProps?> = props
        .map { it.takeUnless { it.isBlank() } }
        .map { it?.decodeBase64String() }
        .map { it?.let { LenientJson.decodeFromString(ClickUpProps.serializer(), it) } }

    private lateinit var root: HTMLDivElement
    private var composition: Composition? = null

    private val loadingScripts = mutableListOf<HTMLScriptElement>()
    private fun HTMLScriptElement.track(callback: () -> Unit) = also {
        loadingScripts.add(it)
        addEventListener("load", EventListener { _ ->
            loadingScripts.remove(it)
            callback()
        })
    }

    override fun RenderContext.init(element: HTMLElement, shadowRoot: ShadowRoot): HtmlTag<HTMLDivElement> {
        listOf(
            GlobalStyles, SiteStyles, ButtonStyles, DivierStyles, HeaderStyles,
            IconStyles, ImageStyles, InputStyles, LabelStyles, ListStyles, LoaderStyles,
            SegmentStyles, FormStyles, GridStyles, MessageStyles, ItemStyles, AccordionStyles,
            CheckboxStyles, DimmerStyles, DropdownStyles, ModalStyles, SearchStyles, TransitionStyles,
        ).forEach { shadowRoot.appendStyle(it) }
        val customCss = shadowRoot.appendStyle("")
        css.render { customCss.textContent = it }
        shadowRoot.appendScript("https://unpkg.com/jquery@3.6.4/dist/jquery.min.js").track {
            shadowRoot.appendScript("https://unpkg.com/semantic-ui@2.5.0/dist/semantic.min.js").track {
                connectedCallback(root)
            }
        }
        return div {}
            .also { root = it.domNode }
    }

    override fun connectedCallback(element: HTMLElement) {
        if (loadingScripts.isNotEmpty()) return
        root.appendScript(null) {
            appendText(
                """
                    window.jQuery = window.jQuery || jQuery;
                    window.jQueryPrototype = window.jQueryPrototype || jQuery.fn;
                    window.semanticModules = window.semanticModules || (Object.keys(jQueryPrototype).filter((key) => {
                      let semanticModule = jQueryPrototype[key];
                      return typeof semanticModule.settings === 'object';
                    }));

                    /**
                     * Instantiates a Semantic UI module.
                     * @param element the HTML element or jQuery selector of the element backing the module
                     * @param module the name of the module
                     * @param args the arguments to be passed to the module constructor
                     * @returns {*}
                     */
                    window.semanticConstructor = window.semanticConstructor || ((element, module, ...args) => {
                      return jQuery(element)[module](...args);
                    });

                    window.SemanticUI = window.SemanticUI || ({ jQuery, semanticModules, semanticConstructor });
                    window.SemanticUI = SemanticUI;
                """.trimIndent()
            )
        }
        updateDebugSettings { _, debugSettings ->
            debugSettings.apply {
                this.silent = false
                this.debug = false
                this.performance = false
                this.verbose = false
            }
        }

        composition = renderComposable(root) {
            val clickUpProps by clickUpProps.collectAsState(null)
            Style(ClickUpStyleSheet)
            when (clickUpProps) {
                null -> {
                    ClickUpMenu(rememberClickUpMenuTestViewModel { toFullyLoaded(runningTimeEntry = null) })
                }

                else -> {
                    val apiToken = clickUpProps?.apiToken
                    if (apiToken != null) {
                        ClickUpMenu(
                            rememberClickUpMenuViewModel(
                                ClickUpHttpClientConfigurer(),
                                ClickUpTestClientConfigurer(),
                                initialState = Disabled,
                                storage = localStorage.scoped("clickup")
                            ).apply {
                                enable(ClickUpHttpClient(apiToken, localStorage.scoped("clickup")))
                            },
                        )
                    } else {
                        ClickUpMenu(rememberClickUpMenuTestViewModel { toFullyLoaded(runningTimeEntry = null) })
                    }
                }
            }
        }
    }

    override fun disconnectedCallback(element: HTMLElement) {
        composition?.dispose()
    }
}
