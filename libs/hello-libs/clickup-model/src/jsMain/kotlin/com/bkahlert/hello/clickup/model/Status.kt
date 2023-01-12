package com.bkahlert.hello.clickup.model

import com.bkahlert.hello.color.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Status(
    @SerialName("id") val id: StatusID,
    @SerialName("status") val status: String,
    @SerialName("color") val color: Color,
    @SerialName("orderindex") val orderIndex: Int,
    @SerialName("type") val type: String,
) {
    public fun asPreview(): StatusPreview = StatusPreview(
        status = status,
        color = color,
        orderIndex = orderIndex,
        type = type,
    )
}

/** Whether this status represents an open status. */
public val Status.open: Boolean get() = type.equals("open", ignoreCase = true)

/** Whether this status represents a custom status. */
public val Status.custom: Boolean get() = type.equals("custom", ignoreCase = true)

/** Whether this status represents a closed status. */
public val Status.closed: Boolean get() = type.equals("closed", ignoreCase = true)

/** Whether this status represents an open status. */
public val StatusPreview.open: Boolean get() = type.equals("open", ignoreCase = true)

/** Whether this status represents a custom status. */
public val StatusPreview.custom: Boolean get() = type.equals("custom", ignoreCase = true)

/** Whether this status represents a closed status. */
public val StatusPreview.closed: Boolean get() = type.equals("closed", ignoreCase = true)

/** The first [Status.open] status in this status collection. */
public val Iterable<Status>.open: Status get() = first { it.open }

/** All [Status.custom] statuses in this status collection. */
public val Iterable<Status>.custom: List<Status> get() = filter { it.custom }

/** The first [Status.closed] status in this status collection. */
public val Iterable<Status>.closed: Status get() = first { it.closed }

@Serializable public value class StatusID(override val id: String) : Identifier<String>

@Serializable
public data class StatusPreview(
    @SerialName("status") val status: String,
    @SerialName("color") val color: Color,
    @SerialName("orderindex") val orderIndex: Int,
    @SerialName("type") val type: String,
)
