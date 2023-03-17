@file:Suppress("RedundantVisibilityModifier")

package playground.components.session

import com.bkahlert.kommons.auth.Session
import com.bkahlert.kommons.auth.Session.AuthorizedSession
import com.bkahlert.kommons.auth.Session.UnauthorizedSession
import dev.fritz2.core.RenderContext
import dev.fritz2.core.storeOf
import playground.components.dataView
import com.bkahlert.hello.fritz2.icon
import playground.tailwind.heroicons.OutlineHeroIcons

public fun RenderContext.sessionView(
    session: Session?,
    onReauthorize: (() -> Unit)? = null,
    onAuthorize: (() -> Unit)? = null,
    onUnauthorize: (() -> Unit)? = null,
) {
    dataView(
        "Session",
        session?.let {
            storeOf(
                when (it) {
                    is UnauthorizedSession -> listOf("Status" to "Unauthorized")
                    is AuthorizedSession -> listOf("Status" to "Authorized") + it.userInfo.map { (key, value) -> key to value.toString() }
                }
            )
        },
        controls = {
            if (session != null) when (session) {
                is UnauthorizedSession -> {
                    if (onReauthorize != null) {
                        button("btn contrast-150 hue-rotate-90") {
                            icon("w-4 h-4", OutlineHeroIcons.arrow_path)
                            +"Re-authorize"
                            clicks handledBy { onReauthorize() }
                        }
                    }
                    if (onAuthorize != null) {
                        button("btn contrast-150 hue-rotate-90") {
                            icon("w-4 h-4", OutlineHeroIcons.arrow_right_on_rectangle)
                            +"Authorize"
                            clicks handledBy { onAuthorize() }
                        }
                    }
                }

                is AuthorizedSession -> {
                    if (onReauthorize != null) {
                        button("btn contrast-150 hue-rotate-90") {
                            icon("w-4 h-4", OutlineHeroIcons.arrow_path)
                            +"Refresh"
                            clicks handledBy { onReauthorize() }
                        }
                    }
                    if (onUnauthorize != null) {
                        button("btn contrast-150 hue-rotate-90") {
                            icon("w-4 h-4", OutlineHeroIcons.arrow_left_on_rectangle)
                            +"Un-authorize"
                            clicks handledBy { onUnauthorize() }
                        }
                    }
                }
            }
        },
    )
}
