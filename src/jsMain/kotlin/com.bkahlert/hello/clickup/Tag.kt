@file:UseSerializers(UrlSerializer::class)

package com.bkahlert.hello.clickup

import com.bkahlert.kommons.Color
import com.bkahlert.kommons.serialization.UrlSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class Tag(
    @SerialName("name") val name: String,
    @SerialName("tag_fg") val color: Color,
    @SerialName("tag_bg") val backgroundColor: Color,
)
