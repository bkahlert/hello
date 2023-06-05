package playground.components.app

import com.bkahlert.hello.icon.assets.Images.HelloFavicon
import com.bkahlert.hello.icon.icon
import dev.fritz2.core.RenderContext

fun RenderContext.landingScreen() {
    div("w-full h-full flex items-center justify-center") {
        icon("w-full h-auto max-w-16 m-8 animate-ping", HelloFavicon)
    }
}
