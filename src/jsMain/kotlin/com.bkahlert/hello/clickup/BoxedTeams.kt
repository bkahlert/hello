package com.bkahlert.hello.clickup

import kotlinx.serialization.Serializable

@Serializable
data class BoxedTeams(
    val teams: List<Team>,
) {
    fun unbox(): List<Team> = teams
}
