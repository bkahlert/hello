package com.bkahlert.hello.clickup

import kotlinx.serialization.Serializable

@Serializable
data class Team(
    val id: Int,
    val name: String,
    val color: String,
    val avatar: String,
    val members: List<User>,
)
