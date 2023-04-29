package com.bkahlert.hello.fritz2.components

import com.bkahlert.kommons.js.ConsoleLogging
import com.bkahlert.kommons.js.grouping
import dev.fritz2.routing.Route
import dev.fritz2.routing.Router

public class PageRouter(
    public val pages: List<Page>,
) : Router<Page?>(PageRoute(pages)) {
    public constructor(vararg pages: Page) : this(pages.asList())
}

public class PageRoute(
    private val pages: List<Page>,
) : Route<Page?> {
    private val logger by ConsoleLogging

    override val default: Page? = null

    override fun deserialize(hash: String): Page? = logger.grouping(::deserialize) {
        hash.split("/").fold(null) { parent: ParentPage?, id ->
            (parent?.pages ?: pages)
                .firstOrNull { it.id == id }
                .let { if (it is ParentPage) it else return it }
        }
    }

    override fun serialize(route: Page?): String = logger.grouping(::serialize) {
        logger.warn("Route", route)
        route?.path.orEmpty().joinToString("/") { it.id }
    }
}
