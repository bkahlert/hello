@file:UseSerializers(DateAsMillisecondsSerializer::class, DurationAsMillisecondsSerializer::class, UrlSerializer::class)

package com.bkahlert.hello.clickup.api.rest

import com.bkahlert.hello.clickup.api.Folder
import com.bkahlert.hello.clickup.api.FolderID
import com.bkahlert.hello.clickup.api.Space
import com.bkahlert.hello.clickup.api.SpaceID
import com.bkahlert.hello.clickup.api.Status
import com.bkahlert.hello.clickup.api.Tag
import com.bkahlert.hello.clickup.api.Task
import com.bkahlert.hello.clickup.api.TaskID
import com.bkahlert.hello.clickup.api.TaskList
import com.bkahlert.hello.clickup.api.TaskListID
import com.bkahlert.hello.clickup.api.Team
import com.bkahlert.hello.clickup.api.TimeEntry
import com.bkahlert.hello.clickup.api.TimeEntryID
import com.bkahlert.hello.clickup.api.User
import com.bkahlert.hello.url.UrlSerializer
import com.bkahlert.kommons.serialization.DateAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.DurationAsMillisecondsSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlin.js.Date

interface ClickUpClient {
    suspend fun getUser(): User

    suspend fun getTeams(): List<Team>

    suspend fun createTask(
        listId: TaskListID,
        task: CreateTaskRequest,
    ): Task

    suspend fun getTasks(
        team: Team,
        page: Int? = null,
        order_by: String? = null,
        reverse: Boolean? = null,
        subtasks: Boolean? = null,
        space_ids: List<SpaceID>? = null,
        project_ids: List<FolderID>? = null,
        list_ids: List<TaskListID>? = null,
        statuses: List<String>? = null,
        include_closed: Boolean? = null,
        assignees: List<String>? = null,
        tags: List<String>? = null,
        due_date_gt: Date? = null,
        due_date_lt: Date? = null,
        date_created_gt: Date? = null,
        date_created_lt: Date? = null,
        date_updated_gt: Date? = null,
        date_updated_lt: Date? = null,
        custom_fields: List<CustomFieldFilter>? = null,
    ): List<Task>

    suspend fun getTask(
        taskId: TaskID,
    ): Task?

    suspend fun getPossibleStatuses(
        task: Task,
    ): List<Status> = getFolder(task.folder.id).statuses

    suspend fun updateTask(
        task: Task,
    ): Task

    suspend fun getSpaces(
        team: Team,
        archived: Boolean = false,
    ): List<Space>

    suspend fun getLists(
        space: Space,
        archived: Boolean = false,
    ): List<TaskList>

    suspend fun getFolders(
        space: Space,
        archived: Boolean = false,
    ): List<Folder>

    suspend fun getFolder(
        folderId: FolderID,
    ): Folder

    suspend fun getLists(
        folder: Folder,
        archived: Boolean = false,
    ): List<TaskList>

    suspend fun getTimeEntry(
        team: Team,
        timeEntryID: TimeEntryID,
    ): TimeEntry?

    suspend fun getRunningTimeEntry(
        team: Team,
        assignee: User?,
    ): TimeEntry?

    suspend fun startTimeEntry(
        team: Team,
        taskId: TaskID? = null,
        description: String? = null,
        billable: Boolean = false,
        vararg tags: Tag,
    ): TimeEntry

    suspend fun stopTimeEntry(
        team: Team,
    ): TimeEntry?

    suspend fun addTagsToTimeEntries(
        team: Team,
        timeEntryIDs: List<TimeEntryID>,
        tags: List<Tag>,
    )
}

@Serializable
data class CreateTaskRequest(
    @SerialName("name") val name: String,
)

@Serializable
data class UpdateTaskRequest(
    @SerialName("status") val status: String?,
)

@Serializable
data class StartTimeEntryRequest(
    val tid: TaskID?,
    val description: String?,
    val billable: Boolean,
    val tags: List<Tag>,
)
