package com.bkahlert.hello.fritz2.app.env

import com.bkahlert.hello.fritz2.components.dataView
import com.bkahlert.hello.fritz2.lensForKey
import com.bkahlert.hello.fritz2.lensForValue
import com.bkahlert.hello.fritz2.mapEntries
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Store
import dev.fritz2.core.lensOf
import dev.fritz2.core.storeOf

public fun RenderContext.environmentView(
    environment: Environment,
): Unit = environmentView(storeOf(environment))

public fun RenderContext.environmentView(
    environment: Store<Environment>,
) {
    dataView(
        name = "Environment",
        store = environment
            .map(
                lensOf(
                    id = "properties",
                    getter = { it.toMap() },
                    setter = { p, _ -> p },
                )
            )
            .mapEntries(),
        lenses = listOf(
            lensForKey(),
            lensForValue(),
        ),
    )
}
