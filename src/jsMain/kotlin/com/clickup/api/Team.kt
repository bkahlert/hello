@file:UseSerializers(DateAsMillisecondsSerializer::class, DurationAsMillisecondsSerializer::class, UrlSerializer::class)

package com.clickup.api

import com.bkahlert.kommons.asString
import com.bkahlert.kommons.color.Color
import com.bkahlert.kommons.dom.URL
import com.bkahlert.kommons.serialization.DateAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.DurationAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.Named
import com.bkahlert.kommons.serialization.UrlSerializer
import com.bkahlert.kommons.text.truncate
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
) {
    override fun toString(): String = asString {
        put(::id.name, id)
        put(::name.name, name)
        put(::color.name, color)
        put(::avatar.name, avatar.toString().truncate())
        put(::members.name, members)
    }
}

@Serializable value class TeamID(override val id: String) : Identifier<String>
