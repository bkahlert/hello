package com.bkahlert.hello.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.bkahlert.hello.data.Resource.Failure
import com.bkahlert.hello.data.Resource.Success
import com.bkahlert.hello.props.ui.PropsView
import com.bkahlert.hello.user.ui.UserMenu
import com.bkahlert.semanticui.collection.Item
import com.bkahlert.semanticui.collection.Menu
import com.bkahlert.semanticui.collection.Message
import com.bkahlert.semanticui.collection.attached
import com.bkahlert.semanticui.collection.borderless
import com.bkahlert.semanticui.collection.info
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Attached.Bottom
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Mini
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.element.Header
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.ImageHeader
import com.bkahlert.semanticui.element.Line
import com.bkahlert.semanticui.element.Loader
import com.bkahlert.semanticui.element.Paragraph
import com.bkahlert.semanticui.element.Placeholder
import com.bkahlert.semanticui.element.Segment
import com.bkahlert.semanticui.element.SegmentElement
import com.bkahlert.semanticui.element.active
import com.bkahlert.semanticui.element.inline
import com.bkahlert.semanticui.element.loading
import com.bkahlert.semanticui.element.size
import org.jetbrains.compose.web.css.AnimationTimingFunction
import org.jetbrains.compose.web.css.duration
import org.jetbrains.compose.web.css.s
import org.jetbrains.compose.web.css.transitions
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Text

@Composable
public fun App(
    viewModel: AppViewModel = rememberAppViewModel(),
    content: SemanticContentBuilder<SegmentElement>? = null,
) {

    val userResource by viewModel.user.collectAsState()
    val propsResource by viewModel.props.collectAsState()

    Menu({ v.borderless() }) {
        Item { Img(HelloImageFixtures.HelloMark.toString()) }
        when (val resource = userResource) {
            null -> Menu({ classes("right") }) { Item { Loader("Loading user") { v.size(Mini).inline(); s.active() } } }

            is Success -> UserMenu(
                user = resource.data,
                onSignIn = viewModel::authorize,
                onReauthorize = { viewModel.reauthorize(force = false) },
                onSignOut = viewModel::unauthorize,
                attrs = { classes("right") },
            )

            is Failure -> UserMenu(
                userResource = resource,
                onReauthorize = { viewModel.reauthorize(force = false) },
                onForceReauthorize = { viewModel.reauthorize(force = true) },
                attrs = { classes("right") },
            )
        }
    }

    Segment({
        if (userResource == null) s.loading()
        style {
            transitions {
                "all" {
                    duration(0.4.s)
                    timingFunction = AnimationTimingFunction.Ease
                }
            }
        }
    }) {
        if (userResource == null) {
            Placeholder {
                Header()
                Line()
                Line()
                ImageHeader()
                Paragraph()
            }
        } else {
            Header { Text("Props") }
            PropsView(propsResource)

            content?.invoke(this)
        }

    }

    Message({ v.attached(Bottom).info() }) {
        Icon("info")
        S("ui", "label") {
            Text("State")
            S("detail") {
                Text(userResource?.let { it::class.simpleName } ?: "Loading")
            }
        }
    }
}
