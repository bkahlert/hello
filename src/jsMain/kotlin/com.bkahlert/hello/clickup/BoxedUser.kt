package com.bkahlert.hello.clickup

import kotlinx.serialization.Serializable

@Serializable
data class BoxedUser(
    val user: User,
) {
    fun unbox(): User = user
}
