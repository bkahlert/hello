@file:Suppress("RedundantVisibilityModifier")

package playground.components.app

import com.bkahlert.hello.app.ui.HelloImageFixtures
import dev.fritz2.core.RenderContext
import com.bkahlert.hello.fritz2.icon

public fun RenderContext.landingScreen() {
    div("w-full h-full flex items-center justify-center") {
        icon("w-full h-auto max-w-16 m-8 animate-ping", HelloImageFixtures.HelloFavicon)
    }
}
