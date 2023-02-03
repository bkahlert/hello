package com.bkahlert.hello.session.data

import com.bkahlert.kommons.auth.Session

public fun interface SessionDataSource {
    public suspend fun load(): Session
}
