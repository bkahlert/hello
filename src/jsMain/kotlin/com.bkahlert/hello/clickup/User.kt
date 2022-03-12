@file:UseSerializers(UrlSerializer::class)

package com.bkahlert.hello.clickup

import com.bkahlert.kommons.Color
import com.bkahlert.kommons.serialization.UrlSerializer
import io.ktor.http.Url
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class User(
    @SerialName("id") val id: Int,
    @SerialName("username") val username: String,
    @SerialName("email") val email: String,
    @SerialName("color") val color: Color?,
    @SerialName("profilePicture") val profilePicture: Url,
    @SerialName("initials") val initials: String,
    @SerialName("week_start_day") val weekStartDay: Int?,
    @SerialName("global_font_support") val globalFontSupport: Boolean?,
    @SerialName("timezone") val timezone: String?,
) {
    @Serializable
    data class Preview(
        @SerialName("color") val color: Color,
        @SerialName("username") val username: String,
        @SerialName("initials") val initials: String?,
        @SerialName("profilePicture") val profilePicture: Url,
    )
}

typealias Creator = User.Preview
typealias Assignee = User.Preview
typealias Watcher = User.Preview
