@file:UseSerializers(DateAsMillisecondsSerializer::class, DurationAsMillisecondsSerializer::class, UrlSerializer::class)

package com.clickup.api

import com.bkahlert.kommons.Color
import com.bkahlert.kommons.serialization.DateAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.DurationAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.Named
import com.bkahlert.kommons.serialization.UrlSerializer
import com.clickup.api.rest.Identifier
import io.ktor.http.Url
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class Team(
    @SerialName("id") val id: ID,
    @SerialName("name") val name: String,
    @SerialName("color") val color: Color,
    @SerialName("avatar") val avatar: Url,
    @SerialName("members") val members: List<Named<User>>,
) {
    @Serializable value class ID(override val id: String) : Identifier<String>
}
