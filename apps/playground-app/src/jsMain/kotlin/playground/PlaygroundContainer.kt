package playground

import com.bkahlert.hello.fritz2.components.Page
import com.bkahlert.hello.fritz2.components.heroicons.HeroIcons
import com.bkahlert.hello.fritz2.components.proseBox

val PlaygroundContainer = Page(
    id = "playground",
    label = "Playground",
    description = "A place to play around with UI elements",
    heroIcon = HeroIcons::beaker,
) {
    proseBox("hello")
}
