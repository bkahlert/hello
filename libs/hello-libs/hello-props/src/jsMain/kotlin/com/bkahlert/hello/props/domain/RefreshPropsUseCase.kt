package com.bkahlert.hello.props.domain

import com.bkahlert.kommons.js.grouping
import kotlinx.coroutines.flow.first

public class RefreshPropsUseCase(
    private val getPropsRepositoryUseCase: GetPropsRepositoryUseCase,
) {
    public suspend operator fun invoke(): Props? {
        val refreshedProps: suspend () -> Props? = {
            getPropsRepositoryUseCase().first()?.refreshProps()
        }
        return console.grouping(RefreshPropsUseCase::class.simpleName!!, block = refreshedProps)
    }
}
