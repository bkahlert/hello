@file:UseSerializers(DateAsMillisecondsSerializer::class, DurationAsMillisecondsSerializer::class, UrlSerializer::class)

package com.bkahlert.hello.clickup.api

import com.bkahlert.hello.color.Color
import com.bkahlert.hello.url.URL
import com.bkahlert.hello.url.UrlSerializer
import com.bkahlert.kommons.serialization.DateAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.DurationAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.Named
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class Team(
    @SerialName("id") val id: TeamID,
    @SerialName("name") val name: String,
    @SerialName("color") val color: Color,
    @SerialName("avatar") val avatar: URL,
    @SerialName("members") val members: List<Named<User>>,
)

@Serializable value class TeamID(override val id: String) : Identifier<String>
