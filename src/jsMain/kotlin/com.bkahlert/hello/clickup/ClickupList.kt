@file:UseSerializers(DateAsMillisecondsSerializer::class, UrlSerializer::class)

package com.bkahlert.hello.clickup

import com.bkahlert.kommons.Color
import com.bkahlert.kommons.serialization.DateAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.UrlSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlin.js.Date

@Serializable
data class ClickupList(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("orderindex") val orderIndex: Int,
    @SerialName("content") val content: String?,
    @SerialName("status") val status: Status,
    @SerialName("priority") val priority: Priority?,
    @SerialName("assignee") val assignee: Assignee?,
    @SerialName("task_count") val taskCount: Int?,
    @SerialName("due_date") val dueDate: Date?,
    @SerialName("start_dare") val startDate: Date?,
    @SerialName("folder") val folder: Folder.Preview,
    @SerialName("space") val space: Space.Preview,
    @SerialName("archived") val archived: Boolean,
    @SerialName("override_statuses") val overrideStatuses: Boolean,
    @SerialName("permission_level") val permissionLevel: String,
) {
    @Serializable
    data class Status(
        @SerialName("status") val status: String,
        @SerialName("color") val color: Color,
        @SerialName("hide_label") val hideLabel: Boolean = false,
    )

    @Serializable
    data class Priority(
        @SerialName("priority") val priority: String,
        @SerialName("color") val color: Color,
    )

    @Serializable
    data class Preview(
        @SerialName("id") val id: Int,
        @SerialName("name") val name: String,
        @SerialName("access") val access: Boolean,
    )
}
