@file:Suppress("RedundantVisibilityModifier")

package playground.components.props

import com.bkahlert.hello.props.domain.Props
import com.bkahlert.kommons.json.LenientAndPrettyJson
import dev.fritz2.core.RenderContext
import dev.fritz2.core.storeOf
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import playground.components.dataView

public fun RenderContext.propsView(
    props: Props? = null,
    onUpdate: ((String, JsonObject) -> Unit)? = null,
    onDelete: ((String) -> Unit)? = null,
) {
    dataView(
        "Props",
        props?.let {
            storeOf(it.content.mapValues { prop -> LenientAndPrettyJson.encodeToString(prop.value) })
        }
    )
}
