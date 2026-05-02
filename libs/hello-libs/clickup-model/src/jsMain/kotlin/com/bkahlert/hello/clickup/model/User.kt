package com.bkahlert.hello.clickup.model

import com.bkahlert.kommons.color.Color
import com.bkahlert.kommons.uri.Uri
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class User(
    @SerialName("id") val id: UserID,
    @SerialName("username") val username: String,
    @SerialName("email") val email: String,
    @SerialName("color") val color: Color,
    @SerialName("profilePicture") val profilePicture: Uri,
    @SerialName("initials") val initials: String,
    @SerialName("week_start_day") val weekStartDay: Int?,
    @SerialName("global_font_support") val globalFontSupport: Boolean?,
    @SerialName("timezone") val timezone: String?,
)

@Serializable public value class UserID(override val id: Int) : Identifier<Int>

@Serializable
public data class UserPreview(
    @SerialName("id") val id: UserID? = null,
    @SerialName("color") val color: Color,
    @SerialName("username") val username: String,
    @SerialName("initials") val initials: String?,
    @SerialName("profilePicture") val profilePicture: Uri,
)

public fun User.asPreview(): UserPreview = UserPreview(id, color, username, initials, profilePicture)

public typealias Creator = UserPreview

public fun User.asCreator(): UserPreview = asPreview()

public typealias Assignee = UserPreview

public fun User.asAssignee(): UserPreview = asPreview()

public typealias Watcher = UserPreview

public fun User.asWatcher(): UserPreview = asPreview()
