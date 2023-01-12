package com.bkahlert.hello.clickup.model

import com.bkahlert.hello.clickup.serialization.DateAsMilliseconds
import com.bkahlert.hello.color.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TaskList(
    @SerialName("id") val id: TaskListID,
    @SerialName("name") val name: String,
    @SerialName("orderindex") val orderIndex: Int,
    @SerialName("content") val content: String?,
    @SerialName("status") val status: TaskListStatus?,
    @SerialName("priority") val priority: TaskListPriority?,
    @SerialName("assignee") val assignee: Assignee?,
    @SerialName("task_count") val taskCount: Int?,
    @SerialName("due_date") val dueDate: DateAsMilliseconds?,
    @SerialName("start_dare") val startDate: DateAsMilliseconds?,
    @SerialName("folder") val folder: FolderPreview?,
    @SerialName("space") val space: SpacePreview?,
    @SerialName("archived") val archived: Boolean,
    @SerialName("override_statuses") val overrideStatuses: Boolean?,
    @SerialName("permission_level") val permissionLevel: String,
) {
    public fun asPreview(): TaskListPreview = TaskListPreview(id = id, name = name, access = true)
}

@Serializable public value class TaskListID(override val id: String) : Identifier<String>

@Serializable
public data class TaskListStatus(
    @SerialName("status") val status: String,
    @SerialName("color") val color: Color,
    @SerialName("hide_label") val hideLabel: Boolean = false,
)

@Serializable
public data class TaskListPriority(
    @SerialName("priority") val priority: String,
    @SerialName("color") val color: Color,
)

@Serializable
public data class TaskListPreview(
    @SerialName("id") val id: TaskListID,
    @SerialName("name") val name: String,
    @SerialName("access") val access: Boolean,
)
