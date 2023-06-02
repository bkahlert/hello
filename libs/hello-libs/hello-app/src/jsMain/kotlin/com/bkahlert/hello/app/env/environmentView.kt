package com.bkahlert.hello.app.env

import com.bkahlert.hello.components.dataView
import com.bkahlert.hello.fritz2.lensForFirst
import com.bkahlert.hello.fritz2.lensForSecond
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
                    getter = { it.toList() },
                    setter = { p, _ -> p },
                )
            ),
        lenses = listOf(
            lensForFirst(),
            lensForSecond(),
        ),
    )
}
