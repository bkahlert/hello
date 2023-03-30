package playground

import com.bkahlert.hello.fritz2.app.props.PropsStore
import com.bkahlert.hello.fritz2.app.props.StoragePropsDataSource
import com.bkahlert.hello.fritz2.app.props.propsView
import com.bkahlert.hello.fritz2.components.Page
import com.bkahlert.hello.fritz2.components.heroicons.HeroIcons
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive

val PlaygroundContainer = Page(
    id = "playground",
    label = "Playground",
    description = "A place to play around with UI elements",
    heroIcon = HeroIcons::beaker,
) {

    val propsStore: PropsStore = PropsStore(
        mapOf(
            "foo" to JsonPrimitive("bar"),
            "baz" to JsonArray(listOf(JsonPrimitive("qux"), JsonPrimitive("pokkkkkkkkkkklöklölök"))),
        ),
        StoragePropsDataSource.InMemoryPropsDataSource(),
    )

    propsView(propsStore)
}
