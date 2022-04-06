package com.clickup.api.rest

import com.clickup.api.Folder
import com.clickup.api.FolderID
import com.clickup.api.Space
import com.clickup.api.SpaceID
import com.clickup.api.Tag
import com.clickup.api.Task
import com.clickup.api.TaskID
import com.clickup.api.TaskList
import com.clickup.api.TaskListID
import com.clickup.api.Team
import com.clickup.api.TimeEntry
import com.clickup.api.TimeEntryID
import com.clickup.api.User
import kotlin.js.Date

interface ClickUpClient {
    suspend fun getUser(): User

    suspend fun getTeams(): List<Team>

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
    ): Unit
}
