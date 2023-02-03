package com.bkahlert.hello.clickup.model

import com.bkahlert.hello.clickup.serialization.DateAsMilliseconds
import com.bkahlert.hello.clickup.serialization.DurationAsMilliseconds
import com.bkahlert.kommons.compareTo
import com.bkahlert.kommons.uri.Uri
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.Date

@Serializable
public data class Task(
    @SerialName("id") val id: TaskID,
    @SerialName("custom_id") val customId: String?,
    @SerialName("name") val name: String,
    @SerialName("text_content") val textContent: String?,
    @SerialName("description") val description: String?,
    @SerialName("status") val status: StatusPreview,
    @SerialName("orderindex") val orderIndex: Double?,
    @SerialName("date_created") val dateCreated: DateAsMilliseconds?,
    @SerialName("date_updated") val dateUpdated: DateAsMilliseconds?,
    @SerialName("date_closed") val dateClosed: DateAsMilliseconds?,
    @SerialName("creator") val creator: Creator,
    @SerialName("assignees") val assignees: List<Assignee>,
    @SerialName("watchers") val watchers: List<Watcher>,
    @SerialName("checklists") val checklists: List<CheckList>,
    @SerialName("tags") val tags: List<Tag>,
    @SerialName("parent") val parent: TaskID?,
    @SerialName("priority") val priority: TaskPriority?,
    @SerialName("due_date") val dueDate: DateAsMilliseconds?,
    @SerialName("start_date") val startDate: DateAsMilliseconds?,
    @SerialName("points") val points: Double?,
    @SerialName("time_estimate") val timeEstimate: DurationAsMilliseconds?,
    @SerialName("time_spent") val timeSpent: DurationAsMilliseconds?,
    @SerialName("custom_fields") val customFields: List<CustomField>,
    @SerialName("dependencies") val dependencies: List<String>,
    @SerialName("linked_tasks") val linkedTasks: List<TaskLink>,
    @SerialName("team_id") val teamId: TeamID?,
    @SerialName("url") val url: Uri?,
    @SerialName("permission_level") val permissionLevel: String?,
    @SerialName("list") val list: TaskListPreview?,
    @SerialName("folder") val folder: FolderPreview,
    @SerialName("space") val space: SpacePreview,
) {
    val overdue: Boolean? get() = dueDate?.let { Date() > it }
    public fun asPreview(): TaskPreview = TaskPreview(
        id = id,
        name = name,
        status = status,
        customType = null,
    )
}

@Serializable public value class TaskID(override val id: String) : Identifier<String>

@Serializable
public data class TaskPreview(
    @SerialName("id") val id: TaskID,
    @SerialName("name") val name: String,
    @SerialName("status") val status: StatusPreview,
    @SerialName("custom_type") val customType: String?,
)

@Serializable
public enum class TaskPriority {
    @SerialName("1") Urgent,
    @SerialName("2") High,
    @SerialName("3") Normal,
    @SerialName("4") Low,
}

@Serializable
public data class TaskLink(
    @SerialName("link_id") val id: TaskLinkID,
    @SerialName("task_id") val taskId: TaskID,
    @SerialName("date_created") val date_created: DateAsMilliseconds,
    @SerialName("userid") val userId: UserID,
)

@Serializable public value class TaskLinkID(override val id: String) : Identifier<String>
