package com.bkahlert.hello.app.props

import com.bkahlert.hello.icon.heroicons.HeroIcons
import com.bkahlert.hello.page.SimplePage
import com.bkahlert.hello.showcase.showcase
import com.bkahlert.hello.showcase.showcases
import kotlinx.browser.localStorage
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive

public val PropsShowcases: SimplePage = SimplePage(
    "props",
    "Props",
    "Props showcases",
    heroIcon = HeroIcons::adjustments_horizontal,
) {
    showcases("PropsView") {
        showcase("Empty") {
            propsView(PropsStore())
        }
        showcase("Filled") {
            propsView(PropsStore(mapOf("foo" to JsonPrimitive(42), "bar" to JsonNull)))
        }
        showcase("LocalStorage") {
            propsView(PropsStore(emptyMap(), StoragePropsDataSource(localStorage)).apply { init() })
        }
    }
}
