@file:Suppress("RedundantVisibilityModifier")

package com.bkahlert.hello.fritz2.app.props

import com.bkahlert.hello.fritz2.components.Page
import com.bkahlert.hello.fritz2.components.heroicons.HeroIcons
import com.bkahlert.hello.fritz2.components.showcase.showcase
import com.bkahlert.hello.fritz2.components.showcase.showcases
import kotlinx.browser.localStorage
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive

public val PropsShowcases: Page = Page(
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
