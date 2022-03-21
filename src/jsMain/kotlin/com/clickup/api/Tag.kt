@file:UseSerializers(DateAsMillisecondsSerializer::class, DurationAsMillisecondsSerializer::class, UrlSerializer::class)

package com.clickup.api

import com.bkahlert.kommons.Color
import com.bkahlert.kommons.coerceAtMost
import com.bkahlert.kommons.serialization.DateAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.DurationAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.UrlSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class Tag(
    @SerialName("name") val name: String,
    @SerialName("tag_fg") val tagForeground: Color? = null,
    @SerialName("tag_bg") val tagBackground: Color? = null,
    @SerialName("creator") val creator: UserID? = null,
) {
    val foregroundColor: Color by lazy { tagForeground ?: Color(0x000000) }
    val backgroundColor: Color by lazy { tagBackground ?: Color(0x000000) }

    val outlineForegroundColor: Color get() = foregroundColor
    val outlineBackgroundColor: Color get() = foregroundColor.transparentize(.2)
    val outlineBorderColor: Color get() = foregroundColor
    val solidForegroundColor: Color get() = Color(0xffffff)
    val solidBackgroundColor: Color get() = backgroundColor.toHSL().coerceAtMost(lightness = 67.0)
    val solidBorderColor: Color get() = solidBackgroundColor
}
