package com.bkahlert.hello.widget

import dev.fritz2.core.Store
import dev.fritz2.routing.Route
import dev.fritz2.routing.Router

public class WidgetRouter(
    public val store: Store<List<Widget>>,
) : Router<WidgetRoute.Current?>(WidgetRoute(store))

public class WidgetRoute(
    private val store: Store<List<Widget>>,
) : Route<WidgetRoute.Current?> {
    override val default: Current? get() = store.current.firstOrNull()?.let { Current(it, false) }

    override fun deserialize(hash: String): Current? = hash.split("/").let { segments ->
        store.current.firstOrNull { it.id == segments[0] }?.let { widget ->
            Current(widget, segments.size > 1 && segments[1] == "edit")
        }
    }

    override fun serialize(route: Current?): String = listOfNotNull(route?.widget?.id, if (route?.edit == true) "edit" else null).joinToString("/")

    public data class Current(val widget: Widget, val edit: Boolean)
}
