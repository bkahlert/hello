package com.bkahlert.hello.clickup

import androidx.compose.runtime.Composition
import com.bkahlert.hello.clickup.client.ClickUpHttpClient
import com.bkahlert.hello.clickup.client.ClickUpHttpClientConfigurer
import com.bkahlert.hello.clickup.model.fixtures.ClickUpTestClient
import com.bkahlert.hello.clickup.view.ClickUpTestClientConfigurer
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenu
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Succeeded.Disabled
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Succeeded.Disconnected
import com.bkahlert.hello.clickup.viewmodel.ClickUpStyleSheet
import com.bkahlert.hello.clickup.viewmodel.fixtures.rememberClickUpMenuTestViewModel
import com.bkahlert.hello.clickup.viewmodel.fixtures.toFullyLoaded
import com.bkahlert.hello.clickup.viewmodel.fixtures.toPartiallyLoaded
import com.bkahlert.hello.clickup.viewmodel.fixtures.toTeamSelecting
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
import kotlinx.dom.appendText
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.ShadowRoot

/**
 * Maps a [demoState] string (set via the `demo-state` attribute on `<clickup-menu-v2>`)
 * to a [ClickUpMenuState] from the fixtures module. Unknown or empty values fall back
 * to the default fully-loaded state without a running pomodoro.
 */
private fun ClickUpTestClient.demoStateOrDefault(demoState: String): ClickUpMenuState = when (demoState) {
    "disabled" -> Disabled
    "disconnected" -> Disconnected
    "team-selecting" -> toTeamSelecting()
    "partially-loaded" -> toPartiallyLoaded(runningTimeEntry = null)
    "fully-loaded-running" -> toFullyLoaded()
    "fully-loaded" -> toFullyLoaded(runningTimeEntry = null)
    else -> toFullyLoaded(runningTimeEntry = null)
}

private val clickUpMenu by lazy {
    document.head().appendStyle(FontStyles)
    ClickUpComponent.register("clickup-menu-v2", "props", "css", "demo-state")
    custom<Tag<HTMLDivElement>>("clickup-menu-v2")
}

public fun RenderContext.clickUpMenu(
    clickUpProps: ClickUpProps? = null,
    css: String? = null,
    demoState: String? = null,
): Tag<HTMLDivElement> = clickUpMenu(this, null, null, {}) {
    attr("props", clickUpProps?.let { LenientJson.encodeToString(ClickUpProps.serializer(), it) }?.encodeBase64())
    attr("css", css ?: "")
    if (demoState != null) attr("demo-state", demoState)
}

public object ClickUpComponent : WebComponent<HTMLDivElement>() {
    // Note: `attributeChanges` emits across every <clickup-menu-v2> instance
    // sharing this singleton, so we only use it for the `css` runtime-update
    // path (where same-value emissions across instances are harmless). The
    // `props` and `demo-state` attributes are read per-element in init().
    private val css: Flow<String> = attributeChanges("css")

    /**
     * Per-element state. Kotlin's `object` declaration is a true singleton even
     * when fritz2's JS wrapper does `new _component()`, so any field on the
     * component class would be shared across every `<clickup-menu-v2>` element
     * on the page. The state is stashed directly on the host element via a
     * hidden JS property; a Kotlin `MutableMap<HTMLElement, _>` was tried but
     * Kotlin/JS map keys collide for fresh DOM nodes (all 6 elements ended up
     * sharing one map slot), so we use the element's own JS object as storage.
     */
    private class InstanceState(
        val element: HTMLElement,
        val root: HTMLDivElement,
        var composition: Composition? = null,
    )

    private const val INSTANCE_STATE_KEY = "__clickUpComponentState"

    private fun stateFor(element: HTMLElement): InstanceState? =
        element.asDynamic()[INSTANCE_STATE_KEY].unsafeCast<InstanceState?>()

    private fun setStateFor(element: HTMLElement, state: InstanceState) {
        element.asDynamic()[INSTANCE_STATE_KEY] = state
    }

    private fun clearStateFor(element: HTMLElement) {
        element.asDynamic()[INSTANCE_STATE_KEY] = null
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
        val rootTag = div {}
        val state = InstanceState(element = element, root = rootTag.domNode)
        setStateFor(element, state)

        // Load jQuery and Semantic UI once globally and let every subsequent
        // instance reuse them — the second `<script src="...">` element pointing
        // at an already-cached resource often fires no `load` event in some
        // browsers, especially across multiple shadow roots, so we cannot rely
        // on per-instance script load chains. Instead the first instance loads
        // the scripts and broadcasts via a global Promise that any later
        // instance can `then(...)` on synchronously.
        scriptsReady().then {
            connectedCallback(element)
        }
        return rootTag
    }

    /** Browser-global cache of the jQuery + Semantic UI load promise. */
    private var scriptsReadyPromise: dynamic = null
    private fun scriptsReady(): dynamic {
        if (scriptsReadyPromise == null) {
            scriptsReadyPromise = js(
                """
                new Promise(function(resolve) {
                    var jq = document.createElement('script');
                    jq.src = 'clickup/jquery.min.js';
                    jq.onload = function() {
                        var su = document.createElement('script');
                        su.src = 'clickup/semantic.min.js';
                        su.onload = function() { resolve(); };
                        document.head.appendChild(su);
                    };
                    document.head.appendChild(jq);
                })
                """
            )
        }
        return scriptsReadyPromise
    }

    override fun connectedCallback(element: HTMLElement) {
        val state = stateFor(element) ?: return
        // Idempotency: connectedCallback is invoked once by the browser when
        // the element is inserted (which happens before our scripts have
        // finished loading) and once again from `scriptsReady().then`. The
        // first call has no jQuery on `window` yet, so bail out; the second
        // call (after `scriptsReady` resolves) actually mounts the composition.
        // We also bail if we already mounted (defensive against a third call).
        if (state.composition != null) return
        if (js("typeof window.jQuery === 'undefined'") as Boolean) return
        state.root.appendScript(null) {
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

        // Read props/demoState directly from the element's attributes rather than
        // from the shared `attributeChanges` Flow — that Flow emits for every
        // instance of the component, so when multiple `<clickup-menu-v2>` elements
        // exist on a page, each composition would receive every other instance's
        // attribute values and the last one wins. Reading per-element captures
        // this instance's own configuration.
        val rawProps = element.getAttribute("props").orEmpty()
        val rawDemoState = element.getAttribute("demo-state").orEmpty()
        val resolvedProps: ClickUpProps? = rawProps.takeUnless { it.isBlank() }
            ?.decodeBase64String()
            ?.let { LenientJson.decodeFromString(ClickUpProps.serializer(), it) }

        state.composition = renderComposable(state.root) {
            Style(ClickUpStyleSheet)
            when (resolvedProps) {
                null -> {
                    ClickUpMenu(rememberClickUpMenuTestViewModel { demoStateOrDefault(rawDemoState) })
                }

                else -> {
                    val apiToken = resolvedProps.apiToken
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
                        ClickUpMenu(rememberClickUpMenuTestViewModel { demoStateOrDefault(rawDemoState) })
                    }
                }
            }
        }
    }

    override fun disconnectedCallback(element: HTMLElement) {
        stateFor(element)?.composition?.dispose()
        clearStateFor(element)
    }
}
