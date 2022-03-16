@file:UseSerializers(UrlSerializer::class)

package com.clickup.api

import com.bkahlert.kommons.Color
import com.bkahlert.kommons.serialization.UrlSerializer
import com.clickup.api.User.ID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class Tag(
    @SerialName("name") val name: String,
    @SerialName("tag_fg") val tagForeground: Color? = null,
    @SerialName("tag_bg") val tagBackground: Color? = null,
    @SerialName("creator") val creator: ID? = null,
) {
    val foregroundColor: Color by lazy { tagForeground ?: Color(0x000000) }
    val backgroundColor: Color by lazy { tagBackground ?: Color(0x000000) }
}
