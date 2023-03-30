@file:Suppress("RedundantVisibilityModifier")

package com.bkahlert.hello.fritz2.app.session

import com.bkahlert.hello.fritz2.components.dataView
import com.bkahlert.hello.fritz2.components.heroicons.OutlineHeroIcons
import com.bkahlert.hello.fritz2.components.icon
import com.bkahlert.kommons.auth.Session.AuthorizedSession
import com.bkahlert.kommons.auth.Session.UnauthorizedSession
import dev.fritz2.core.RenderContext
import dev.fritz2.core.lensOf
import kotlinx.coroutines.flow.map

public fun RenderContext.sessionView(
    store: SessionStore,
) {
    dataView(
        "Session",
        store = store.map(
            lensOf(
                id = "claims",
                getter = { (it as? AuthorizedSession)?.userInfo?.toList() ?: emptyList() },
                setter = { p, _ -> p },
            )
        ),
        lenses = listOf(
            lensOf("key", { it.first }) { p, _ -> p },
            lensOf("value", { it.second.toString() }, { p, _ -> p }),
        ),
        controls = {
            store.data.render { session ->
                when (session) {
                    is UnauthorizedSession -> {
                        button("btn contrast-150 hue-rotate-90") {
                            icon("w-4 h-4", OutlineHeroIcons.arrow_path)
                            +"Re-authorize"
                            clicks.map { false } handledBy store.reauthorize
                        }
                        button("btn contrast-150 hue-rotate-90") {
                            icon("w-4 h-4", OutlineHeroIcons.arrow_right_on_rectangle)
                            +"Authorize"
                            clicks handledBy store.authorize
                        }
                    }

                    is AuthorizedSession -> {
                        button("btn contrast-150 hue-rotate-90") {
                            icon("w-4 h-4", OutlineHeroIcons.arrow_path)
                            +"Refresh"
                            clicks.map { false } handledBy store.reauthorize
                        }
                        button("btn contrast-150 hue-rotate-90") {
                            icon("w-4 h-4", OutlineHeroIcons.arrow_left_on_rectangle)
                            +"Un-authorize"
                            clicks handledBy store.unauthorize
                        }
                    }
                }
            }
        },
    )
}
