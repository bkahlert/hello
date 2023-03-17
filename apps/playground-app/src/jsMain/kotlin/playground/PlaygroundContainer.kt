package playground

import playground.components.Page
import playground.components.proseBox
import playground.tailwind.heroicons.HeroIcons

val PlaygroundContainer = Page(
    id = "playground",
    label = "Playground",
    description = "A place to play around with UI elements",
    heroIcon = HeroIcons::beaker,
) {
    proseBox("hello")
}
