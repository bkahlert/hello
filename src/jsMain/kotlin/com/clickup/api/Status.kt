@file:UseSerializers(DateAsMillisecondsSerializer::class, DurationAsMillisecondsSerializer::class, UrlSerializer::class)

package com.clickup.api

import com.bkahlert.kommons.color.Color
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
) {
    fun asPreview() = StatusPreview(
        status = status,
        color = color,
        orderIndex = orderIndex,
        type = type,
    )
}

/** Whether this status represents an open status. */
val Status.open: Boolean get() = type.equals("open", ignoreCase = true)

/** Whether this status represents a custom status. */
val Status.custom: Boolean get() = type.equals("custom", ignoreCase = true)

/** Whether this status represents a closed status. */
val Status.closed: Boolean get() = type.equals("closed", ignoreCase = true)

/** Whether this status represents an open status. */
val StatusPreview.open: Boolean get() = type.equals("open", ignoreCase = true)

/** Whether this status represents a custom status. */
val StatusPreview.custom: Boolean get() = type.equals("custom", ignoreCase = true)

/** Whether this status represents a closed status. */
val StatusPreview.closed: Boolean get() = type.equals("closed", ignoreCase = true)

/** The first [Status.open] status in this status collection. */
val Iterable<Status>.open: Status get() = first { it.open }

/** All [Status.custom] statuses in this status collection. */
val Iterable<Status>.custom: List<Status> get() = filter { it.custom }

/** The first [Status.closed] status in this status collection. */
val Iterable<Status>.closed: Status get() = first { it.closed }

@Serializable value class StatusID(override val id: String) : Identifier<String>

@Serializable
data class StatusPreview(
    @SerialName("status") val status: String,
    @SerialName("color") val color: Color,
    @SerialName("orderindex") val orderIndex: Int,
    @SerialName("type") val type: String,
)
