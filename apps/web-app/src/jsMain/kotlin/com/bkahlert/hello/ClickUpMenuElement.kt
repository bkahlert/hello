package com.bkahlert.hello

import androidx.compose.runtime.Composable
import com.bkahlert.hello.clickup.client.http.ClickUpHttpClient
import com.bkahlert.hello.clickup.client.http.ClickUpHttpClientConfigurer
import com.bkahlert.hello.clickup.view.ClickUpTestClientConfigurer
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenu
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Succeeded.Disabled
import com.bkahlert.hello.clickup.viewmodel.ClickUpStyleSheet
import com.bkahlert.hello.clickup.viewmodel.rememberClickUpMenuViewModel
import com.bkahlert.hello.data.Resource
import com.bkahlert.hello.data.Resource.Failure
import com.bkahlert.hello.data.Resource.Success
import com.bkahlert.kommons.dom.ScopedStorage.Companion.scoped
import com.bkahlert.semanticui.custom.ErrorMessage
import com.bkahlert.semanticui.custom.LoadingState.On
import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.browser.window
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.boxSizing
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.TagElement
import org.w3c.dom.CustomElementRegistry
import org.w3c.dom.HTMLElement
import org.w3c.dom.OPEN
import org.w3c.dom.ShadowRootInit
import org.w3c.dom.ShadowRootMode

@Composable
fun ClickUpMenuElement(
    clickUpPropsResource: Resource<ClickUpProps?>?,
) {
    window.customElements.defineWhenUndefined("clickup-menu") {
        val shadowRoot = attachShadow(ShadowRootInit(ShadowRootMode.OPEN));
        shadowRoot.appendChild(document.createElement("slot"));
    }

    TagElement<HTMLElement>("clickup-menu",
        applyAttrs = null,
        content = {
            Style(ClickUpStyleSheet)
            Div({
                style { boxSizing("border-box") } // normally inherited by a html css rule
            }) {
                when (clickUpPropsResource) {
                    null -> ClickUpMenu(
                        rememberClickUpMenuViewModel(),
                        state = Disabled,
                        loadingState = On,
                    )

                    is Success -> {
                        ClickUpMenu(
                            rememberClickUpMenuViewModel(
                                ClickUpHttpClientConfigurer(),
                                ClickUpTestClientConfigurer(),
                                initialState = Disabled,
                                storage = localStorage.scoped("clickup")
                            ).apply {
                                val clickUpProps = clickUpPropsResource.data
                                if (clickUpProps?.apiToken != null) {
                                    enable(ClickUpHttpClient(clickUpProps.apiToken, localStorage.scoped("clickup")))
                                } else {
                                    enable()
                                }
                            },
                        )
                    }

                    is Failure -> {
                        ErrorMessage(clickUpPropsResource.message, clickUpPropsResource.cause)
                    }
                }
            }
        }
    )
}

/**
 * Creates a new JavaScript function
 * that accepts the specified [args] and is implemented
 * by the specified [code].
 */
@JsName("Function")
private external fun <T> Function(vararg args: String, code: String): T

// language=es6
private const val ES6_CLASS_ADAPTER = """return class extends HTMLElement {
  constructor() {
    super();
    // `init` exists because `Function` is called with it as the only argument
    // noinspection JSUnresolvedFunction
    init(this)
  }
}"""
private val customElementClassFactory: () -> () -> dynamic = Function(code = ES6_CLASS_ADAPTER)
fun CustomElementRegistry.defineWhenUndefined(name: String, init: HTMLElement.() -> Unit) {
    if (get(name) == null) {
        val constructor = Function<(init: HTMLElement.() -> Unit) -> () -> dynamic>("init", code = ES6_CLASS_ADAPTER)
        define(name, constructor(init))
    }
}

@Composable
fun CustomElement(
    name: String,
    attrs: AttrBuilderContext<HTMLElement>? = null,
    content: ContentBuilder<HTMLElement>? = null,
) {
    window.customElements.defineWhenUndefined(name) {
        val shadowRoot = attachShadow(ShadowRootInit(ShadowRootMode.OPEN));
        shadowRoot.appendChild(document.createElement("slot"));
    }

    TagElement(name, attrs, content)
}
