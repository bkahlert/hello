package com.bkahlert.hello.clickup.model

import com.bkahlert.hello.clickup.serialization.Named
import com.bkahlert.hello.color.Color
import com.bkahlert.kommons.net.Uri
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Team(
    @SerialName("id") val id: TeamID,
    @SerialName("name") val name: String,
    @SerialName("color") val color: Color,
    @SerialName("avatar") val avatar: Uri,
    @SerialName("members") val members: List<Named<User>>,
)

@Serializable public value class TeamID(override val id: String) : Identifier<String>
