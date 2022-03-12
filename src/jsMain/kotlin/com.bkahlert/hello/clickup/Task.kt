@file:UseSerializers(DateAsMillisecondsSerializer::class, UrlSerializer::class)

package com.bkahlert.hello.clickup

import com.bkahlert.kommons.serialization.DateAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.UrlSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlin.js.Date

@Serializable
data class Task(
    @SerialName("id") val id: String,
    @SerialName("custom_id") val customId: String?,
    @SerialName("name") val name: String,
    @SerialName("text_content") val textContent: String?,
    @SerialName("description") val description: String?,
    @SerialName("status") val status: Status.Preview,
    @SerialName("orderindex") val orderIndex: String,
    @SerialName("date_created") val dateCreated: Date?,
    @SerialName("date_updated") val dateUpdated: Date?,
    @SerialName("date_closed") val dateClosed: String?,
    @SerialName("creator") val creator: Creator,
    @SerialName("assignees") val assignees: List<Assignee>,
    @SerialName("watchers") val watchers: List<Watcher>,
    @SerialName("checklists") val checklists: List<CheckList>,
    @SerialName("tags") val tags: List<Tag>,
    @SerialName("parent") val parent: String?,
    @SerialName("priority") val priority: Int?,
    @SerialName("due_date") val dueDate: Date?,
    @SerialName("start_date") val startDate: Date?,
    @SerialName("points") val points: Double?,
    @SerialName("time_estimate") val timeEstimate: Double?,
    @SerialName("custom_fields") val customFields: List<CustomField>,
    @SerialName("dependencies") val dependencies: List<String>,
    @SerialName("linked_tasks") val linkedTasks: List<String>,
    @SerialName("team_id") val teamId: String?,
    @SerialName("url") val url: String?,
    @SerialName("permission_level") val permissionLevel: String?,
    @SerialName("list") val list: ClickupList.Preview?,
    @SerialName("folder") val folder: Folder.Preview,
    @SerialName("space") val space: Space.Preview,
) {

    @Serializable
    data class Preview(
        @SerialName("id") val id: String,
        @SerialName("name") val name: String,
        @SerialName("status") val status: Status.Preview,
        @SerialName("custom_type") val customType: String?,
    )
}
