@file:UseSerializers(DateAsMillisecondsSerializer::class, UrlSerializer::class)

package com.bkahlert.hello.clickup

import com.bkahlert.hello.clickup.rest.Identifier
import com.bkahlert.kommons.serialization.DateAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.UrlSerializer
import io.ktor.http.Url
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlin.js.Date

@Serializable
data class Task(
    @SerialName("id") val id: ID,
    @SerialName("custom_id") val customId: String?,
    @SerialName("name") val name: String,
    @SerialName("text_content") val textContent: String?,
    @SerialName("description") val description: String?,
    @SerialName("status") val status: Status.Preview,
    @SerialName("orderindex") val orderIndex: Double?,
    @SerialName("date_created") val dateCreated: Date?,
    @SerialName("date_updated") val dateUpdated: Date?,
    @SerialName("date_closed") val dateClosed: String?,
    @SerialName("creator") val creator: Creator,
    @SerialName("assignees") val assignees: List<Assignee>,
    @SerialName("watchers") val watchers: List<Watcher>,
    @SerialName("checklists") val checklists: List<CheckList>,
    @SerialName("tags") val tags: List<Tag>,
    @SerialName("parent") val parent: ID?,
    @SerialName("priority") val priority: Priority?,
    @SerialName("due_date") val dueDate: Date?,
    @SerialName("start_date") val startDate: Date?,
    @SerialName("points") val points: Double?,
    @SerialName("time_estimate") val timeEstimate: Double?,
    @SerialName("custom_fields") val customFields: List<CustomField>,
    @SerialName("dependencies") val dependencies: List<String>,
    @SerialName("linked_tasks") val linkedTasks: List<Link>,
    @SerialName("team_id") val teamId: Team.ID?,
    @SerialName("url") val url: Url?,
    @SerialName("permission_level") val permissionLevel: String?,
    @SerialName("list") val list: ClickupList.Preview?,
    @SerialName("folder") val folder: Folder.Preview,
    @SerialName("space") val space: Space.Preview,
) {
    @Serializable value class ID(override val id: String) : Identifier<String>

    @Serializable
    data class Preview(
        @SerialName("id") val id: ID,
        @SerialName("name") val name: String,
        @SerialName("status") val status: Status.Preview,
        @SerialName("custom_type") val customType: String?,
    )

    @Serializable
    enum class Priority {
        @SerialName("1") Urgent,
        @SerialName("2") High,
        @SerialName("3") Normal,
        @SerialName("4") Low,
    }

    @Serializable
    data class Link(
        @SerialName("link_id") val id: ID,
        @SerialName("task_id") val taskId: Task.ID,
        @SerialName("date_created") val date_created: Date,
        @SerialName("userid") val userId: User.ID,
    ) {
        @Serializable value class ID(override val id: String) : Identifier<String>
    }
}
