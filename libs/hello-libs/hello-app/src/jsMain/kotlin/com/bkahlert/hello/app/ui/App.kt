package com.bkahlert.hello.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import com.bkahlert.hello.props.ui.PropsView
import com.bkahlert.hello.user.ui.UserMenu
import com.bkahlert.kommons.takeUnlessEmpty
import com.bkahlert.kommons.text.capitalize
import com.bkahlert.semanticui.collection.Item
import com.bkahlert.semanticui.collection.LinkItem
import com.bkahlert.semanticui.collection.Menu
import com.bkahlert.semanticui.collection.Message
import com.bkahlert.semanticui.collection.attached
import com.bkahlert.semanticui.collection.borderless
import com.bkahlert.semanticui.collection.info
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Attached.Bottom
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.LineLength
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.custom.ErrorMessage
import com.bkahlert.semanticui.custom.LoadingState
import com.bkahlert.semanticui.element.Button
import com.bkahlert.semanticui.element.Buttons
import com.bkahlert.semanticui.element.Header
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.Item
import com.bkahlert.semanticui.element.Line
import com.bkahlert.semanticui.element.Loader
import com.bkahlert.semanticui.element.Placeholder
import com.bkahlert.semanticui.element.Segment
import com.bkahlert.semanticui.element.SegmentElement
import com.bkahlert.semanticui.element.active
import com.bkahlert.semanticui.element.lineLength
import com.bkahlert.semanticui.element.loading
import org.jetbrains.compose.web.css.AnimationTimingFunction
import org.jetbrains.compose.web.css.duration
import org.jetbrains.compose.web.css.s
import org.jetbrains.compose.web.css.transitions
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import com.bkahlert.semanticui.element.List as SList

@Composable
public fun App(
    viewModel: AppViewModel = rememberAppViewModel(),
    content: SemanticContentBuilder<SegmentElement>? = null,
) {

    val uiState: State<AppViewModelState> = viewModel.uiState.collectAsState()
    val propsState = viewModel.props.collectAsState()

    Menu({ v.borderless() }) {
        Item { Img(HelloImageFixtures.HelloMark.toString()) }
        when (val state = uiState.value) {
            is AppViewModelState.Loading -> Menu({ classes("right") }) {
                Item {
                    Loader(state.models.takeUnlessEmpty()?.let { "Loading $it" }) { s.active() }
                }
            }

            is AppViewModelState.Loaded -> UserMenu(
                user = state.user,
                onSignIn = viewModel::authorize,
                onSignOut = viewModel::unauthorize,
                {
                    LinkItem({ onClick { viewModel.reauthorize() } }) {
                        Icon("sync")
                        Text("Refresh")
                    }
                },
                attrs = { classes("right") }
            )

            is AppViewModelState.Failed -> Menu({ classes("right") }) {
                Item {
                    Buttons {
                        Button({
                            onClick { viewModel.reauthorize() }
                        }) {
                            Icon("eraser")
                            Text("Reset")
                        }
                    }
                }
            }
        }
    }

    Segment({
        if (uiState.value is AppViewModelState.Loading) s.loading()
        style {
            transitions {
                "all" {
                    duration(0.4.s)
                    timingFunction = AnimationTimingFunction.Ease
                }
            }
        }
    }) {
        when (val state = uiState.value) {
            is AppViewModelState.Loading -> SList {
                repeat(2) {
                    Item {
                        Placeholder {
                            Line({ v.lineLength(LineLength.Short) })
                            Line()
                        }
                    }
                }
            }

            is AppViewModelState.Loaded -> {
                Header { Text("Props") }
                when (val props = propsState.value) {
                    null -> PropsView(loadingState = LoadingState.On)
                    else -> PropsView(props)
                }

                content?.invoke(this)
            }

            is AppViewModelState.Failed -> {
                Header { Text(state.operation.capitalize() + " failed") }
                ErrorMessage(state.cause, state.operation.capitalize() + " failed") {
                    val details = state.cause.message
                    if (details != null) {
                        P { Text(details) }
                    }
                }
            }
        }
    }

    Message({ v.attached(Bottom).info() }) {
        Icon("info")
        S("ui", "label") {
            Text("State")
            S("detail") {
                Text(uiState.value::class.simpleName ?: "?")
            }
        }
    }
}
