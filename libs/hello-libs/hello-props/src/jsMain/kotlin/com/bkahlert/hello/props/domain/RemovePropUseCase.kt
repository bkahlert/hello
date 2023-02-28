package com.bkahlert.hello.props.domain

import com.bkahlert.kommons.js.ConsoleLogging
import com.bkahlert.kommons.js.grouping
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.JsonElement

public class RemovePropUseCase(
    private val getPropsRepositoryUseCase: GetPropsRepositoryUseCase,
) {
    private val logger by ConsoleLogging

    public suspend operator fun invoke(id: String): JsonElement? = logger.grouping(::invoke) {
        getPropsRepositoryUseCase().first()?.removeProp(id)
    }
}
