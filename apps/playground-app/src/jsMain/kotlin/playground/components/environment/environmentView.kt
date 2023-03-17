@file:Suppress("RedundantVisibilityModifier")

package playground.components.environment

import com.bkahlert.hello.environment.domain.Environment
import dev.fritz2.core.RenderContext
import dev.fritz2.core.storeOf
import playground.components.dataView

public fun RenderContext.environmentView(
    environment: Environment?,
) {
    dataView("Environment", environment?.let { storeOf(it.toList()) })
}
