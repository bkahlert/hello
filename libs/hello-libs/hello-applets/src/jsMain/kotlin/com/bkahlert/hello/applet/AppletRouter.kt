package com.bkahlert.hello.applet

import dev.fritz2.core.Store
import dev.fritz2.routing.Route
import dev.fritz2.routing.Router

public class AppletRouter(
    public val store: Store<List<Applet>>,
) : Router<AppletRoute.Current?>(AppletRoute(store))

public class AppletRoute(
    private val store: Store<List<Applet>>,
) : Route<AppletRoute.Current?> {
    override val default: Current? get() = store.current.firstOrNull()?.let { Current(it, false) }

    override fun deserialize(hash: String): Current? = hash.split("/").let { segments ->
        store.current.firstOrNull { it.id == segments[0] }?.let { applet ->
            Current(applet, segments.size > 1 && segments[1] == "edit")
        }
    }

    override fun serialize(route: Current?): String = listOfNotNull(route?.applet?.id, if (route?.edit == true) "edit" else null).joinToString("/")

    public data class Current(val applet: Applet, val edit: Boolean)
}
