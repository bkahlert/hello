@file:UseSerializers(DateAsMillisecondsSerializer::class, DurationAsMillisecondsSerializer::class, UrlSerializer::class)

package com.bkahlert.hello.clickup.api

import com.bkahlert.kommons.color.Color
import com.bkahlert.kommons.dom.URL
import com.bkahlert.kommons.serialization.DateAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.DurationAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.UrlSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class User(
    @SerialName("id") val id: UserID,
    @SerialName("username") val username: String,
    @SerialName("email") val email: String,
    @SerialName("color") val color: Color,
    @SerialName("profilePicture") val profilePicture: URL,
    @SerialName("initials") val initials: String,
    @SerialName("week_start_day") val weekStartDay: Int?,
    @SerialName("global_font_support") val globalFontSupport: Boolean?,
    @SerialName("timezone") val timezone: String?,
)

@Serializable value class UserID(override val id: Int) : Identifier<Int>

@Serializable
data class UserPreview(
    @SerialName("id") val id: UserID? = null,
    @SerialName("color") val color: Color,
    @SerialName("username") val username: String,
    @SerialName("initials") val initials: String?,
    @SerialName("profilePicture") val profilePicture: URL,
)

fun User.asPreview() = UserPreview(id, color, username, initials, profilePicture)

typealias Creator = UserPreview

fun User.asCreator() = asPreview()

typealias Assignee = UserPreview

fun User.asAssignee() = asPreview()

typealias Watcher = UserPreview

fun User.asWatcher() = asPreview()
