@file:UseSerializers(DateAsMillisecondsSerializer::class, DurationAsMillisecondsSerializer::class, UrlSerializer::class)

package com.clickup.api

import com.bkahlert.kommons.Color
import com.bkahlert.kommons.serialization.DateAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.DurationAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.UrlSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class Status(
    @SerialName("id") val id: StatusID,
    @SerialName("status") val status: String,
    @SerialName("color") val color: Color,
    @SerialName("orderindex") val orderIndex: Int,
    @SerialName("type") val type: String,
)

@Serializable value class StatusID(override val id: String) : Identifier<String>

@Serializable
data class StatusPreview(
    @SerialName("status") val status: String,
    @SerialName("color") val color: Color,
    @SerialName("orderindex") val orderIndex: Int,
    @SerialName("type") val type: String,
)
