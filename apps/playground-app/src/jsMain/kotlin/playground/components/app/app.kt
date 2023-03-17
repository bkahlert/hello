@file:Suppress("RedundantVisibilityModifier")

package playground.components.app

import com.bkahlert.hello.clickup.model.fixtures.ImageFixtures
import dev.fritz2.core.RenderContext
import dev.fritz2.core.alt
import dev.fritz2.core.classes
import dev.fritz2.core.src
import playground.components.loader
import playground.components.props.propsView
import playground.components.user.userDropdown
import playground.fritz2.ContentBuilder

public fun RenderContext.app(
    store: AppStore,
    content: ContentBuilder? = null,
) {
    appBar("mb-4") { userDropdown(store.userStore) }
    store.props.render {
        if (it == null) {
            loader()
        } else {
            if (content != null) {
                content.invoke(this)
                hr { }
            }
            propsView(it)
        }
    }
}

fun RenderContext.appBar(
    classes: String? = null,
    endContent: ContentBuilder? = null,
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
                        img("shrink-0 block h-8 w-auto") {
                            src(ImageFixtures.HelloFavicon.toString())
                            alt("Hello!")
                        }
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
