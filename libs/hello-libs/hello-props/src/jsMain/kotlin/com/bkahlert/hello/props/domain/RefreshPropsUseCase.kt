package com.bkahlert.hello.props.domain

import com.bkahlert.kommons.js.ConsoleLogging
import com.bkahlert.kommons.js.grouping
import kotlinx.coroutines.flow.first

public class RefreshPropsUseCase(
    private val getPropsRepositoryUseCase: GetPropsRepositoryUseCase,
) {
    private val logger by ConsoleLogging

    public suspend operator fun invoke(): Props? = logger.grouping(::invoke) {
        getPropsRepositoryUseCase().first()?.refreshProps()
    }
}
