package com.bkahlert.hello.props.domain

import com.bkahlert.hello.props.data.PropsRepository

public class RemovePropUseCase(
    private val repository: PropsRepository,
) {
    public operator fun invoke(id: String) {
        repository.removeProp(id)
    }
}
