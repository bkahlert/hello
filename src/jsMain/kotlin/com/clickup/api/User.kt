@file:UseSerializers(DateAsMillisecondsSerializer::class, DurationAsMillisecondsSerializer::class, UrlSerializer::class)

package com.clickup.api

import com.bkahlert.kommons.Color
import com.bkahlert.kommons.asString
import com.bkahlert.kommons.dom.URL
import com.bkahlert.kommons.serialization.DateAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.DurationAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.UrlSerializer
import com.bkahlert.kommons.text.truncate
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
) {
    override fun toString(): String = asString {
        ::id.name to id
        ::username.name to username
        ::email.name to email
        ::color.name to color
        ::profilePicture.name to profilePicture.toString().truncate()
        ::initials.name to initials
        ::weekStartDay.name to weekStartDay
        ::globalFontSupport.name to globalFontSupport
        ::timezone.name to timezone
    }
}

@Serializable value class UserID(override val id: Int) : Identifier<Int>

@Serializable
data class UserPreview(
    @SerialName("id") val id: UserID? = null,
    @SerialName("color") val color: Color,
    @SerialName("username") val username: String,
    @SerialName("initials") val initials: String?,
    @SerialName("profilePicture") val profilePicture: URL,
) {
    override fun toString(): String = asString {
        ::id.name to id
        ::username.name to username
        ::color.name to color
        ::profilePicture.name to profilePicture.toString().truncate()
        ::initials.name to initials
    }
}

fun User.asPreview() = UserPreview(id, color, username, initials, profilePicture)

typealias Creator = UserPreview

fun User.asCreator() = asPreview()

typealias Assignee = UserPreview

fun User.asAssignee() = asPreview()

typealias Watcher = UserPreview

fun User.asWatcher() = asPreview()
