package com.bkahlert.hello.clickup.model

import com.bkahlert.hello.clickup.model.fixtures.ClickUpFixtures
import com.bkahlert.hello.clickup.serialization.SerializerTest
import com.bkahlert.hello.color.Color
import kotlin.js.Date

@Suppress("unused")
class TaskListTest : SerializerTest<TaskList>(
    TaskList.serializer(),
    // language=JSON
    """
        {
            "id": "25510969",
            "name": "Professional",
            "orderindex": 0,
            "status": {
                "status": "professional",
                "color": "#02bcd4"
            },
            "assignee": {
                "id": 11111,
                "color": "#ff0000",
                "username": "john.doe",
                "initials": "JD",
                "profilePicture": "${ClickUpFixtures.User.profilePicture}"
            },
            "task_count": 45,
            "folder": {
                "id": "11087491",
                "name": "hidden",
                "hidden": true,
                "access": true
            },
            "space": {
                "id": "1",
                "name": "Personal",
                "access": true
            },
            "archived": false,
            "override_statuses": false,
            "permission_level": "create"
        }
    """.trimIndent() to taskList()
)


fun taskList(
    id: TaskListID = TaskListID("25510969"),
    name: String = "Professional",
    orderIndex: Int = 0,
    content: String? = null,
    status: TaskListStatus = TaskListStatus("professional", Color("#02BCD4")),
    priority: TaskListPriority? = null,
    assignee: Assignee = ClickUpFixtures.User.asAssignee(),
    taskCount: Int = 45,
    dueDate: Date? = null,
    startDate: Date? = null,
    folder: FolderPreview = FolderPreview(
        id = FolderID("11087491"),
        name = "hidden",
        hidden = true,
        access = true
    ),
    space: SpacePreview = SpacePreview(
        SpaceID("1"),
        "Personal",
        true
    ),
    archived: Boolean = false,
    overrideStatuses: Boolean = false,
    permissionLevel: String = "create",
) = TaskList(
    id,
    name,
    orderIndex,
    content,
    status,
    priority,
    assignee,
    taskCount,
    dueDate,
    startDate,
    folder,
    space,
    archived,
    overrideStatuses,
    permissionLevel,
)
