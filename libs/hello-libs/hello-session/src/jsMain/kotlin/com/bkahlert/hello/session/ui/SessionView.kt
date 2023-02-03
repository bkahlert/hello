package com.bkahlert.hello.session.ui

import androidx.compose.runtime.Composable
import com.bkahlert.kommons.auth.Session
import com.bkahlert.kommons.auth.Session.AuthorizedSession
import com.bkahlert.kommons.auth.Session.UnauthorizedSession
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Mini
import com.bkahlert.semanticui.custom.LoadingState
import com.bkahlert.semanticui.custom.apply
import com.bkahlert.semanticui.element.Header
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.Item
import com.bkahlert.semanticui.element.LabeledIconButton
import com.bkahlert.semanticui.element.divided
import com.bkahlert.semanticui.element.size
import com.bkahlert.semanticui.module.inverted
import org.jetbrains.compose.web.dom.Em
import org.jetbrains.compose.web.dom.Text
import com.bkahlert.semanticui.element.List as SList

@Composable
public fun SessionView(
    session: Session? = null,
    onReauthorize: (() -> Unit)? = null,
    onAuthorize: (() -> Unit)? = null,
    onUnauthorize: (() -> Unit)? = null,
    loadingState: LoadingState = if (session == null) LoadingState.Indeterminate else LoadingState.Off,
) {
    SList({
        apply(loadingState)
        v.divided()
    }) {
        when (session) {
            null -> {
                apply(loadingState, dimmerAttrs = { v.inverted() }, loaderAttrs = { v.size(Mini) })
            }

            is UnauthorizedSession -> {
                Item {
                    Header { Em { Text("Signed-out") } }
                    if (onReauthorize != null) {
                        LabeledIconButton({
                            onClick { onReauthorize() }
                        }) {
                            Icon("sync")
                            Text("Refresh")
                        }
                    }
                    if (onAuthorize != null) {
                        LabeledIconButton({
                            onClick { onAuthorize() }
                        }) {
                            Icon("sign-in")
                            Text("Sign-in")
                        }
                    }
                    apply(loadingState, dimmerAttrs = { v.inverted() }, loaderAttrs = { v.size(Mini) })
                }
            }

            is AuthorizedSession -> {
                Item {
                    Header { Em { Text("Signed-in") } }
                    if (onReauthorize != null) {
                        LabeledIconButton({
                            onClick { onReauthorize() }
                        }) {
                            Icon("sync")
                            Text("Refresh")
                        }
                    }
                    if (onUnauthorize != null) {
                        LabeledIconButton({
                            onClick { onUnauthorize() }
                        }) {
                            Icon("sign-out")
                            Text("Sign-out")
                        }
                    }
                    apply(loadingState, dimmerAttrs = { v.inverted() }, loaderAttrs = { v.size(Mini) })
                }
                when (session.userInfo.size) {
                    0 -> {
                        Item {
                            Header { Em { Text("Empty") } }
                        }
                    }

                    else -> {
                        session.userInfo.forEach { (key, value) ->
                            Item {
                                S("ui", "horizontal", "label") { Text(key) }
                                Text(value.toString())
                            }
                        }
                    }
                }
            }
        }
    }
}
