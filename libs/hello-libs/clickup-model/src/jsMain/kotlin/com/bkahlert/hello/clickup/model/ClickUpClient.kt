package com.bkahlert.hello.clickup.model

import kotlinx.datetime.Instant

public interface ClickUpClient {

    public suspend fun getUser(): User

    public suspend fun getTeams(): List<Team>

    public suspend fun createTask(
        listId: TaskListID,
        name: String,
    ): Task

    public suspend fun getTasks(
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
        due_date_gt: Instant? = null,
        due_date_lt: Instant? = null,
        date_created_gt: Instant? = null,
        date_created_lt: Instant? = null,
        date_updated_gt: Instant? = null,
        date_updated_lt: Instant? = null,
        custom_fields: List<CustomFieldFilter>? = null,
    ): List<Task>

    public suspend fun getTask(
        taskId: TaskID,
    ): Task?

    public suspend fun getPossibleStatuses(
        task: Task,
    ): List<Status> = getFolder(task.folder.id).statuses

    public suspend fun updateTask(
        task: Task,
    ): Task

    public suspend fun getSpaces(
        team: Team,
        archived: Boolean = false,
    ): List<Space>

    public suspend fun getLists(
        space: Space,
        archived: Boolean = false,
    ): List<TaskList>

    public suspend fun getFolders(
        space: Space,
        archived: Boolean = false,
    ): List<Folder>

    public suspend fun getFolder(
        folderId: FolderID,
    ): Folder

    public suspend fun getLists(
        folder: Folder,
        archived: Boolean = false,
    ): List<TaskList>

    public suspend fun getTimeEntry(
        team: Team,
        timeEntryID: TimeEntryID,
    ): TimeEntry?

    public suspend fun getRunningTimeEntry(
        team: Team,
        assignee: User?,
    ): TimeEntry?

    public suspend fun startTimeEntry(
        team: Team,
        taskId: TaskID? = null,
        description: String? = null,
        billable: Boolean = false,
        vararg tags: Tag,
    ): TimeEntry

    public suspend fun stopTimeEntry(
        team: Team,
    ): TimeEntry?

    public suspend fun addTagsToTimeEntries(
        team: Team,
        timeEntryIDs: List<TimeEntryID>,
        tags: List<Tag>,
    )
}
