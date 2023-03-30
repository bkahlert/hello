@file:Suppress("RedundantVisibilityModifier")

package playground.components.props

import com.bkahlert.hello.fritz2.components.Page
import com.bkahlert.hello.fritz2.components.heroicons.HeroIcons
import com.bkahlert.hello.fritz2.components.showcase.showcase
import com.bkahlert.hello.fritz2.components.showcase.showcases
import com.bkahlert.hello.props.demo.InMemoryPropsDataSource
import com.bkahlert.hello.props.domain.Props
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

public val PropsShowcases = Page(
    "props",
    "Props",
    "Props showcases",
    heroIcon = HeroIcons::adjustments_horizontal,
) {
    showcases("PropsView") {
        showcase("Loading") {
            propsView(null)
        }
        showcase("Empty") {
            propsView(Props.EMPTY)
        }
        showcase("Filled") {
            propsView(
                Props(
                    buildJsonObject {
                        put("foo", buildJsonObject { put("bar", JsonPrimitive(42)) })
                        put("baz", JsonNull)
                    }
                ),
            )
        }
        showcase("Dynamically filled") {
            val repository = PropsStore(
                InMemoryPropsDataSource(
                    Props(
                        buildJsonObject {
                            put("foo", buildJsonObject { put("bar", JsonPrimitive(42)) })
                            put("baz", JsonNull)
                        }
                    ),
                ),
            )
            repository.data.render { props ->
                propsView(
                    props,
                    onUpdate = { id, value ->
                        repository.setProp(id to value)
                    },
                    onDelete = {
                        repository.removeProp(it)
                    },
                )
            }
        }
    }
}
