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
        put(::id.name, id)
        put(::customId.name, customId)
        put(::name.name, name)
        put(::textContent.name, textContent)
        put(::description.name, description)
        put(::status.name, status)
        put(::orderIndex.name, orderIndex)
        put(::dateCreated.name, dateCreated)
        put(::dateUpdated.name, dateUpdated)
        put(::dateClosed.name, dateClosed)
        put(::creator.name, creator)
        put(::assignees.name, assignees)
        put(::watchers.name, watchers)
        put(::checklists.name, checklists)
        put(::tags.name, tags)
        put(::parent.name, parent)
        put(::priority.name, priority)
        put(::dueDate.name, dueDate)
        put(::startDate.name, startDate)
        put(::points.name, points)
        put(::timeEstimate.name, timeEstimate)
        put(::timeSpent.name, timeSpent)
        put(::customFields.name, customFields)
        put(::dependencies.name, dependencies)
        put(::linkedTasks.name, linkedTasks)
        put(::teamId.name, teamId)
        put(::url.name, url)
        put(::permissionLevel.name, permissionLevel)
        put(::list.name, list)
        put(::folder.name, folder)
        put(::space.name, space)
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
