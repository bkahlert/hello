@file:Suppress("RedundantVisibilityModifier")

package playground.components.app

import com.bkahlert.hello.clickup.model.fixtures.ImageFixtures
import com.bkahlert.hello.fritz2.ContentBuilder
import com.bkahlert.hello.fritz2.app.AppState
import com.bkahlert.hello.fritz2.app.AppStore
import com.bkahlert.hello.fritz2.app.props.propsView
import com.bkahlert.hello.fritz2.app.user.userDropdown
import com.bkahlert.hello.fritz2.components.icon
import dev.fritz2.core.RenderContext
import dev.fritz2.core.classes
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement

public fun RenderContext.app(
    appStore: AppStore,
    content: ContentBuilder<Element>? = null,
) {
    appStore.data.render { state ->
        when (state) {
            is AppState.Loading -> landingScreen()
            is AppState.Loaded -> {
                appBar("mb-4") { userDropdown(state.session) }
                if (content != null) {
                    content.invoke(this)
                    hr { }
                }
                propsView(state.props)
            }
        }
    }
}

fun RenderContext.appBar(
    classes: String? = null,
    endContent: ContentBuilder<HTMLDivElement>? = null,
) {
    nav(classes) {
        div(
            classes(
                "mx-auto max-w-7xl",
                "px-2 sm:px-6 lg:px-8",
                "rounded-xl shadow-lg shadow-inner bg-white/5",
            )
        ) {
            div("relative flex h-16 items-center justify-between") {
                div("flex flex-1 items-center justify-center sm:items-stretch sm:justify-start") {
                    div("flex flex-shrink-0 items-center") {
                        icon("shrink-0 block h-8 w-auto", ImageFixtures.HelloFavicon)
                    }
                }

                if (endContent != null) {
                    div("absolute inset-y-0 right-0 pr-2 flex items-center sm:static sm:inset-auto sm:ml-6 sm:pr-0") {
                        endContent()
                    }
                }
            }
        }
    }
}
