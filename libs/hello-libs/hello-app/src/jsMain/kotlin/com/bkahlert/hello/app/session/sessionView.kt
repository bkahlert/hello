@file:Suppress("RedundantVisibilityModifier")

package com.bkahlert.hello.app.session

import com.bkahlert.hello.button.button
import com.bkahlert.hello.components.dataView
import com.bkahlert.hello.icon.heroicons.OutlineHeroIcons
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
                        button(OutlineHeroIcons.arrow_path, "Re-authorize", simple = true)
                            .clicks.map { false } handledBy store.reauthorize
                        button(OutlineHeroIcons.arrow_right_on_rectangle, "Authorize", simple = true)
                            .clicks handledBy store.authorize
                    }

                    is AuthorizedSession -> {
                        button(OutlineHeroIcons.arrow_path, "Refresh", simple = true)
                            .clicks.map { false } handledBy store.reauthorize
                        button(OutlineHeroIcons.arrow_left_on_rectangle, "Un-authorize", simple = true)
                            .clicks handledBy store.unauthorize
                    }
                }
            }
        },
    )
}
