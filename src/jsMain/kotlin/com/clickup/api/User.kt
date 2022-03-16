@file:UseSerializers(UrlSerializer::class)

package com.clickup.api

import com.bkahlert.kommons.Color
import com.bkahlert.kommons.serialization.UrlSerializer
import com.clickup.api.User.Preview
import com.clickup.api.rest.Identifier
import io.ktor.http.Url
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class User(
    @SerialName("id") val id: ID,
    @SerialName("username") val username: String,
    @SerialName("email") val email: String,
    @SerialName("color") val color: Color?,
    @SerialName("profilePicture") val profilePicture: Url,
    @SerialName("initials") val initials: String,
    @SerialName("week_start_day") val weekStartDay: Int?,
    @SerialName("global_font_support") val globalFontSupport: Boolean?,
    @SerialName("timezone") val timezone: String?,
) {
    @Serializable value class ID(override val id: Int) : Identifier<Int>

    @Serializable
    data class Preview(
        @SerialName("id") val id: ID? = null,
        @SerialName("color") val color: Color,
        @SerialName("username") val username: String,
        @SerialName("initials") val initials: String?,
        @SerialName("profilePicture") val profilePicture: Url,
    )
}

typealias Creator = Preview
typealias Assignee = Preview
typealias Watcher = Preview
