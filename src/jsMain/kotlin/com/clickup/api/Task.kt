@file:UseSerializers(DateAsMillisecondsSerializer::class, DurationAsMillisecondsSerializer::class, UrlSerializer::class)

package com.clickup.api

import com.bkahlert.kommons.asString
import com.bkahlert.kommons.dom.URL
import com.bkahlert.kommons.serialization.DateAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.DurationAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.UrlSerializer
import com.bkahlert.kommons.time.Now
import com.bkahlert.kommons.time.compareTo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlin.js.Date
import kotlin.time.Duration

@Serializable
data class Task(
    @SerialName("id") val id: TaskID,
    @SerialName("custom_id") val customId: String?,
    @SerialName("name") val name: String,
    @SerialName("text_content") val textContent: String?,
    @SerialName("description") val description: String?,
    @SerialName("status") val status: StatusPreview,
    @SerialName("orderindex") val orderIndex: Double?,
    @SerialName("date_created") val dateCreated: Date?,
    @SerialName("date_updated") val dateUpdated: Date?,
    @SerialName("date_closed") val dateClosed: String?,
    @SerialName("creator") val creator: Creator,
    @SerialName("assignees") val assignees: List<Assignee>,
    @SerialName("watchers") val watchers: List<Watcher>,
    @SerialName("checklists") val checklists: List<CheckList>,
    @SerialName("tags") val tags: List<Tag>,
    @SerialName("parent") val parent: TaskID?,
    @SerialName("priority") val priority: TaskPriority?,
    @SerialName("due_date") val dueDate: Date?,
    @SerialName("start_date") val startDate: Date?,
    @SerialName("points") val points: Double?,
    @SerialName("time_estimate") val timeEstimate: Duration?,
    @SerialName("time_spent") val timeSpent: Duration?,
    @SerialName("custom_fields") val customFields: List<CustomField>,
    @SerialName("dependencies") val dependencies: List<String>,
    @SerialName("linked_tasks") val linkedTasks: List<TaskLink>,
    @SerialName("team_id") val teamId: TeamID?,
    @SerialName("url") val url: URL?,
    @SerialName("permission_level") val permissionLevel: String?,
    @SerialName("list") val list: TaskListPreview?,
    @SerialName("folder") val folder: FolderPreview,
    @SerialName("space") val space: SpacePreview,
) {
    val overdue: Boolean? get() = dueDate?.let { Now > it }
    fun asPreview() = TaskPreview(
        id = id,
        name = name,
        status = status,
        customType = null,
    )

    override fun toString(): String = asString {
        ::id.name to id
        ::customId.name to customId
        ::name.name to name
        ::textContent.name to textContent
        ::description.name to description
        ::status.name to status
        ::orderIndex.name to orderIndex
        ::dateCreated.name to dateCreated
        ::dateUpdated.name to dateUpdated
        ::dateClosed.name to dateClosed
        ::creator.name to creator
        ::assignees.name to assignees
        ::watchers.name to watchers
        ::checklists.name to checklists
        ::tags.name to tags
        ::parent.name to parent
        ::priority.name to priority
        ::dueDate.name to dueDate
        ::startDate.name to startDate
        ::points.name to points
        ::timeEstimate.name to timeEstimate
        ::timeSpent.name to timeSpent
        ::customFields.name to customFields
        ::dependencies.name to dependencies
        ::linkedTasks.name to linkedTasks
        ::teamId.name to teamId
        ::url.name to url
        ::permissionLevel.name to permissionLevel
        ::list.name to list
        ::folder.name to folder
        ::space.name to space
    }
}

@Serializable value class TaskID(override val id: String) : Identifier<String>

@Serializable
data class TaskPreview(
    @SerialName("id") val id: TaskID,
    @SerialName("name") val name: String,
    @SerialName("status") val status: StatusPreview,
    @SerialName("custom_type") val customType: String?,
)

@Serializable
enum class TaskPriority {
    @SerialName("1") Urgent,
    @SerialName("2") High,
    @SerialName("3") Normal,
    @SerialName("4") Low,
}

@Serializable
data class TaskLink(
    @SerialName("link_id") val id: TaskLinkID,
    @SerialName("task_id") val taskId: TaskID,
    @SerialName("date_created") val date_created: Date,
    @SerialName("userid") val userId: UserID,
)

@Serializable value class TaskLinkID(override val id: String) : Identifier<String>
