@file:UseSerializers(DateAsMillisecondsSerializer::class, DurationAsMillisecondsSerializer::class, UrlSerializer::class)

package com.clickup.api

import com.bkahlert.kommons.Color
import com.bkahlert.kommons.serialization.DateAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.DurationAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.UrlSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlin.js.Date

@Serializable
data class TaskList(
    @SerialName("id") val id: TaskListID,
    @SerialName("name") val name: String,
    @SerialName("orderindex") val orderIndex: Int,
    @SerialName("content") val content: String?,
    @SerialName("status") val status: TaskListStatus?,
    @SerialName("priority") val priority: TaskListPriority?,
    @SerialName("assignee") val assignee: Assignee?,
    @SerialName("task_count") val taskCount: Int?,
    @SerialName("due_date") val dueDate: Date?,
    @SerialName("start_dare") val startDate: Date?,
    @SerialName("folder") val folder: FolderPreview?,
    @SerialName("space") val space: SpacePreview,
    @SerialName("archived") val archived: Boolean,
    @SerialName("override_statuses") val overrideStatuses: Boolean?,
    @SerialName("permission_level") val permissionLevel: String,
) {
    fun asPreview(): TaskListPreview = TaskListPreview(id = id, name = name, access = true)
}

@Serializable value class TaskListID(override val id: String) : Identifier<String>

@Serializable
data class TaskListStatus(
    @SerialName("status") val status: String,
    @SerialName("color") val color: Color,
    @SerialName("hide_label") val hideLabel: Boolean = false,
)

@Serializable
data class TaskListPriority(
    @SerialName("priority") val priority: String,
    @SerialName("color") val color: Color,
)

@Serializable
data class TaskListPreview(
    @SerialName("id") val id: TaskListID,
    @SerialName("name") val name: String,
    @SerialName("access") val access: Boolean,
)
