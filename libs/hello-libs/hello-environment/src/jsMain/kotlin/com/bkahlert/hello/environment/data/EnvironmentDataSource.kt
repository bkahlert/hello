package com.bkahlert.hello.environment.data

import com.bkahlert.hello.environment.domain.Environment

public fun interface EnvironmentDataSource {
    public suspend fun load(): Environment
}
