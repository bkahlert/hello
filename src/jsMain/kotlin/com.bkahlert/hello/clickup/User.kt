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
    val id: Int,
    val username: String,
    val email: String,
    val color: Color?,
    val profilePicture: Url?,
    @SerialName("week_start_day") val weekStartDay: Int?,
    @SerialName("global_font_support") val globalFontSupport: Boolean?,
    val timezone: String?,
) {
    fun box(): BoxedUser = BoxedUser(this)
}
